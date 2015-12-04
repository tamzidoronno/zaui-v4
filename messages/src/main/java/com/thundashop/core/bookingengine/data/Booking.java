/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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

    public String getInformation() {
        return "[Itemid=" + bookingItemId+",incrementalBookingId="+incrementalBookingId+",bookingItemTypeId="+bookingItemTypeId+",startDate="+startDate+",endDate="+endDate+"]";
    }
}
