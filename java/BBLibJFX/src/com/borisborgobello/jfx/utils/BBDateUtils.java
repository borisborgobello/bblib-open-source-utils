package com.borisborgobello.jfx.utils;

import static com.borisborgobello.jfx.utils.BBTools.isEmpty;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BBDateUtils {
    private static final String JACKSON_ISO_CLASS = "com.fasterxml.jackson.databind.util.ISO8601DateFormat";
    
    // Date formats
    public static final SimpleDateFormat FDATE_APPLE_ALT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss");
    public static final SimpleDateFormat FDATE_ISO8601_ALT = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ssZZZZZZ");
    public static final SimpleDateFormat FDATE_YYYY_MM_DD = new SimpleDateFormat(
            "yyyy-MM-dd");
    public static final SimpleDateFormat FDATE_TIME1 = new SimpleDateFormat(
            "HH:mm:ss.SSS");
    public static final SimpleDateFormat FDATE_LOGSTYLE = new SimpleDateFormat(
            "yyyy-MM-dd'_'HH-mm-ss");
    public static final SimpleDateFormat FDATE_DATETIME_USER_FRIENDLY = new SimpleDateFormat(
            "dd/MM/yy HH:mm:ss");
    public static final DateFormat FDATE_ISO8601; // Jackson dependency
    static {
        DateFormat tmp = null;
        try { tmp = (DateFormat) Class.forName(JACKSON_ISO_CLASS).newInstance(); } 
        catch (Throwable ex) { Logger.getLogger(BBDateUtils.class.getName()).log(Level.SEVERE, null, "ISO8601 is unavailable without jackson databind"); }
        FDATE_ISO8601 = tmp;
    }
    public static final SimpleDateFormat FDATE_TIMETRACK = new SimpleDateFormat(
            "dd/MM/yy HH:mm:ss");
    public static final SimpleDateFormat FDATE_TIMETRACK_PAUSE = new SimpleDateFormat(
            "HH:mm:ss");

    // Comparators
    public static final Comparator<String> COMPARATOR_USER_DATE = (String o1, String o2) -> {
        try {
            return FDATE_DATETIME_USER_FRIENDLY.parse(o1).compareTo(FDATE_DATETIME_USER_FRIENDLY.parse(o2));
        } catch (ParseException ex) {
            Logger.getLogger(BBDateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    };
    public static final Comparator<String> COMPARATOR_BIRTHDAY_DATE = (String o1, String o2) -> {
        try {
            return FDATE_YYYY_MM_DD.parse(o1).compareTo(FDATE_YYYY_MM_DD.parse(o2));
        } catch (ParseException ex) {
            Logger.getLogger(BBDateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    };

    public static final Comparator<String> COMPARATOR_ISO_DATE = (String o1, String o2) -> {
        try {
            return FDATE_ISO8601.parse(o1).compareTo(FDATE_ISO8601.parse(o2));
        } catch (ParseException ex) {
            Logger.getLogger(BBDateUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    };
    public static final Comparator<String> COMPARATOR_RAILS_TIMESTAMP = COMPARATOR_ISO_DATE; // ALIAS
    
    public static final Comparator<String> LOG_DATE_COMPARATOR = (String s1, String s2) -> {
        if (isEmpty(s1) && isEmpty(s2)) {
            return 0;
        }
        if (isEmpty(s1)) {
            return 1;
        }
        if (isEmpty(s2)) {
            return -1;
        }
        Date d1 = null, d2 = null;
        try {
            d1 = BBDateUtils.FDATE_LOGSTYLE.parse(s1);
        } catch (Exception e) {
        }
        try {
            d2 = BBDateUtils.FDATE_LOGSTYLE.parse(s2);
        } catch (Exception e) {
        }
        if (d1 == null && d2 == null) {
            return 0;
        }
        if (d1 == null) {
            return 1;
        }
        if (d2 == null) {
            return -1;
        }
        return d1.compareTo(d2);
    };

    private static final StringBuilder PRIV_SBUILDER_FAST;
    private static final Formatter PRIV_FORMAT_FAST;
    static {
        PRIV_SBUILDER_FAST = new StringBuilder();
        PRIV_FORMAT_FAST = new Formatter(PRIV_SBUILDER_FAST, Locale.getDefault());
    }
    
    // Util functions
    public static Date asDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    // Date to string, silently fails and returns empty string
    public static final String dateUserForISODate(String d) {
        try {
            return FDATE_DATETIME_USER_FRIENDLY.format(FDATE_ISO8601.parse(d));
        } catch (Exception e) {
            return "";
        }
    }

    // Current date to string
    public static final String newISODate() {
        return FDATE_ISO8601.format(new Date());
    }
    
    // Fast but not thread safe ! Use on UI Thread only
    public static String stringForTime(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = totalSeconds / 3600;

        PRIV_SBUILDER_FAST.setLength(0);
        if (hours > 0) {
            return PRIV_FORMAT_FAST.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return PRIV_FORMAT_FAST.format("%02d:%02d", minutes, seconds).toString();
        }
    }
    // Fast but not thread safe ! Use on UI Thread only
    public static String stringForTime2(long timeMs) {
        long totalSeconds = timeMs / 1000;

        long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600) % 24;
        long days = totalSeconds / (3600 * 24);

        PRIV_SBUILDER_FAST.setLength(0);
        if (days > 0) {
            return PRIV_FORMAT_FAST.format("%dd %dh %dm", days, hours, minutes).toString();
        } else if (hours > 0) {
            return PRIV_FORMAT_FAST.format("%dh %dm", hours, minutes).toString();
        } else {
            return PRIV_FORMAT_FAST.format("%dm", minutes).toString();
        }
    }
    // Fast but not thread safe ! Use on UI Thread only
    public static String stringForTime3(long timeMs) {
        long totalSeconds = timeMs / 1000;

        //long seconds = totalSeconds % 60;
        long minutes = (totalSeconds / 60) % 60;
        long hours = (totalSeconds / 3600) % 24;
        long days = totalSeconds / (3600 * 24);

        PRIV_SBUILDER_FAST.setLength(0);
        if (days > 0) {
            return PRIV_FORMAT_FAST.format("%dd%dh%dm", days, hours, minutes).toString();
        } else if (hours > 0) {
            return PRIV_FORMAT_FAST.format("%dh%dm", hours, minutes).toString();
        } else {
            return PRIV_FORMAT_FAST.format("%dm", minutes).toString();
        }
    }
}
