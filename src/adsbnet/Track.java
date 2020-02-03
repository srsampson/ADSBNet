/**
 * Track.java
 */
package adsbnet;

/**
 * This is the vehicle track object
 *
 * @author Steve Sampson, January 2020
 */
public final class Track {

    public final static int TRACK_UNKNOWN = 0;
    public final static int TRACK_LOCAL = 1;
    public final static int TRACK_REMOTE = 2;
    //
    private final static int MIN_VERTICAL_RATE = 192;
    //
    private String acid;            // Aircraft ID
    private long siteID;            // Originating radar site ID
    private String siteIP;          // Originating radar site IP
    private int trackType;          // Local or Remote Track
    private int trackQuality;       // 0 - 9 quality value (9 means Firm)
    private int verticalRate;       // fps
    private double groundSpeed;     // kts
    private double groundTrack;     // deg
    private double latitude;        // aircraft position latitude (- is south)
    private double longitude;       // aircraft position longitude (- is west)
    private int altitude;           // aircraft current altitude in feet
    private String callsign;        // 8 character string
    private int squawk;             // 4 digit octal code
    //
    private boolean alert;          // octal code changed bit
    private boolean emergency;      // emergency bit
    private boolean spi;            // ident bit
    private boolean isOnGround;     // aircraft squat switch activated
    //
    private long updateTime;        // zulu time object was updated
    private long updatePositionTime;// zulu time object lat/lon position was updated
    private long detectedTime;      // zulu time object was locally detected
    private boolean updated;        // set on update, cleared on sent
    //
    private final ZuluMillis zulu;        // UTC time generator

    public Track() {
        this.zulu = new ZuluMillis();
        this.acid = "";
        this.siteID = 0L;
        this.siteIP = "";
        this.groundSpeed = 0.0;
        this.groundTrack = 0.0;
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.squawk = 0;
        this.verticalRate = 0;
        this.altitude = 0;
        this.callsign = "";
        this.trackType = TRACK_UNKNOWN; // Unknown, Local, Remote
        this.trackQuality = 0;
        this.updatePositionTime = this.detectedTime = 0L;
        this.updateTime = zulu.getUTCTime();
        this.alert = this.emergency = this.spi = this.isOnGround = false;
        this.updated = false;
    }

    /**
     * Method to return the site ID
     *
     * @return a long representing the site ID
     */
    public long getSiteID() {
        return this.siteID;
    }

    /**
     * Method to set the site ID
     *
     * @param val a long representing the site ID
     */
    public void setSiteID(long val) {
        this.siteID = val;
    }

    /**
     * Method to return the site IP
     *
     * @return a String representing the site IP
     */
    public String getSiteIP() {
        return this.siteIP;
    }

    /**
     * Method to set the site IP
     *
     * @param val a String representing the site IP
     */
    public void setSiteIP(String val) {
        this.siteIP = val;
    }

    /**
     * Method to set the track type to LOCAL or REMOTE
     *
     * @param val an integer Representing whether the track is a local track or
     * remote
     */
    public void setTrackType(int val) {
        this.trackType = val;
    }

    /**
     * Method to return track type (UNKNOWN, LOCAL, or REMOTE)
     *
     * @return integer representing whether the track is local or remote
     */
    public int getTrackType() {
        return this.trackType;
    }

    /**
     * Method to increment track quality
     */
    public void incrementTrackQuality() {
        if (this.trackQuality < 9) {
            this.trackQuality++;
            this.updated = true;
        }
    }

    /**
     * Method to decrement track quality
     */
    public void decrementTrackQuality() {
        if (this.trackQuality > 0) {
            this.trackQuality--;
            this.updated = true;
        }
    }

    /**
     * Method to set the track quality
     *
     * @param val an integer Representing the track quality [0...9]
     */
    public void setTrackQuality(int val) {
        this.trackQuality = val;
    }

    /**
     * Method to return track quality
     *
     * @return an integer representing the track quality [0...9]
     */
    public int getTrackQuality() {
        return this.trackQuality;
    }

