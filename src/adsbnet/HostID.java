/**
 * HostID.java
 */
package adsbnet;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class to store remote unicast host information
 * 
 * @author Steve Sampson, January 2020
 */
public final class HostID {

    private final String hostName;
    private final boolean localOnly;
    private InetAddress ipAddress;

    /**
     * Class Constructor with a variable and a boolean
     *
     * @param host a string Representing the hostname/IP to connect to
     * @param local a boolean Representing whether to send local only tracks
     */
    public HostID(String host, boolean local) {
        try {
            this.ipAddress = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            this.ipAddress = (InetAddress) null;
        }

        this.hostName = host;
        this.localOnly = local;
    }

    public boolean getLocalOnly() {
        return this.localOnly;
    }

    public String getHostName() {
        return this.hostName;
    }

    public InetAddress getIPAddress() {
        return this.ipAddress;
    }

    /**
     * Method to update the IP address if needed
     *
     * @param val InetAddress representing the updated PU IP Address
     */
    public void setIPAddress(InetAddress val) {
        this.ipAddress = val;
    }
}
