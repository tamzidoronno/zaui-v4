package com.thundashop.core.pmsmanager;

import java.util.Date;

public class PmsBookingFilter {
    static class PmsBookingFilterTypes {
        public static String registered = "registered";
        public static String active = "active";
        public static String uncofirmed = "uncofirmed";
        public static String checkin = "checkin";
        public static String checkout = "checkout";
        public static String deleted = "deleted";
        public static String stats = "stats";
    }
    public Integer state;
    public Date startDate;
    public Date endDate;
    public String filterType;
    public String searchWord;
    public Boolean needToBeConfirmed = false;
    public Boolean includeDeleted = false;
    public String sorting;
}
