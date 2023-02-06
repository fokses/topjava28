package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");

    private DateUtil() {}

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        if(localDateTime == null) {
            return "";
        }
        return formatter.format(localDateTime);
    }

    public static String toString(LocalDateTime dateTime) {
        if(dateTime == null) {
            return null;
        }

        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public static LocalDateTime fromString(String str) {
        if (str == null)
            return null;

        return LocalDateTime.parse(str, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
