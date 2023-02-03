package ru.javawebinar.topjava.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy hh:mm");

    private DateUtil() {}

    public static String formatLocalDateTime(LocalDateTime localDateTime) {
        return formatter.format(localDateTime);
    }
}
