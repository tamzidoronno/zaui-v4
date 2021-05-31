package com.thundashop.core.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getCurrentTime() {
        System.out.println("DateUtils.getCurrentTime: "+DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now().atZone(ZoneId.systemDefault())));
        return DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now().atZone(ZoneId.systemDefault()));
    }
}
