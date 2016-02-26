/*
 * A BookingItemType is a type of booking, this can be for instance an event, 
 * a hotelroom, a hairdresser etc.
 * 
 * All BookingItems need a bookingItemType
 */
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.TimeRepeaterData;
import java.util.HashMap;

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
    
    public String name;
    public String productId;
    public String pageId;
    public Boolean visibleForBooking = true;
    public Integer addon = BookingItemAddonTypes.NONE;
    public Integer size = 0;
    public RegistrationRules rules = null;
    public TimeRepeaterData openingHours;
    public HashMap<String, TimeRepeaterData> openingHoursData = new HashMap();
    public Integer order = 0;
    public String description = "";
    public Integer capacity = 0;

}
