/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.checklist;

import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class BookingComVirtualCreditCardCheckProcessor extends CheckProcessorBase implements CheckListProcessor {

    public BookingComVirtualCreditCardCheckProcessor(OrderManager orderManager, PmsManager pmsManager) {
        super(orderManager, pmsManager);
    }

    @Override
    public CheckListError getError(PmsBooking booking) {
        Date today = new Date();
        
        if (booking.startDate.after(today)) {
            return null;
        }
        
        List<Order> orders = getOrders(booking);
        
        orders = orders.stream()
             .filter(order -> !order.billable)
             .collect(Collectors.toList());
        
        for (Order order : orders) {
            if (order.isBookingCom() && order.status != Order.Status.PAYMENT_COMPLETED) {
                CheckListError error = new CheckListError();
                error.filterType = getClass().getSimpleName();
                error.description = "Booking.com order : " + order.incrementOrderId + " has not been marked as paid, need to collect EVC credit.";
                error.metaData.put("orderid", order.id);
                error.metaData.put("pmsbookingid", booking.id);
                return error;
            }
        }
        
        return null;
    }

    
}
