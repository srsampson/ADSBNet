/**
 * ADSBNet.java
 */
package adsbnet;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Automatic Dependent Surveillance (ADS-B) Track Feeder
 *
 * <p>
 * This software listens for target data on the Kinetic compatible socket and
 * builds tracks which are then sent out on specified networks
 *
 * Uses UDP broadcasts for all transmitted tracks.
 *
 * @version 1.90
 * @author Steve Sampson, January 2020
 */
public final class Main {

    private static DatagramSocket ds;
    private static MulticastSocket ms;
    private static MulticastSocket zs;
    //
    private static MulticastTrackBuilder mtrack;
    private static UnicastTrackBuilder utrack;
    private static KineticParse con;
    //
    private static String config = "adsbnet.conf";
    private static Config c;

    public static void main(String[] args) {
        /*
         * The user may have a commandline option as to which config file to use
         */

        try {
            if (args[0].equals("-c") || args[0].equals("/c")) {
                config = args[1];
            }
        } catch (Exception e) {
        }

        Locale.setDefault(Locale.US);

        c = new Config(config);
        System.out.println("Using config file: " + c.getOSConfPath());

        if (c.getGUIEnable() == true) {
            try {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
                // Take whatever you get then
            }
        }

        con = new KineticParse(c);

        /*
         * Start the network broadcast ports
         */
        try {
            ds = new DatagramSocket();
            ds.setSoTimeout(800);
        } catch (IOException e) {
            System.err.println("main: unable to open and set unicast interfaces");
            System.exit(-1);
        }
        
        try {
            ms = new MulticastSocket(c.getMulticastPort());
            ms.setInterface(InetAddress.getByName(c.getMulticastNIC()));
            ms.joinGroup(InetAddress.getByName(c.getMulticastHost()));
            ms.setSoTimeout(800);
            ms.setTimeToLive(3);
        } catch (IOException e) {
            System.err.println("main: unable to open and set multicast interfaces");
            System.exit(-1);
        }

        try {
            zs = new MulticastSocket(c.getZerotierPort());
            zs.setInterface(InetAddress.getByName(c.getZerotierNIC()));
            zs.joinGroup(InetAddress.getByName(c.getZerotierHost()));
            zs.setSoTimeout(800);
            zs.setTimeToLive(3);
        } catch (IOException e) {
            System.err.println("main: unable to open and set zerotier interfaces");
            System.exit(-1);
        }

        try {
            mtrack = new MulticastTrackBuilder(c, ms, zs, con);
            utrack = new UnicastTrackBuilder(c, ds, con);
        } catch (Exception e) {
            System.exit(-1);
        }
        
        Shutdown sh = new Shutdown(con, mtrack, utrack);
        Runtime.getRuntime().addShutdownHook(sh);
    }
}
