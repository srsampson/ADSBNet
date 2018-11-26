/**
 * UnicastTrackBuilder.java
 */
package adsnet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class sends track objects to remote Participant Units (PU). It only
 * sends tracks which have been updated during a configured time setting.
 *
 * <p>
 * The track and heartbeat are sent over the WAN using UDP IP packets on the
 * configured IP port.
 *
 * @author Steve Sampson, May 2010
 */
public final class UnicastTrackBuilder extends Thread {

    private static final long RATE = 29000L;		// 29 seconds
    //
    private final Thread track;
    //
    private final KineticParse process;
    //
    private final DatagramSocket socket;
    //
    private final ZuluMillis zulu;
    private final Config config;
    //
    private final Timer timer;
    private final TimerTask task;
    //
    private HostID[] host;
    private final int hostCount;
    private int unicastPort;
    private long homeID;
    private long unicastTime;
    private double homeLat;
    private double homeLon;
    private String homeName;
    private boolean shutdown;

    public UnicastTrackBuilder(Config c, DatagramSocket ds, KineticParse proc) {
        this.config = c;
        this.socket = ds;
        this.process = proc;
        this.zulu = new ZuluMillis();

        task = new HeartBeat();
        timer = new Timer();
        shutdown = false;

        this.hostCount = config.getUnicastHostCount();

        track = new Thread(this);

        if (hostCount > 0) {
            this.unicastTime = 0L;
            this.host = config.getUnicastHosts();
            this.unicastPort = config.getUnicastPort();
            this.homeID = config.getHomeID();
            this.unicastTime = config.getUnicastTime(); // 1 sec default
            this.homeLat = config.getHomeLat();
            this.homeLon = config.getHomeLon();
            this.homeName = config.getHomeName();

            track.setName("UnicastTrackBuilder");
            track.setPriority(Thread.NORM_PRIORITY);
            track.start();

            timer.scheduleAtFixedRate(task, 0, RATE);
        }
    }

    public void close() {
        timer.cancel();
        shutdown = true;
    }

    private synchronized void sendWAN(String data, InetAddress ip) {
        try {
            byte[] buffer = data.getBytes("US-ASCII");
            socket.send(new DatagramPacket(buffer, buffer.length, ip, unicastPort));
        } catch (IOException e) {
            // Punt
        }
    }

    @Override
    public void run() {
        Timestamp sqlTime = new Timestamp(0L);
        CopyOnWriteArrayList<Track> values;
        InetAddress ip;
        String acid;
        long now, last;
        boolean localOnly;

        last = zulu.getUTCTime();

        while (shutdown == false) {
            now = zulu.getUTCTime();

            if (process.getWANQueueSize() > 0) {
                if ((now - last) > unicastTime) {   // 1 second by default
                    last = now;

                    values = process.getWANQueueTable();

                    for (int i = 0; i < hostCount; i++) {
                        try {
                            ip = InetAddress.getByName(host[i].getHostName());
                            if (ip != host[i].getIPAddress()) {
                                host[i].setIPAddress(ip);
                            }

                            localOnly = host[i].getLocalOnly();

                            for (Track id : values) {
                                acid = id.getAircraftID();
                                process.removeWANQueueVal(acid);

                                if (localOnly && (id.getTrackType() != Track.TRACK_LOCAL)) {
                                    continue;
                                }

                                sqlTime.setTime(id.getDetectedTime());

                                sendWAN(String.format("TRK,%d,%s,%s,%s,%s,%d,%.1f,%.1f,%d,%s,%s,%s,%s,%s,%s,%d\r\n",
                                        homeID,
                                        sqlTime.toString(),
                                        acid,
                                        (id.getCallsign() == null) ? "" : id.getCallsign(),
                                        (id.getSquawk() == 0) ? "" : String.format("%04d", id.getSquawk()),
                                        id.getVerticalRate(),
                                        id.getGroundTrack(),
                                        id.getGroundSpeed(),
                                        id.getAltitude(),
                                        (id.getLatitude() == 0.0D) ? "" : String.format("%f", id.getLatitude()),
                                        (id.getLongitude() == 0.0D) ? "" : String.format("%f", id.getLongitude()),
                                        (id.getAlert() == true) ? "-1" : "0",
                                        (id.getEmergency() == true) ? "-1" : "0",
                                        (id.getSPI() == true) ? "-1" : "0",
                                        (id.getOnGround() == true) ? "-1" : "0",
                                        id.getTrackQuality()), ip);
                            }
                        } catch (UnknownHostException e) {
                            // Punt
                        }
                    }
                }
            }

            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
            }
        }
    }


    /*
     * This will send a packet to show we are still alive
     */
    private class HeartBeat extends TimerTask {

        private final Timestamp HBTime = new Timestamp(0L);
        private InetAddress ip;

        @Override
        public void run() {
            HBTime.setTime(zulu.getUTCTime());

            for (int i = 0; i < hostCount; i++) {
                try {
                    ip = InetAddress.getByName(host[i].getHostName());

                    if (ip != host[i].getIPAddress()) {
                        host[i].setIPAddress(ip);
                    }

                    sendWAN(String.format("STA,%d,%s,%s,%f,%f,%d\r\n",
                            homeID,
                            HBTime.toString(),
                            homeName,
                            homeLat,
                            homeLon,
                            process.getTrackReportsSize()), ip);
                } catch (UnknownHostException e) {
                    // Punt
                }
            }

            Thread.yield();
        }
    }
}
