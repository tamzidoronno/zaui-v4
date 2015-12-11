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
    public boolean needConfirmation = false;

    public String getInformation() {
        return "[Itemid=" + bookingItemId+",incrementalBookingId="+incrementalBookingId+",bookingItemTypeId="+bookingItemTypeId+",startDate="+startDate+",endDate="+endDate+"]";
    }

    public boolean conflictsWith(Date start, Date end) {
        if (start.equals(start) || start.equals(end) || end.equals(start) || end.equals(end)) {
            return true;
        }
        
        if (start.after(startDate) && start.before(endDate)) {
            return true;
        }
        
        if (end.after(startDate) && end.before(endDate)) {
            return true;
        }
        
        if (start.equals(startDate) && end.equals(endDate)) {
            return true;
        }
        
        return false;
        
    }
}
