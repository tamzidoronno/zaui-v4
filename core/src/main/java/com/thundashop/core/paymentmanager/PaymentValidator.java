/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.paymentmanager.PaymentConfiguration;

/**
 *
 * @author ktonder
 */
public class PaymentValidator extends DataCommon {
    private PaymentConfiguration config;
    private Order order;

    public PaymentValidator(PaymentConfiguration config, Order order) {
        this.config = config;
        this.order = order;
    }
    
    
}
