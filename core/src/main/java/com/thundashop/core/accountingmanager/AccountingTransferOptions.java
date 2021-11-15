/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
class AccountingTransferOptions {

    private static final Logger log = LoggerFactory.getLogger(AccountingTransferOptions.class);

    public boolean hasFail = false;
    public List<User> users;
    public List<Order> orders;
    public AccountingTransferConfig config;
    public AccountingManagers managers;
    public List<String> logEntries = new ArrayList();
    
    public void addToLog(String text) {
        logEntries.add(text);
    }
    
    public List<String> getLogEntries() {
        return logEntries;
    }
    
    public Integer getAccountingId(String userId) {
        Integer idToUse = 0;
        if(config.startCustomerCodeOffset != null && config.startCustomerCodeOffset > 0) {
            idToUse = config.startCustomerCodeOffset;
        }

        User user = managers.userManager.getUserById(userId);
        Integer accountingId = user.customerId;
        if(user.accountingId != null && !user.accountingId.trim().isEmpty() && !user.accountingId.equals("0")) {
            try {
                accountingId = new Integer(user.accountingId);
            }catch(Exception e) {
                log.error("Number exception problem on user: `{}` storeId `{}`", user.fullName, user.storeId, e);
            }
        }
        if(accountingId >= idToUse) {
            return accountingId;
        } else {
            if(!managers.productMode) {
                //DO NOT CREATE NEW IDS IN NON PRODUCTION MODE
                return -100000;
            }
            
            return managers.userManager.setNextAccountingId(user.id, idToUse);
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
