/*
 * A BookingItemType is a type of booking, this can be for instance an event, 
 * a hotelroom, a hairdresser etc.
 * 
 * All BookingItems need a bookingItemType
 */
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Translation;
import com.thundashop.core.pmsmanager.TimeRepeaterData;

import java.util.*;

/**
 *
 * @author ktonder
 */
public class BookingItemType extends DataCommon {

    public String getTranslatedName(String language) {
        
        if(language != null && language.equals("en")) {
            language = "en_en";
        }
        
        String desc = getTranslationsByKey("name", language);
        if(desc == null || desc.isEmpty()) { desc = name; }
        if(nameTranslations.containsKey(language)) { desc = nameTranslations.get(language); }
        return desc;
    }

    public String getTranslatedDescription(String language) {
        String desc = getTranslationsByKey("description", language);
        if(desc == null || desc.isEmpty()) { desc = description; }
        if(descriptionTranslations.containsKey(language)) { desc = descriptionTranslations.get(language); }
        return desc;
    }
    
    public static class BookingItemAddonTypes {
        public static Integer NONE = 0;
        public static Integer ONETIME = 1;
        public static Integer FORTIME = 2;
    }
    public static class BookingSystemCategory {
        public static Integer ROOM = 0;
        public static Integer CONFERENCE = 1;
        public static Integer RESTAURANT = 2;
        public static Integer CAMPING = 3;
        public static Integer CABIN = 4;
        public static Integer HOSTELBED = 5;
        public static Integer APARTMENT = 6;
        public static final Map<Integer, String> categories;
        static {
            Map<Integer, String> aMap = new HashMap<>();
            aMap.put(0, "ROOM");
            aMap.put(1, "CONFERENCE");
            aMap.put(2, "RESTAURANT");
            aMap.put(3, "CAMPING");
            aMap.put(4, "CABIN");
            aMap.put(5, "HOSTELBED");
            aMap.put(6, "APARTMENT");
            categories = Collections.unmodifiableMap(aMap);
        }
    }
    
    @Translation
    public String name;
    public String productId;
    public List<String> historicalProductIds = new ArrayList();
    public String pageId;
    public Boolean visibleForBooking = true;
    public Boolean autoConfirm = false;
    public Integer addon = BookingItemAddonTypes.NONE;
    public Integer size = 0;
    public RegistrationRules rules = null;
    public TimeRepeaterData openingHours;
    public HashMap<String, TimeRepeaterData> openingHoursData = new HashMap();
    public Integer order = 0;
    public Integer orderAvailability = 0;
    @Translation
    public String description = "";
    public HashMap<String, String> descriptionTranslations = new HashMap();
    public HashMap<String, String> nameTranslations = new HashMap();
    public String group = "";
    public Integer capacity = 0;
    public Integer minStay = 0;
    public String eventItemGroup = "";
    
    //0 = default hotel room, 1 = conference room, 2 = restaurant
    public Integer systemCategory = 0;
    
    public String departmentId = "";

}
