package main.bankmanagement.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // Convert String to java.sql.Date
    public static Date parseDate(String dateStr) {
        try {
            java.util.Date parsed = DATE_FORMAT.parse(dateStr);
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Convert String to java.sql.Timestamp
    public static Timestamp parseTimestamp(String timestampStr) {
        try {
            java.util.Date parsed = DATETIME_FORMAT.parse(timestampStr);
            return new Timestamp(parsed.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Format java.sql.Date to String
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
    
    // Format java.sql.Timestamp to String
    public static String formatTimestamp(Timestamp timestamp) {
        if (timestamp == null) return "";
        return DATETIME_FORMAT.format(timestamp);
    }
    
    // Get current date as java.sql.Date
    public static Date getCurrentDate() {
        return Date.valueOf(LocalDate.now());
    }
    
    // Get current timestamp as java.sql.Timestamp
    public static Timestamp getCurrentTimestamp() {
        return Timestamp.valueOf(LocalDateTime.now());
    }
    
    // Check if a string is a valid date
    public static boolean isValidDate(String dateStr) {
        try {
            DATE_FORMAT.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    // Check if a string is a valid timestamp
    public static boolean isValidTimestamp(String timestampStr) {
        try {
            DATETIME_FORMAT.parse(timestampStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}