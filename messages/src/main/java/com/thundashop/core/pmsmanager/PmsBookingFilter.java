package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsBookingFilter extends DataCommon {

    public static class PmsBookingFilterTypes {
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
    
    public String startDateAsText;
    public String endDateAsText;
    
    
    public String filterType;
    public String searchWord;
    public Boolean needToBeConfirmed = false;
    public Boolean includeDeleted = false;
    public Boolean onlyUntransferredToBookingCom = false;
    public Boolean groupByBooking = false;
    public String sorting;
    public String userId;
    public String channel;
    public String interval;
    public String timeInterval;
    public boolean includeCleaningInformation = false;
    public boolean includeVirtual = false;
    public boolean removeClosedRooms = false;
    public List<String> typeFilter = new ArrayList();
    public List<String> itemFilter = new ArrayList();
    public String filterName = "";
}
