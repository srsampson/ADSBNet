/*
 * Shutdown.java
 */
package adsbnet;

public final class Shutdown extends Thread {

    private final KineticParse con;
    private final MulticastTrackBuilder mtrack;
    private final ZerotierTrackBuilder ztrack;
    private final UnicastTrackBuilder utrack;

    public Shutdown(KineticParse c, MulticastTrackBuilder m, ZerotierTrackBuilder z, UnicastTrackBuilder u) {
        con = c;
        mtrack = m;
        ztrack = z;
        utrack = u;
    }

    @Override
    public void run() {
        System.out.println("Shutdown started");
        
        con.close();
        mtrack.close();
        ztrack.close();
        utrack.close();
        System.runFinalization();
    }
}