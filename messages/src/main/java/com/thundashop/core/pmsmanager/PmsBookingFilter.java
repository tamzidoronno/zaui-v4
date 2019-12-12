package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class PmsBookingFilter extends DataCommon {


    boolean containsCode(String couponCode) {
        if(couponCode == null) {
            return false;
        }
        for(String code : codes) {
            if(code.equalsIgnoreCase(couponCode)) {
                return true;
            }
        }
        return false;
    }

    boolean containsAddon(List<PmsBookingAddonItem> addonsToCheck) {
        if(addons == null) {
            return false;
        }
        
        for(String productId : addons) {
            for(PmsBookingAddonItem item2 : addonsToCheck) {
                if(item2.productId.equals(productId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void normalizeStartEndDateByBeginningEndOfDay() {
        Calendar cal = Calendar.getInstance();
        if(startDate != null) {
            cal.setTime(startDate);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startDate = cal.getTime();
        }
        if(endDate != null) {
            cal.setTime(endDate);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            endDate = cal.getTime();
        }
    }

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
    public String filterSubType;
    public String searchWord;
    public String bookingId;
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
    public boolean includeNonBookable = false;
    public boolean includeOrderStatistics = false;
    public boolean removeAddonsIncludedInRoomPrice = false;
    public boolean priceIncTaxes = false;
    public List<String> typeFilter = new ArrayList();
    public List<String> itemFilter = new ArrayList();
    public List<String> departmentIds = new ArrayList();
    public List<String> segments = new ArrayList();
    public String filterName = "";
    boolean fromPms = false;

    public List<String> customers = new ArrayList();
    public List<String> addons = new ArrayList();
    public List<String> codes = new ArrayList();
}
