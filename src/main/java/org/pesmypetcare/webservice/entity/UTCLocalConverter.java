package org.pesmypetcare.webservice.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

class UTCLocalConverter {

    private static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String UTC = "UTC";

    public static String convertUTCtoLocal(String datein) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone(UTC));
        Date date = sdf.parse(datein);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static String convertLocaltoUTC(String datein) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = sdf.parse(datein);
        sdf.setTimeZone(TimeZone.getTimeZone(UTC));
        return sdf.format(date);
    }

    public static String getCurrentUTC() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        Date date2 = new Date();
        sdf.setTimeZone(TimeZone.getTimeZone(UTC));
        return sdf.format(date2);
    }
}
