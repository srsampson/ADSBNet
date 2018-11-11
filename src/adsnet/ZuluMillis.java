/**
 * ZuluMillis.java
 */
package adsnet;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class ZuluMillis {

    private static final Calendar cal = new GregorianCalendar();

    /**
     * Method to return the current UTC time
     *
     * @return a long Representing the UTC time.
     */
    public long getUTCTime() {
        cal.setTimeInMillis(System.currentTimeMillis());

        return cal.getTimeInMillis()
                - cal.get(Calendar.ZONE_OFFSET)
                - cal.get(Calendar.DST_OFFSET);
    }
}