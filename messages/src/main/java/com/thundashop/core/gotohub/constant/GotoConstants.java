package com.thundashop.core.gotohub.constant;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;

public class GotoConstants {
    public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    public static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final SimpleDateFormat checkinOutDateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DAILY_PRICE_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat cancellationDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static String GOTO_BOOKING_CHANNEL_NAME = "goto";
    public static int DEFAULT_AUTO_DELETION_TIME_FOR_GOTO_BOOKING_IN_MINUTE = 90;
    public static String GOTO_PAYMENT= "GOTO_PAYMENT";
    public static String STAY_PAYMENT= "STAY_PAYMENT";
}
