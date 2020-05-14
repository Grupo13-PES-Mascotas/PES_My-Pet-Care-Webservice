package org.pesmypetcare.webservice.entity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UTCLocalConverter {
    public static String convertUTCtoLocal(String datein) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = sdf.parse(datein);
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(date);
    }

    public static String convertLocaltoUTC(String datein) throws ParseException{
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        TimeZone tz = TimeZone.getDefault();
        sdf.setTimeZone(TimeZone.getDefault());
        Date date = sdf.parse(datein);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }

    public static String getCurrentUTC(){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
        Date date2 = new Date();
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date2);
    }
}
