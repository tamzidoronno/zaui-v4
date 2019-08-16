/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class UnsettledAmountProcessor extends CheckProcessorBase implements CheckListProcessor {

    public UnsettledAmountProcessor(OrderManager orderManager, PmsManager pmsManager) {
        super(orderManager, pmsManager);
    }

    @Override
    public CheckListError getError(PmsBooking booking) {
        if (new Date().before(booking.getEndDate())) {
            return null;
        }
        
        if (booking.ignoreUnsettledAmount) {
            return null;
        }
        
        getPmsManager().calculateUnsettledAmountForRooms(booking);
        
        double amount = booking.getUnpaidAmount();
        
        if (amount < -0.1 || amount > 0.1) {
            CheckListError error = new CheckListError();
            error.filterType = getClass().getSimpleName();
            error.metaData.put("pmsBookingId", booking.id);
            error.metaData.put("bookingEndDate", booking.endDate);
            error.metaData.put("totalUnsettledAmount", amount);
            error.description = "Unsettled amount";
            return error;
        }
        
        return null;
    }
    
}
