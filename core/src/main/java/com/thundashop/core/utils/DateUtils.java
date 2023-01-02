package com.thundashop.core.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String getCurrentTime() {
        return DateTimeFormatter.ofPattern("HH:mm").format(LocalDateTime.now().atZone(ZoneId.systemDefault()));
    }

    public static Date getCorrectCheckOutDate(Date start, Date end) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(start);

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(end);
        endCal.set(Calendar.DAY_OF_YEAR, startCal.get(Calendar.DAY_OF_YEAR) + 1);
        endCal.set(Calendar.YEAR, startCal.get(Calendar.YEAR));
        return endCal.getTime();
    }
}
