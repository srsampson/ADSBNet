/**
 * MulticastTrackBuilder.java
 */
package adsbnet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class is a thread that outputs track objects which have been updated to
 * the local area network (LAN).
 *
 * @author Steve Sampson, January 2020
 */
public final class MulticastTrackBuilder extends Thread {

    private final Thread track;
    //
    private final KineticParse process;
    private final ZuluMillis zulu;
    private final Config config;
    private final MulticastSocket msocket;
    //
    private boolean shutdown;

    public MulticastTrackBuilder(Config c, MulticastSocket ms, KineticParse proc) {
        this.config = c;
        this.process = proc;
        this.msocket = ms;
        this.zulu = new ZuluMillis();

        track = new Thread(this);
        track.setName("MulticastTrackBuilder");
        track.setPriority(Thread.NORM_PRIORITY);

        shutdown = false;
        track.start();
    }

    public void close() {
        shutdown = true;
    }

    private void sendLAN(String data) {
        try {
            byte[] buffer = data.getBytes("US-ASCII");
            msocket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(config.getMulticastHost()), config.getMulticastPort()));
        } catch (IOException e) {
            // Punt
        }
    }

    @Override
    public void run() {
        Timestamp sqlTime = new Timestamp(0L);
        CopyOnWriteArrayList<Track> values;
        String acid;
        String trkstr;
        long now;

        long last = zulu.getUTCTime();

        while (shutdown == false) {
            now = zulu.getUTCTime();
            values = process.getTrackMultiUpdatedHashTable(); // remember, this is only a copy of the track queue

            if (values.size() > 0) {
                for (Track id : values) {
                    acid = id.getAircraftID();

                    process.putWANQueueVal(acid, id);   // write to WAN users (unicast) (if any)

                    sqlTime.setTime(id.getDetectedTime());

                    trkstr = String.format("TRK,%d,%s,%s,%s,%s,%d,%.1f,%.1f,%d,%s,%s,%s,%s,%s,%s,%d\r\n",
                            id.getSiteID(),
                            sqlTime.toString(),
                            acid,
                            (id.getCallsign() == null) ? "" : id.getCallsign(),
                            (id.getSquawk() == 0) ? "" : String.format("%04d", id.getSquawk()),
                            id.getVerticalRate(),
                            id.getGroundTrack(),
                            id.getGroundSpeed(),
                            id.getAltitude(),
                            (id.getLatitude() == 0.0) ? "" : String.format("%f", id.getLatitude()),
                            (id.getLongitude() == 0.0) ? "" : String.format("%f", id.getLongitude()),
                            (id.getAlert() == true) ? "-1" : "0",
                            (id.getEmergency() == true) ? "-1" : "0",
                            (id.getSPI() == true) ? "-1" : "0",
                            (id.getOnGround() == true) ? "-1" : "0",
                            id.getTrackQuality());
                    sendLAN(trkstr);
                }

                try {
                    Thread.sleep(50L);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}
