/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.function.Predicate;

/**
 *
 * @author ktonder
 */
public class OrderLight extends DataCommon {

    public String session;
    public String userId;
    public String paymentTransactionId = "";
    public String reference = "";
    public int status = Order.Status.CREATED;
    public boolean testOrder = false;
    public boolean captured = false;
    public Date createdDate = new Date();
    public String orderId = "";
    public String incrementOrderId;
    

    public OrderLight() {
    }

    public OrderLight(Order order) {
        update(order);
    }
    
    public boolean useForStatistic() {
        if (status == Order.Status.CANCELED || status == Order.Status.PAYMENT_FAILED) {
            return false;
        }
        
        if (testOrder) {
            return false;
        }
        
        return true;
    }

    public void update(Order order) {
        session = order.session;
        userId = order.userId;
        paymentTransactionId = order.paymentTransactionId;
        reference = order.reference;
        status = order.status;
        testOrder = order.testOrder;
        captured = order.captured;
        createdDate = order.createdDate;
        orderId = order.id;
    }
    
}
