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

/**
 *
 * @author ktonder
 */
public class UnpaidPaymentLinksProcessor extends CheckProcessorBase implements CheckListProcessor {

    public UnpaidPaymentLinksProcessor(OrderManager orderManager, PmsManager pmsManager) {
        super(orderManager, pmsManager);
    }

    @Override
    public CheckListError getError(PmsBooking booking) {
        Date today = new Date();
        
        if (today.before(booking.endDate)) {
            return null;
        }
        
        List<Order> orders = getOrders(booking);

        for (Order order : orders) {
            if (order.isPaymentLinkType() && order.status != Order.Status.PAYMENT_COMPLETED) {
                CheckListError error = new CheckListError();
                error.filterType = getClass().getSimpleName();
                error.metaData.put("pmsBookingId", booking.id);
                error.metaData.put("bookingEndDate", booking.endDate);
                error.metaData.put("orderId", order.id);
                error.description = "Paymentlinks should not be sent out after stay has ended, please delete order";
                return error;
            }
        }
        
        return null;
    }
    
}