    /**
     * Method to check if the track has been updated
     *
     * @return boolean which signals if the track has been updated
     */
    public boolean getUpdated() {
        return this.updated;
    }

    /**
     * Method to flag a track as being updated or not updated
     *
     * @param val a boolean to set or reset the track updated status
     */
    public void setUpdated(boolean val) {
        this.updated = val;
    }

    /**
     * Method to return the Aircraft Mode-S Hex ID
     *
     * @return a string Representing the track Mode-S Hex ID
     */
    public String getAircraftID() {
        return this.acid;
    }

    /**
     * Method to set the Aircraft Mode-S Hex ID
     *
     * @param val a string Representing the track Mode-S Hex ID
     */
    public void setAircraftID(String val) {
        this.acid = val;
    }

    /**
     * Method to return the tracks updated position time in UTC milliseconds
     *
     * @return a long Representing the track updated position time in UTC
     * milliseconds
     */
    public long getUpdatePositionTime() {
        return this.updatePositionTime;
    }

    /**
     * Method to set the tracks updated position time in UTC milliseconds
     *
     * @param val a long Representing the track updated position time in UTC
     * milliseconds
     */
    public void setUpdatePositionTime(long val) {
        this.updatePositionTime = val;
    }

    /**
     * Method to return the tracks updated time in UTC milliseconds
     *
     * @return a long Representing the track updated time in UTC milliseconds
     */
    public long getUpdateTime() {
        return this.updateTime;
    }

    /**
     * Method to set the track updated time in UTC milliseconds
     *
     * @param val a long Representing the track updated time in UTC milliseconds
     */
    public void setUpdateTime(long val) {
        this.updateTime = val;
    }

    /**
     * Method to return the tracks detected time in UTC milliseconds
     *
     * @return a long Representing the track detected time in UTC milliseconds
     */
    public long getDetectedTime() {
        return this.detectedTime;
    }

    /**
     * Method to set the track detected time in UTC milliseconds
     *
     * @param val a long Representing the track detected time in UTC
     * milliseconds
     */
    public void setDetectedTime(long val) {
        this.detectedTime = val;
    }

    /**
     * Method to return the track vertical rate in feet per second The
     * resolution is +/- 64 fps, with descent being negative
     *
     * @return an integer Representing the track climb or descent rate
     */
    public int getVerticalRate() {
        return this.verticalRate;
    }

    /**
     * Method to set the track vertical rate in feet per second The resolution
     * is +/- 64 fps, with descent being negative
     *
     * <p>
     * Values below the MIN_VERTICAL_RATE are reset to zero. This is to prevent
     * noise on the network as turbulence will cause the aircraft to bobble up
     * and down.
     *
     * @param val an integer Representing the track climb or descent rate
     */
    public void setVerticalRate(int val) {
        if (val == -999) {
            return;
        }

        int vs = 0;

        if (Math.abs(val) >= MIN_VERTICAL_RATE) {
            vs = val;
        }

        if (this.verticalRate != vs) {
            this.verticalRate = vs;
            this.updated = true;
        }
    }

    /**
     * Method used to return the target ground speed in knots (1 knot
     * resolution)
     *
     * @return a double Representing target groundspeed in knots
     */
    public double getGroundSpeed() {
        return this.groundSpeed;
    }

    public void setGroundSpeed(double val) {
        if (val == -999.0) {
            return;
        }

        this.groundSpeed = val;
        this.updated = true;
    }

    /**
     * Method used to return the target ground track in degrees true. (1 degree
     * resolution).
     *
     * @return a double Representing target ground track in degrees true
     */
    public double getGroundTrack() {
        return this.groundTrack;
    }

    public void setGroundTrack(double val) {
        if (val == -999.0) {
            return;
        }

        this.groundTrack = val;
        this.updated = true;
    }

    /**
     * Method used to set the target altitude in feet MSL (29.92)
     *
     * @param val an integer Representing altitude in feet MSL
     */
    public void setAltitude(int val) {
        if (val == -999) {
            return;
        }
        
        if (this.altitude != val) {
            this.altitude = val;
            this.updated = true;
        }
    }

