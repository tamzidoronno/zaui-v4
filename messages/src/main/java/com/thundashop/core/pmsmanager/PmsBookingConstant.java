package com.thundashop.core.pmsmanager;

public class PmsBookingConstant {
    public static class PriceType {
        public static Integer daily = 1;
        public static Integer monthly = 2;
        public static Integer weekly = 3;
        public static Integer minutly = 4;
        public static Integer hourly = 5;
        public static Integer secondly = 6;
        public static Integer progressive = 7;
        public static Integer interval = 8;
    }
    public static class BookingStates {
        public static Integer STARTED = 0;
        public static Integer COMPLETED = 1;
        public static Integer DELETED = 2;
    }
}
