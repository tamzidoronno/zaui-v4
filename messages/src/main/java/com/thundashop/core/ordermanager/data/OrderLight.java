/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import java.util.Date;

/**
 *
 * @author ktonder
 */
public class OrderLight {
    private final long incrementOrderId;
    private final Date paymentDate;
    private String paymentType;
    private final Date createdDate;
    
    public OrderLight(Order order) {
        this.incrementOrderId = order.incrementOrderId;
        this.paymentDate = order.paymentDate;
        this.createdDate = order.createdDate;
        if (order.payment != null) {
            this.paymentType = order.payment.paymentType;
        }
    }
}
