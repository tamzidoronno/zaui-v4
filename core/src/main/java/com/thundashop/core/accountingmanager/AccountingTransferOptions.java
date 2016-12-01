/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 *
 * @author boggi
 */
class AccountingTransferOptions {
    public List<User> users;
    public List<Order> orders;
    public AccountingTransferConfig config;
    public AccountingManagers managers;

    public String getUniqueCustomerIdForOrder(Order order) {
        for(String paymentMethod : config.paymentTypeCustomerIds.values()) {
            String customerId = config.paymentTypeCustomerIds.get(paymentMethod);
            if(customerId != null && !customerId.isEmpty()) {
                if(order.hasPaymentMethod(paymentMethod)) {
                    return customerId;
                }
            }
        }
        return "";
    }
    
}
