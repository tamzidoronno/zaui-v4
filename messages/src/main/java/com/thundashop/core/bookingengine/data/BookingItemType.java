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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class BookingItemType extends DataCommon {
    public static class BookingItemAddonTypes {
        public static Integer NONE = 0;
        public static Integer ONETIME = 1;
        public static Integer FORTIME = 2;
    }
    
    @Translation
    public String name;
    public String productId;
    public List<String> historicalProductIds = new ArrayList();
    public String pageId;
    public Boolean visibleForBooking = true;
    public Boolean autoConfirm = false;
    public Boolean internal = false;
    public Integer addon = BookingItemAddonTypes.NONE;
    public Integer size = 0;
    public RegistrationRules rules = null;
    public TimeRepeaterData openingHours;
    public HashMap<String, TimeRepeaterData> openingHoursData = new HashMap();
    public Integer order = 0;
    @Translation
    public String description = "";
    public String group = "";
    public Integer capacity = 0;
    public Integer minStay = 0;
    public String eventItemGroup = "";

}
