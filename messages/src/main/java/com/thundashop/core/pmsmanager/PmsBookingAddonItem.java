package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TranslationHandler;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

public class PmsBookingAddonItem extends TranslationHandler implements Serializable {
public static class AddonTypes {
        public static Integer BREAKFAST = 1;
        public static Integer PARKING = 2;
        public static Integer LATECHECKOUT = 3;
        public static Integer EARLYCHECKIN = 4;
        public static Integer EXTRABED = 5;
        public static Integer CANCELLATION = 6;
        public static Integer EXTRACHILDBED = 7;
    }
    
    public String addonId = UUID.randomUUID().toString();
    public Date date;
    public Double price;
    public Double priceExTaxes;
    public String productId;
    public Integer addonType;
    public String addonSubType = "";
    public Integer count = 1;
    public boolean isActive = false;
    public boolean isSingle = false;
    public boolean noRefundable = false;
    public boolean isAvailableForBooking = false;
    public boolean isAvailableForCleaner = false;
    public boolean dependsOnGuestCount = false;
    public boolean isIncludedInRoomPrice = false;
    public String channelManagerAddonText = "";
    public List<String> includedInBookingItemTypes = new ArrayList();
    public String addedBy;
    public List<PmsBookingAddonItemValidDateRange> validDates = new ArrayList();
    public boolean atEndOfStay;
    
    Map<String, String> variations = new HashMap();
    String description;

    
    @Translation
    public String descriptionWeb = "";
    
    @Transient
    public String name = "";
    boolean addedToRoom = false;

    
    public boolean isValidForPeriode(Date start, Date end, Date regDate) {
        if(validDates == null || validDates.isEmpty()) {
            return true;
        }
        
        for(PmsBookingAddonItemValidDateRange range : validDates) {
            if(range.containsPeriode(start, end, regDate)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    boolean isSame(PmsBookingAddonItem item) {
        if(item.productId.equals(productId)) {
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            cal1.setTime(item.date);
            cal2.setTime(date);
            boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                              cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
            if(sameDay) {
                return true;
            }
        }
        return false;
    }


    
}