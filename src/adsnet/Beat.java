/**
 * Beat.java
 */
package adsnet;

/**
 *  Class to store a network received Participant Unit (PU) Heartbeat
 *
 *  @author Steve Sampson, May 2010
 */
public class Beat {

    private final ZuluMillis zulu = new ZuluMillis();
    private final long stationID;         // Station ID
    private final double stationLat;      // Station Latitude
    private final double stationLon;      // Station Longitude
    private final long diffTime;          // UTC difference between local and remote
    //
    private String stationName;           // Station Name
    private String stationIP;             // Station IP Address
    private long track_count;             // Number of tracks the station holds
    private long beat_count;              // Number of heartbeats heard
    private long updateUTCTime;           // time object was updated

    public Beat(String val1, long val2, double val3, double val4, long val5, long val6) {
        this.stationName = val1;
        this.stationID = val2;
        this.stationLat = val3;
        this.stationLon = val4;
        this.diffTime = val5;
        //
        this.updateUTCTime = zulu.getUTCTime();
        this.stationIP = "";
        this.track_count = val6;
        this.beat_count = 0L;
    }

    public long getStationID() {
        return this.stationID;
    }

    public long getUpdateTime() {
        return this.updateUTCTime;
    }

    public void setUpdateTime(long val) {
        this.updateUTCTime = val;
    }

    public long getDiffTime() {
        return this.diffTime;
    }

    public double getStationLat() {
        return this.stationLat;
    }

    public double getStationLon() {
        return this.stationLon;
    }

    public String getStationName() {
        return this.stationName;
    }

    public void setStationName(String val) {
        this.stationName = val;
    }
    
    public String getStationIP() {
        return this.stationIP;
    }

    public void setStationIP(String val) {
        this.stationIP = val;
    }

    public long getTrackCount() {
        return this.track_count;
    }

    public void setTrackCount(long val) {
        this.track_count = val;
    }
    
    public long getBeatCount() {
        return this.beat_count;
    }

    public void clearBeatCount() {
        this.beat_count = 0L;
    }

    public void incrementBeatCount() {
        this.beat_count++;
    }

    public void deccrementBeatCount() {
        if (this.beat_count > 0L) {
            this.beat_count--;
        }
    }
}
