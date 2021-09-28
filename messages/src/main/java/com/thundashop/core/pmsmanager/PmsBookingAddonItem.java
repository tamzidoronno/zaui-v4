package com.thundashop.core.pmsmanager;

import com.google.gson.Gson;
import com.thundashop.core.common.Translation;
import com.thundashop.core.common.TranslationHandler;
import com.thundashop.core.productmanager.data.Product;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

public class PmsBookingAddonItem extends TranslationHandler implements Serializable {
    

    public void finalize() {
        if(overrideName != null && !overrideName.isEmpty()) {
            name = overrideName;
        }
    }
    
    void setOverrideName(String overrideName) {
        this.name = overrideName;
        this.overrideName = overrideName;
    }

    boolean isGroupAddon() {
        if(groupAddonSettings != null && groupAddonSettings.groupProductIds != null && !groupAddonSettings.groupProductIds.isEmpty()) {
            return true;
        }
        return false;
    }

    boolean nameIsSameAsTranslation(String text) {
        HashMap<String, String> translations = getTranslations();
        Gson gson = new Gson();
        for(String key : translations.keySet()) {
            if(key.endsWith("_name")) {
                String toCheck = gson.fromJson(translations.get(key), String.class).trim();
                if(text.trim().equals(toCheck)) {
                    return true;
                }
            }
        }
        return false;
    }

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
    private String overrideName = "";
    public Double price;
    public Double priceExTaxes;
    public Integer percentagePrice = 0;
    public String productId;
    public Integer addonType;
    public String addonSubType = "";
    public Integer count = 1;
    public boolean isActive = false;
    public boolean isSingle = false;
    public boolean noRefundable = false;
    public boolean alwaysAddAddon = false;
    public boolean isAvailableForBooking = false;
    public boolean isAvailableForCleaner = false;
    public boolean dependsOnGuestCount = false;
    public boolean isIncludedInRoomPrice = false;
    public boolean automaticallyAddToRoom = false;
    public String groupAddonType = "";
    public GroupAddonSettings groupAddonSettings = new GroupAddonSettings();
    public String channelManagerAddonText = "";
    public String bookingicon = "";
    public List<String> includedInBookingItemTypes = new ArrayList<>();
    public List<String> onlyForBookingItems = new ArrayList<>();
    public List<String> displayInBookingProcess = new ArrayList<>();
    public String addedBy;
    public List<PmsBookingAddonItemValidDateRange> validDates = new ArrayList<>();
    public boolean atEndOfStay;
    public boolean isUniqueOnOrder = false;
    public String referenceId = "";
    public Integer taxGroupNumber = null;
    public String departmentRemoteId = "";
    
    
    Map<String, String> variations = new HashMap<>();
    String description;

    
    @Translation
    public String descriptionWeb = "";
    
    @Translation
    private String name = "";
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

    void setName(String name) {
        this.name = name;
    }
    
    void setName(Product product) {
        if (product != null) {
            name = product.name;
        }
        
        if (overrideName != null && !overrideName.isEmpty()) {
            name = overrideName;
        }
    }

    public String getName() {
        if (overrideName != null && !overrideName.isEmpty()) {
            name = overrideName;
        }
        return name;
    }
    
    public String getStringDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String retDate = sdf.format(date);
        return retDate;
    }
    
    /**
     * This is the key used to indentify the same addons
     * for the same day.
     * 
     * @return 
     */
    public String getKey() {
        String key = productId;
        key += isIncludedInRoomPrice ? ";isincluded;" : ";notincluded;";
        if(isUniqueOnOrder) {
            key += ";"+addonId;
        } else {
            key += ";";
        }
        return key;
    }
}