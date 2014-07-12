package hu.flexisys.kbr.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Peter on 2014.07.03..
 */
public class DateUtil {

    public static final String requestIdFormat = "yyMMdd-HHmmss";
    public static final String timestampFormat = "yyyy.MM.dd HH:mm:ss";
    public static final String dateFormat = "yyyy.MM.dd";

    public static String getRequestId() {
        return new SimpleDateFormat(requestIdFormat).format(new Date());
    }

    public static Date getDateFromTimestampString(String timestampString) throws ParseException {
        return new SimpleDateFormat(timestampFormat).parse(timestampString);
    }

    public static Date getDateFromDateString(String dateString) throws ParseException {
        return new SimpleDateFormat(dateFormat).parse(dateString);
    }

    public static String formatDate(Date date) {
        return new SimpleDateFormat(dateFormat).format(date);
    }
}
