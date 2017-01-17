package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TranslationHandler;
import java.io.Serializable;
import java.util.ArrayList;
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
    public double price;
    public double priceExTaxes;
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
    List<String> includedInBookingItemTypes = new ArrayList();
    public String addedBy;
    
    Map<String, String> variations = new HashMap();
    String description;

    
    @Translation
    public String descriptionWeb = "";
    
    @Transient
    public String name = "";
}