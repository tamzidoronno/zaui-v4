/*
 * A BookingItemType is a type of booking, this can be for instance an event, 
 * a hotelroom, a hairdresser etc.
 * 
 * All BookingItems need a bookingItemType
 */
package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;

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
    public RegistrationRules rules = null;
}
