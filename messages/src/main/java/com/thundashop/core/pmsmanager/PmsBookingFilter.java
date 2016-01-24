package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsBookingFilter implements Serializable {
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
    public List<String> typeFilter = new ArrayList();
}
