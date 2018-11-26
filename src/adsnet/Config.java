/**
 * Config.java
 */
package adsnet;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * A Class to store configuration parameters from config file
 *
 * @author Steve Sampson, May 2010
 */
public final class Config {

    private final static String MULTICAST_ADDRESS = "239.192.10.90";    // I made it up (has 1090 in it)
    private final static int MULTICAST_PORT = 31090;                    // I made it up (has 1090 in it)
    private final static int UNICAST_PORT = 30339;
    private final static String SOCKET_ADDRESS = "127.0.0.1";
    private final static int SOCKET_PORT = 30003;
    //
    private int socketPort;
    private String socketIP;
    private String multicastIP;
    private int multicastPort;
    private String multicastNIC;
    private int unicastPort;
    private int unicastHostCount;
    private HostID[] unicastHost;
    private long unicastTime;
    private boolean multicastLog;
    private boolean guienable;
    //
    private Properties Props;
    private String homeName;
    private double homeLon;
    private double homeLat;
    private long homeID;
    private final String userDir;
    private final String fileSeparator;
    private String OSConfPath;
    private String homeDir;
    private SiteID SID;

    public Config(String val) {
        String temp;

        multicastIP = MULTICAST_ADDRESS;
        multicastPort = MULTICAST_PORT;
        //
        socketIP = SOCKET_ADDRESS;
        socketPort = SOCKET_PORT;
        //
        unicastPort = UNICAST_PORT;
        unicastHostCount = 0;
        unicastTime = 1000L;
        multicastLog = false;
        guienable = true;
        Props = null;
        homeName = "";
        homeLon = -97.0D;
        homeLat = 35.0D;
        homeID = 0L;
        SID = null;
        
        userDir = System.getProperty("user.dir");
        fileSeparator = System.getProperty("file.separator");
        OSConfPath = userDir + fileSeparator + val;
        homeDir = userDir + fileSeparator;

        try {
            FileInputStream in = new FileInputStream(OSConfPath);
            Props = new Properties();
            Props.load(in);
            in.close();
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        if (Props != null) {
            temp = Props.getProperty("multicast.nicaddress");
            if (temp == null) {
                multicastNIC = "127.0.0.1";
                System.out.println("multicast NIC not specified, using Loopback 127.0.0.1");
            } else {
                multicastNIC = temp.trim();
            }

            temp = Props.getProperty("multicast.address");
            if (temp == null) {
                multicastIP = MULTICAST_ADDRESS;
                System.out.println("socket address not set, set to " + MULTICAST_ADDRESS);
            } else {
                multicastIP = temp.trim();
            }

            temp = Props.getProperty("multicast.port");
            if (temp == null) {
                multicastPort = MULTICAST_PORT;
                System.out.println("socket port not set, set to " + String.valueOf(multicastPort));
            } else {
                try {
                    multicastPort = Integer.parseInt(temp.trim());
                } catch (NumberFormatException e) {
                    multicastPort = MULTICAST_PORT;
                }
            }

            temp = Props.getProperty("multicast.log");
            if (temp == null) {
                multicastLog = false;
                System.out.println("Logging not set, set to false");
            } else {
                try {
                    multicastLog = Boolean.parseBoolean(temp.trim());
                } catch (Exception e) {
                    multicastLog = false;
                }
            }

            temp = Props.getProperty("gui.enable");
            if (temp == null) {
                guienable = true;
                System.out.println("GUI enable not set, set to true");
            } else {
                try {
                    guienable = Boolean.parseBoolean(temp.trim());
                } catch (Exception e) {
                    guienable = true;
                }
            }
            
            temp = Props.getProperty("socket.address");
            if (temp == null) {
                socketIP = SOCKET_ADDRESS;
                System.out.println("socket address not set, set to " + SOCKET_ADDRESS);
            } else {
                socketIP = temp.trim();
            }

            temp = Props.getProperty("socket.port");
            if (temp == null) {
                socketPort = SOCKET_PORT;
                System.out.println("socket port not set, set to " + String.valueOf(socketPort));
            } else {
                try {
                    socketPort = Integer.parseInt(temp.trim());
                } catch (NumberFormatException e) {
                    socketPort = SOCKET_PORT;
                }
            }

            temp = Props.getProperty("unicast.address");
            if (temp == null) {
                unicastHostCount = 0;
            } else {
                String[] token = temp.split(",");   // Tokenize the data input line

                unicastHostCount = token.length;
                unicastHost = new HostID[token.length];

                for (int i = 0; i < unicastHostCount; i++) {
                    String[] hostPart = token[i].split(":"); // see if we have a nonlocal flag

                    if (hostPart.length != 1) {
                        if (hostPart[1].trim().toLowerCase().equals("y")) {
                            unicastHost[i] = new HostID(hostPart[0].trim(), false); // send all tracks
                        } else {
                            unicastHost[i] = new HostID(hostPart[0].trim(), true);   // send local only tracks
                        }
                    } else {
                        unicastHost[i] = new HostID(token[i].trim(), true);   // send local only tracks
                    }
                }
            }

            temp = Props.getProperty("unicast.port");
            if (temp != null) {
                try {
                    unicastPort = Integer.parseInt(temp.trim());
                } catch (NumberFormatException e) {
                    unicastPort = UNICAST_PORT;
                    System.err.println("server port (" + temp + ") invalid, set to " + String.valueOf(unicastPort));
                }
            }

            temp = Props.getProperty("unicast.seconds");
            if (temp != null) {
                try {
                    unicastTime = Math.abs(Long.parseLong(temp.trim()));

                    if (unicastTime == 0L) {
                        unicastTime = 1000L;
                    } else {
                        unicastTime *= 1000L;
                    }
                } catch (NumberFormatException e) {
                    unicastTime = 1000L;
                    System.err.println("server time (" + temp + ") invalid, set to 1 second");
                }
            }

            temp = Props.getProperty("station.latitude");
            if (temp == null) {
                homeLat = 0.0D;
                System.out.println("station.latitude not set");
            } else {
                try {
                    homeLat = Double.parseDouble(temp.trim());
                } catch (NumberFormatException e) {
                    homeLat = 0.0D;
                    System.err.println("station.latitude (" + temp + ") invalid");
                }
            }

            temp = Props.getProperty("station.longitude");
            if (temp == null) {
                homeLon = 0.0D;
                System.out.println("station.longitude not set");
            } else {
                try {
                    homeLon = Double.parseDouble(temp.trim());
                } catch (NumberFormatException e) {
                    homeLon = 0.0D;
                    System.err.println("station.longitude (" + temp + ") invalid");
                }
            }

            temp = Props.getProperty("station.name");
            if (temp == null) {
                System.out.println("station.name not set, set to Unknown");
                homeName = "Unknown";
            } else {
                homeName = temp.replace(',', ' ');
            }
        }

        SID = new SiteID(homeName);
        homeID = SID.getSiteID();
    }

    /**
     * Method to return the configuration home name
     *
     * @return a string Representing the city or site name of this receiver
     * location
     */
    public String getHomeName() {
        return homeName;
    }

    /**
     * Method to return the site latitude (north is positive)
     *
     * @return a double Representing the receiver site latitude
     */
    public double getHomeLat() {
        return homeLat;
    }

    /**
     * Method to return the site longitude (east is positive)
     *
     * @return a double Representing the receiver site longitude
     */
    public double getHomeLon() {
        return homeLon;
    }

    /**
     * Method to return the site UTC time in milliseconds
     *
     * @return a long Representing UTC time in milliseconds
     */
    public long getUnicastTime() {
        return unicastTime;
    }

    /**
     * Method to return the number of hosts to transmit UDP packets to
     *
     * @return an integer Representing the number of hosts to transmit to.
     */
    public int getUnicastHostCount() {
        return unicastHostCount;
    }

    public String getMulticastNIC() {
        return multicastNIC;
    }

    /**
     * Method to return the configuration multicast UDP port
     *
     * @return an integer Representing the multicast UDP port to transmit on
     */
    public int getMulticastPort() {
        return multicastPort;
    }

    /**
     * Method to return the configuration logging status
     *
     * @return a boolean Representing logging being requested
     */
    public boolean getMulticastLog() {
        return multicastLog;
    }

    /**
     * Method to return the configuration GUI status
     *
     * @return a boolean Representing GUI being requested
     */
    public boolean getGUIEnable() {
        return guienable;
    }
    
    /**
     * Method to return the configuration multicast host IP
     *
     * @return a string Representing the multicast host IP
     */
    public String getMulticastHost() {
        return multicastIP;
    }

    /**
     * Method to return the configuration unicast UDP port
     *
     * @return an integer Representing the unicast port
     */
    public int getUnicastPort() {
        return unicastPort;
    }

    /**
     * Method to return the configuration Basestation TCP port
     *
     * @return an integer Representing the Basestation TCP port
     */
    public int getSocketPort() {
        return socketPort;
    }

    /**
     * Method to return an array of HostID that contain the IP or Hostnames of
     * the target Participant Units (PU) to receive UDP unicasts
     *
     * @return a string array Representing target UDP hosts
     */
    public HostID[] getUnicastHosts() {
        return unicastHost;
    }

    /**
     * Method to return the Basestation IP or Hostname
     *
     * @return a string Representing the IP or Hostname of the Basestation host
     */
    public String getSocketHost() {
        return socketIP;
    }

    /**
     * Method to return the 64-bit Random Site ID of this broadcaster
     *
     * @return a long Representing the receiver Site ID
     */
    public long getHomeID() {
        return homeID;
    }

    /**
     * Method to return the filename path of the configuration file
     *
     * @return a string Representing the configuration file path
     */
    public String getOSConfPath() {
        return OSConfPath;
    }
    
    public String getHomeDir() {
        return homeDir;
    }
}
