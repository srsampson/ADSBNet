/**
 * ADSNet.java
 */
package adsnet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Locale;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Automatic Dependent Surveillance (ADS-B) Track Networker
 *
 * <p>
 * This software listens for target data on the Kinetic compatible socket and
 * builds tracks which are then sent out on specified unicast WAN hosts, and all
 * multicast LAN hosts.
 *
 * The networker uses UDP broadcasts for all transmitted tracks.
 *
 * @version 1.83
 * @author Steve Sampson, November 2018
 */
public final class Main {

    private static DatagramSocket ds;
    private static MulticastSocket ms;
    //
    private static KineticParse con;
    //
    private static String config = "adsnet.conf";
    private static Config c;
    //
    private static BufferedWriter logwriter = (BufferedWriter) null;

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

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {
            // Take whatever you get then
        }

        c = new Config(config);
        System.out.println("Using config file: " + c.getOSConfPath());

        if (c.getMulticastLog() == true) {
            try {
                File logfile = new File(c.getHomeDir() + "adsnet.log");

                if (!logfile.exists()) {
                    logfile.createNewFile();
                }

                logwriter = new BufferedWriter(new FileWriter(logfile, true));
            } catch (IOException e) {
                System.out.println("Unable to create or open logfile!");
            }
        }

        con = new KineticParse(c, logwriter);

        /*
         * Start the network broadcast ports
         */
        try {
            ds = new DatagramSocket();
            ds.setSoTimeout(800);
            ms = new MulticastSocket(c.getMulticastPort());
            ms.setInterface(InetAddress.getByName(c.getMulticastNIC()));
            ms.joinGroup(InetAddress.getByName(c.getMulticastHost()));
            ms.setSoTimeout(800);
            ms.setTimeToLive(3);   // I chose three in case you have a couple routers in your LAN/TUNNEL
        } catch (IOException e) {
            System.err.println("main: unable to open and set network interfaces");
            System.exit(-1);
        }

        try {
            new MulticastTrackBuilder(c, ms, con, logwriter);
            new UnicastTrackBuilder(c, ds, con);
        } catch (Exception e) {
            System.exit(-1);
        }
    }
}
