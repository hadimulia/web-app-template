package app.spring.web.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {
    
    /**
     * Format LocalDateTime to string with the given pattern
     */
    public static String formatLocalDateTime(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return "";
        }
        
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return dateTime.format(formatter);
        } catch (Exception e) {
            return dateTime.toString();
        }
    }
    
    /**
     * Format LocalDateTime to default format (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        return formatLocalDateTime(dateTime, "yyyy-MM-dd HH:mm:ss");
    }
}
