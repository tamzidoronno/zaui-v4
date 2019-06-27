/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsManager;

/**
 *
 * @author ktonder
 */
public class AccruedPaymentChecklist extends CheckProcessorBase implements CheckListProcessor {

    public AccruedPaymentChecklist(OrderManager orderManager, PmsManager pmsManager) {
        super(orderManager, pmsManager);
    }

    @Override
    public CheckListError getError(PmsBooking booking) {
        for (PmsBookingRooms room : booking.rooms) {
            if (room.createOrdersOnZReport && room.isEnded() && (room.unsettledAmountIncAccrued == null || room.unsettledAmountIncAccrued != 0.0)) {
                CheckListError error = new CheckListError();
                error.filterType = getClass().getSimpleName();
                error.description = "Room found and set to pay after stay, after checkout time has passed.";
                error.metaData.put("pmsbookingid", booking.id);
                error.metaData.put("pmsbookingroomid", room.pmsBookingRoomId);
                return error;
            }
        }
        
        return null;
    }
    
}
