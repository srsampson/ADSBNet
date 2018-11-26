/*
 * Shutdown.java
 */
package adsnet;

public final class Shutdown extends Thread {

    private final KineticParse con;
    private final MulticastTrackBuilder mtrack;
    private final UnicastTrackBuilder utrack;

    public Shutdown(KineticParse c, MulticastTrackBuilder m, UnicastTrackBuilder u) {
        con = c;
        mtrack = m;
        utrack = u;
    }

    @Override
    public void run() {
        System.out.println("Shutdown started");
        
        con.close();
        mtrack.close();
        utrack.close();
        System.runFinalization();
    }
}