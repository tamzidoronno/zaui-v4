/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.accountingmanager.AccountingTransferConfig;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.paymentmanager.PaymentManager;
import com.thundashop.core.paymentmanager.StorePaymentConfig;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public abstract class AccountingSystemBase extends ManagerBase {

    public enum SystemType {
        GBAT10,
        XLEDGER
    }
    
    private List<String> logEntries = new ArrayList();
     
    @Autowired
    public OrderManager orderManager;    
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    private PaymentManager paymentManager;
    
    public HashMap<String, SavedOrderFile> files = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        addSavedOrderFiles(data);
    }

    private void addSavedOrderFiles(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if(obj instanceof SavedOrderFile) {
                SavedOrderFile file = (SavedOrderFile) obj;
                files.put(((SavedOrderFile) obj).id, file);
            }
        }
    }
    
    public abstract List<SavedOrderFile> createFiles(List<Order> orders);
    
    public abstract SystemType getSystemType();
    
    public void transferOldOrderFile(SavedOrderFile file) {
        if (files.containsKey(file.id)) {
            return;
        }
        
        file.orders.stream().forEach(orderId -> {
            Order order = orderManager.getOrderSecure(orderId);
            order.transferredToAccountingSystem = true;
            order.transferToAccountingDate = file.startDate;
            orderManager.saveObject(order);
        });
        
        
        saveObject(file);
        files.put(file.id, file);
    }
    
    public List<String> createNextOrderFile(Date endDate) {
        logEntries.clear();
        
        List<Order> orders = orderManager.getOrdersToTransferToAccount(endDate);
        List<SavedOrderFile> newFiles = createFiles(orders);
        
        if (newFiles == null) {
            return new ArrayList();
        }
        
        Date prevDate = getPreviouseEndDate();
        newFiles.stream().forEach(file -> {
            file.endDate = endDate;
            file.startDate = prevDate;
            markOrdersAsTransferred(file);
            sumOrders(file);
            finalizeFile(file);
            saveObject(file);
            files.put(file.id, file);
        });
        
        return newFiles.stream().map(file -> file.id).collect(Collectors.toList());
    }
    
    public List<SavedOrderFile> getOrderFiles() {
        List<SavedOrderFile> retList = new ArrayList(files.values());
        
        Collections.sort(retList, (SavedOrderFile o1, SavedOrderFile o2) -> {
            return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
        });
        
        return retList;
    }
    
    public SavedOrderFile getOrderFile(String fileId) {
        return files.get(fileId);
    }
    
    public Date getPreviouseEndDate() {
        if (files.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 0);
            cal.set(Calendar.DAY_OF_MONTH, 0);
            return cal.getTime();
        }
        
        return files.values()
                .stream()
                .map(u -> u.endDate)
                .max(Date::compareTo)
                .get();
    }
    
    public List<String> getOrdersToIncludeForNextTransfer(Date endDate) {
        List<Order> orders =  orderManager.getOrdersToTransferToAccount(endDate);
        
        return orders.stream()
                .map(order -> order.id)
                .collect(Collectors.toList());
    }
    
    void deleteFile(String fileId) {
        SavedOrderFile file = files.remove(fileId);
        if (file != null) {
            deleteObject(file);
        }
    }
    
    public Integer getAccountingId(String userId) {
        Integer idToUse = 0;
        Integer useOffset = paymentManager.getGeneralPaymentConfig().accountingCustomerOffset;
        
        if (useOffset != null && useOffset > 0) {
            idToUse = useOffset;
        }

        User user = userManager.getUserById(userId);
        Integer accountingId = user.customerId;
        if(user.accountingId != null && !user.accountingId.trim().isEmpty() && !user.accountingId.equals("0")) {
            try {
                accountingId = new Integer(user.accountingId);
            }catch(Exception e) {
                System.out.println("Number exception problem on user: " + user.fullName);
            }
        }
        if(accountingId >= idToUse) {
            return accountingId;
        } else {
            if(GetShopLogHandler.isDeveloper) {
                //DO NOT CREATE NEW IDS IN NON PRODUCTION MODE
                return -100000;
            }
            int next = userManager.getNextAccountingId();
            if(next < idToUse) {
                next = idToUse;
            }
            user.accountingId = next + "";
            userManager.saveUser(user);
            return next;
        }
    }
    
    
    public String getUniqueCustomerIdForOrder(Order order) {
        String paymentId = order.getPaymentApplicationId();
        if (paymentId == null) {
            return null;
        }
        
        StorePaymentConfig config = paymentManager.getStorePaymentConfiguration(paymentId);
        
        if (config != null) {
            return config.userCustomerNumber;
        }

        return null;
    }
    
    public void addToLog(String text) {
        logEntries.add(text);
    }
   
    private void markOrdersAsTransferred(SavedOrderFile file) {
        file.orders.stream().forEach(orderId -> { 
            Order order = orderManager.getOrderSecure(orderId);
            order.transferredToAccountingSystem = true;
            orderManager.saveOrder(order);
        });
    }

    public List<String> getLogEntries() {
        return logEntries;
    }
    
    private void finalizeFile(SavedOrderFile saved) {
        saved.sumAmountExOrderLines = 0.0;
        saved.sumAmountIncOrderLines = 0.0;
        saved.onlyPositiveLinesEx = 0.0;
        saved.onlyPositiveLinesInc = 0.0;
        
        saved.tamperedOrders.clear();
        for(String orderId : saved.orders) {
            Order order = orderManager.getOrderSecure(orderId);
            if(order == null || order.cart == null) {
                continue;
            } 
            double total = orderManager.getTotalAmount(order);
            double totalEx = orderManager.getTotalAmountExTaxes(order);
            for(CartItem item : order.cart.getItems()) {
                int count = item.getCount();
                int countToUse = new Integer(count);
                if(countToUse > 0 && total > 0) {
                    saved.onlyPositiveLinesEx += item.getProduct().priceExTaxes * countToUse;
                    saved.onlyPositiveLinesInc += item.getProduct().price * countToUse;
                }
                if(countToUse < 0 && total < 0) {
                    saved.onlyPositiveLinesEx += item.getProduct().priceExTaxes * (countToUse*-1);
                    saved.onlyPositiveLinesInc += item.getProduct().price * (countToUse*-1);
                }
                if(count< 0) {
                    count *= -1;
                }
                saved.sumAmountExOrderLines += (item.getProduct().priceExTaxes*count);
                saved.sumAmountIncOrderLines += (item.getProduct().price*count);
            }
            if(!saved.amountOnOrder.containsKey(order.id)) {
                saved.amountOnOrder.put(order.id, total);
            } else if(total != saved.amountOnOrder.get(order.id)) {
                saved.tamperedOrders.add(order.id);
            }

            if(total < 0) { total *= -1; }
            if(totalEx < 0) { totalEx *= -1; }
            saved.sumAmountIncOrderLines += total;
            saved.sumAmountExOrderLines += totalEx;
        }
    }
    
    private void sumOrders(SavedOrderFile res) {
        if(res.orders == null) {
            return;
        }
        Double amountEx = 0.0;
        Double amountInc = 0.0;
        Double amountExDebet = 0.0;
        Double amountIncDebet = 0.0;
        List<String> orderIds = new ArrayList();
        
        for(String order : res.orders) {
            Order ord = orderManager.getOrder(order);
            if(!ord.closed) {
                ord.closed = true;
                orderManager.saveOrder(ord);
            }
            Double sumEx = orderManager.getTotalAmountExTaxes(ord);
            Double sumInc = orderManager.getTotalAmount(ord);
            amountEx += sumEx;
            amountInc += sumInc;
            
            if(sumEx < 0) {
                amountExDebet += (sumEx * -1);
            } else {
                amountExDebet += sumEx;
            }
            
            if(sumInc < 0) {
                amountIncDebet += (sumInc * -1);
            } else {
                amountIncDebet += sumInc;
            }
            orderIds.add(ord.id);
        }
        
        res.amountEx = amountEx;
        res.amountInc = amountInc;
        res.amountExDebet = amountExDebet;
        res.amountIncDebet = amountIncDebet;
        finalizeFile(res);
    }
    
    void createMegaFile() {
        TreeSet<String> set = new TreeSet<String>();
        
        files.values().stream()
                .forEach(ok -> {
                    set.addAll(ok.orders);
                });
        
        List<Order> toProcessOrder = set.stream()
                .map(orderId -> orderManager.getOrder(orderId))
                .collect(Collectors.toList());
        
        createFiles(toProcessOrder);
        
    }

}