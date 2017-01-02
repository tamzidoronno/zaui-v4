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

    
    public Integer getAccountingId(String userId) {
        Integer idToUse = 0;
        if(config.startCustomerCodeOffset != null && config.startCustomerCodeOffset > 0) {
            idToUse = config.startCustomerCodeOffset;
        }

        User user = managers.userManager.getUserById(userId);

        Integer accountingId = user.customerId;
        if(user.accountingId != null && !user.accountingId.trim().isEmpty() && !user.accountingId.equals("0")) {
            accountingId = new Integer(user.accountingId);
        }
        if(accountingId > idToUse) {
            return accountingId;
        } else {
            int next = managers.userManager.getNextAccountingId();
            if(next < idToUse) {
                next = idToUse;
            }
            user.accountingId = next + "";
            managers.userManager.saveUser(user);
            return next;
        }
    }
    
    
    public String getUniqueCustomerIdForOrder(Order order) {
        for(String paymentMethod : config.paymentTypeCustomerIds.keySet()) {
            String customerId = config.paymentTypeCustomerIds.get(paymentMethod);
            if(customerId != null && !customerId.isEmpty()) {
                if(order.hasPaymentMethod(paymentMethod)) {
                    return customerId;
                }
            }
        }
        return null;
    }
    
}
