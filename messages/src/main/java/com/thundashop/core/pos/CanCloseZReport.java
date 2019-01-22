/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pos;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class CanCloseZReport {
    public boolean canClose = true;
    
    public List<PmsBookingRooms> roomsWithProblems = new ArrayList();
    public long fReportErrorCount = 0;
    public List<Order> uncompletedOrders = new ArrayList();
    
    public void finalize() {
        if (!roomsWithProblems.isEmpty()) {
            canClose = false;
        }
        
        if (!uncompletedOrders.isEmpty()) {
            canClose = false;
        }
        
        if (fReportErrorCount > 0) {
            canClose = false;
        }
    }
}
