/**
 * MulticastTrackBuilder.java
 */
package adsnet;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.sql.Timestamp;
import java.util.List;

/**
 * This class is a thread that outputs track objects which have been updated to
 * the local area network (LAN).
 *
 * @author Steve Sampson, May 2010
 */
public final class MulticastTrackBuilder extends Thread {

    private final Thread track;
    //
    private final KineticParse process;
    //
    private final Config config;
    //
    private final MulticastSocket msocket;
    private final BufferedWriter logwriter;

    public MulticastTrackBuilder(Config c, MulticastSocket ms, KineticParse proc, BufferedWriter log) {
        this.config = c;
        this.process = proc;
        this.msocket = ms;
        this.logwriter = log;   // might be null

        track = new Thread(this);
        track.setName("MulticastTrackBuilder");
        track.setPriority(Thread.NORM_PRIORITY);
        track.start();
    }

    private void sendLAN(String data) {
        /*
         * Send copy to a buffered log file
         */
        if (config.getMulticastLog() == true) {
            if (logwriter != null) {
                try {
                    logwriter.write(data);
                } catch (IOException e) {
                    System.err.println("MulticastTrackBuilder::send Couldn't write to log file " + e.toString());
                }
            }
        }

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
        List<Track> values;
        String acid;
        String trkstr;

        while (true) {
            values = process.getTrackUpdatedHashTable(); // remember, this is only a copy of the track queue

            if (values.size() > 0) {
                /*
                 * First clear the updated flag on all the tracks and put a copy in the WAN table
                 */

                for (Track id : values) {
                    acid = id.getAircraftID();
                    id.setUpdated(false);
                    process.putTrackReportsVal(acid, id); // write back to track queue to flip updated flag

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
            }

            try {
                Thread.sleep(50L);
            } catch (InterruptedException e) {
            }
        }
    }
}
