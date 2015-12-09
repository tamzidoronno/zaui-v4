package com.thundashop.core.bookingengine.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class Booking extends DataCommon {
    public String bookingItemId = "";
    public int incrementalBookingId = 0;
    public boolean bookingDeleted = false;
    
    public String bookingItemTypeId = "";
    
    public Date startDate;
    public Date endDate;
    public String externalReference;

    public String getInformation() {
        return "[Itemid=" + bookingItemId+",incrementalBookingId="+incrementalBookingId+",bookingItemTypeId="+bookingItemTypeId+",startDate="+startDate+",endDate="+endDate+"]";
    }
}
