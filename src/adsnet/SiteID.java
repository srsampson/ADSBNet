/**
 * SiteID.java
 */
package adsnet;

import java.util.Random;

/**
 * Class to store a Participant Unit (PU) unique Global ID
 *
 * <p>Each network participant needs a unique random ID to share tracks
 *
 * @author Steve Sampson, May 2010
 */
public final class SiteID {

    protected long siteID;
    protected String siteName;

    public SiteID(String name) {
        Random rnd = new Random(System.currentTimeMillis());
        this.siteName = name;
        this.siteID = rnd.nextLong();        // 64 bit random number
    }

    /**
     * Method to return a random Site ID
     *
     * @return a long Representing a random Site ID
     */
    public long getSiteID() {
        return this.siteID;
    }

    /**
     * Method to return the Site Name from the configuration file
     *
     * @return a string Representing the Site Name
     */
    public String getSiteName() {
        return this.siteName;
    }

    /**
     * Method to set the Site ID to a user chosen value
     *
     * @param val a long Representing the Site ID
     */
    public void setSiteID(long val) {
        this.siteID = val;
    }

    /**
     * Method to set the Site Name to an user chosen value
     *
     * @param val a string Representing the Site Name
     */
    public void setSiteName(String val) {
        this.siteName = val;
    }
}
