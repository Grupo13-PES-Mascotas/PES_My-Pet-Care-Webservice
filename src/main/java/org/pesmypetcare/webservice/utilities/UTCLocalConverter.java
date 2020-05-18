package org.pesmypetcare.webservice.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author Álvaro Trius
 */
public class UTCLocalConverter {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String UTC = "UTC";

    /**
     * This method forbids creators, since this class shouldn't be instantiated in the first place.
     */
    private UTCLocalConverter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Converts the specified date as a String from UTC to Local.
     * @param dateIn the String to be converted
     * @return a String with the dateIn converted to local timezone
     * @throws ParseException constructs a ParseException with the specified detail message and offset
     */
    public static String convertUTCtoLocal(String dateIn) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone(UTC));
        Date date = sdf.parse(dateIn);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    /**
     * Converts the specified date as a String from local timezone to UTC.
     * @param dateIn the String to be converted
     * @return a String with the dateIn converted to UTC timezone
     * @throws ParseException constructs a ParseException with the specified detail message and offset
     */
    public static String convertLocalToUTC(String dateIn) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = sdf.parse(dateIn);
        sdf.setTimeZone(TimeZone.getTimeZone(UTC));
        return sdf.format(date);
    }

    /**
     * Returns the current date as in the UTC timezone and in YYYY:MM:DD'T'HH:MM:SS format.
     * @return the current date in UTC timezone as a String
     */
    public static String getCurrentUTC() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        Date date2 = new Date();
        sdf.setTimeZone(TimeZone.getTimeZone(UTC));
        return sdf.format(date2);
    }
}