    /**
     * Method used to return the target altitude in feet MSL (29.92)
     *
     * @return an integer Representing the target altitude in feet MSL
     */
    public int getAltitude() {
        return this.altitude;
    }

    /**
     * Method used to return the target latitude in degrees (south is negative)
     *
     * @return a double Representing the target latitude
     */
    public double getLatitude() {
        return this.latitude;
    }

    /**
     * Method used to return the target longitude in degrees (west is negative)
     *
     * @return a double Representing the target longitude
     */
    public double getLongitude() {
        return this.longitude;
    }

    /**
     * Method used to set the target 2D position (latitude, longitude) (south
     * and west are negative)
     *
     * @param val1 a double Representing the target latitude
     * @param val2 a double Representing the target longitude
     */
    public void setPosition(double val1, double val2) {
        boolean changed = false;

        if (this.latitude != val1) {
            this.latitude = val1;
            changed = true;
        }

        if (this.longitude != val2) {
            this.longitude = val2;
            changed = true;
        }

        if (changed) {
            incrementTrackQuality();
            this.updated = true;
            this.updatePositionTime = zulu.getUTCTime();
        }
    }

    /**
     * Method used to return the target callsign (8 characters maximum)
     *
     * @return a string Representing the target callsign
     */
    public String getCallsign() {
        return this.callsign;
    }

    /**
     * Method used to set the target callsign (synchronized)
     *
     * @param val a string Representing the target callsign
     */
    public void setCallsign(String val) {
        if (!val.equals(this.callsign)) {
            this.callsign = val;
            this.updated = true;
        }
    }

    /**
     * Method used to return the target octal 4-digit squawk
     *
     * @return an integer Representing the target octal squawk
     */
    public int getSquawk() {
        return this.squawk;
    }

    /**
     * Method used to set the target octal 4-digit squawk
     *
     * @param val an integer Representing the target octal squawk
     */
    public void setSquawk(int val) {
        if (val == -999) {
            return;
        }
        
        if (this.squawk != val) {
            this.squawk = val;
            this.updated = true;
        }
    }

    /**
     * Method used to return the status of the Emergency bit
     *
     * @return a boolean Representing the status of the Emergency bit
     */
    public boolean getEmergency() {
        return this.emergency;
    }

    /**
     * Method used to return the status of the Special Position Identifier bit
     * The SPI bit is also called the Ident, and is a button the pilots presses
     * at the controllers request, to identify their position.
     *
     * @return a boolean Representing the status of the SPI bit
     */
    public boolean getSPI() {
        return this.spi;
    }

    /**
     * Method used to return the status of the OnGround bit
     *
     * @return a boolean Representing the status of the OnGround bit
     */
    public boolean getOnGround() {
        return this.isOnGround;
    }

    /**
     * Method to set the status of the OnGround bit
     *
     * @param val a boolean Representing the status of the OnGround bit
     */
    public void setOnGround(boolean val) {
        if (this.isOnGround != val) {
            this.isOnGround = val;
            this.updated = true;
        }
    }

    /**
     * Method used to return the status of the Alert bit The Alert signals the
     * 4-digit octal squawk has changed
     *
     * @return a boolean Representing the status of the Alert bit
     */
    public boolean getAlert() {
        return this.alert;
    }

    /**
     * Method to set all the boolean bits
     *
     * <p>
     * The Alert bit is set if the 4-digit octal code is changed. The Emergency
     * bit is set if the pilot puts in the emergency code The SPI bit is set if
     * the pilots presses the Ident button.
     *
     * @param val1 a boolean Representing the status of the Alert bit
     * @param val2 a boolean Representing the status of the Emergency bit
     * @param val3 a boolean Representing the status of the SPI bit.
     */
    public void setAlert(boolean val1, boolean val2, boolean val3) {
        boolean changed = false;

        if (this.alert != val1) {
            this.alert = val1;
            changed = true;
        }

        if (this.emergency != val2) {
            this.emergency = val2;
            changed = true;
        }

        if (this.spi != val3) {
            this.spi = val3;
            changed = true;
        }

        if (changed) {
            this.updated = true;
        }
    }
}
