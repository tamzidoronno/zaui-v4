/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.accountingmanager.AccountingSystemConfiguration;
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
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.webmanager.WebManager;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ktonder
 */
public abstract class AccountingSystemBase extends ManagerBase {
 
    private List<String> logEntries = new ArrayList();
     
    private AccountingSystemConfiguration config = new AccountingSystemConfiguration();
    
    @Autowired
    GetShopAccountingManager getShopAccountingManager;
    
    @Autowired
    public OrderManager orderManager;    
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    private PaymentManager paymentManager;
    
    @Autowired
    public ProductManager productManager;
    
    @Autowired
    public WebManager webManager;
    
    @Autowired
    public StoreManager storeManager;
    
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
            if (obj instanceof AccountingSystemConfiguration) {
                this.config = (AccountingSystemConfiguration)obj;
            }
        }
    }
    
    public abstract List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end);
    
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
    
    private boolean checkTaxCodes(Order order) {
        for(CartItem item : order.cart.getItems()) {
            Product prod = productManager.getProduct(item.getProduct().id);
            if(prod == null) {
                prod = productManager.getDeletedProduct(item.getProduct().id);
            }
            if(prod == null) {
                addToLog("Product does not exists anymore on order, regarding order: " + order.incrementOrderId);
                return true;
            } else if(isUsingProductTaxCodes() && (prod.sku == null || prod.sku.trim().isEmpty())) {
                if(prod.deleted != null && (prod.name == null || prod.name.trim().isEmpty())) {
                    prod.name = item.getProduct().name;
                    productManager.saveProduct(prod);
                }
                addToLog("Tax code not set for product: " + prod.name + ", regarding order: " + order.incrementOrderId);
                return true;
            }
        }
        
        return false;
    }
    
    public String getProductNumber(Product product) {
        return "";
    }
    
    abstract boolean isUsingProductTaxCodes();
    
    private boolean checkTaxCodes(List<Order> orders) {
        boolean hasFail = false;
        for(Order order : orders) {
            if (checkTaxCodes(order)) {
                hasFail = true;
            }
        }
        
        return hasFail;
    }
    
    public List<String> createNextOrderFile(Date endDate, String subType, List<Order> orders) {
        clearLog();
        
        orders.removeIf(order -> order.triedTransferredToAccountingSystem);
        
        boolean hasFail = checkTaxCodes(orders);
        
        if(hasFail) {
            return null;
        }
        
        Map<String, List<Order>> groupedOrders = groupOrders(orders);
        
        if (subType != null) {
            orders = groupedOrders.get(subType);
            if (orders == null) {
                orders = new ArrayList();
            }
        }
        
        orders = distinctOrders(orders);
        
        Date prevDate = getPreviouseEndDate();
        
        List<SavedOrderFile> newFiles = createFiles(orders, prevDate, endDate);
        
        if (newFiles == null) {
            return new ArrayList();
        }
        
        
        newFiles.stream().forEach(file -> {
            file.endDate = endDate;
            file.startDate = prevDate;
            markOrdersAsTransferred(file);
            markOrdersAsFailedTrasfer(file);
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
            cal.set(Calendar.DAY_OF_YEAR, 1);
            return cal.getTime();
        }
        
        return files.values()
                .stream()
                .filter(u -> u.endDate != null)
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
        
        if (file != null && file.orders != null) {
            file.orders.stream().forEach(orderId -> {
                
                orderManager.resetTransferToAccount(orderId);
            });    
        }
        
        
        if (file != null) {
            deleteObject(file);
        }
    }
    
    public Integer getAccountingAccountId(String userId) {
        Integer idToUse = 0;
        Integer useOffset = paymentManager.getGeneralPaymentConfig().accountingCustomerOffset;
        
        if (useOffset != null && useOffset > 0) {
            idToUse = useOffset;
        }
        
        User user = userManager.getUserByIdIncludedDeleted(userId);
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
            return userManager.setNextAccountingId(userId, idToUse);
        }
    }
    
    public String getAccountingNumberForProduct(String productId) {
        return productManager.getProduct(productId).getAccountingAccount();
    }
    
    public String getUniqueCustomerIdForOrder(Order order) {
        String paymentId = order.getPaymentApplicationId();
        return getAccountingNumberForPaymentApplicationId(paymentId);
    }

    protected String getAccountingNumberForPaymentApplicationId(String paymentId) {
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
        logPrint(text);
        logEntries.add(text);
    }
   
    private void markOrdersAsTransferred(SavedOrderFile file) {
        file.orders.stream().forEach(orderId -> { 
            orderManager.markOrderAsTransferredToAccounting(orderId);
        });
    }

    public List<String> getLogEntries() {
        return logEntries;
    }
    
    public long getAccountingIncrementOrderId(Order order) {
        Integer prefix = paymentManager.getGeneralPaymentConfig().accountingerOrderIdPrefix;
        long toReturn = order.incrementOrderId;
        if(prefix != null && paymentManager.getGeneralPaymentConfig().accountingerOrderIdPrefix > 0) {
            toReturn = new Long(prefix + "" + toReturn + "");
        }
        return toReturn;
    }
    
    public Date getAccountingPostingDate(Order order) {
        Date postingDate = order.getEndDateByItems();
        if(postingDate == null) {
            postingDate = order.rowCreatedDate;
        }
        if(order.paymentDate != null) {
            postingDate = order.paymentDate;
        }
        
        if(paymentManager.getGeneralPaymentConfig().postingDate != null && paymentManager.getGeneralPaymentConfig().postingDate.contains("checkout")) {
            postingDate = order.getEndDateByItems();
        }
        if(paymentManager.getGeneralPaymentConfig().postingDate != null && paymentManager.getGeneralPaymentConfig().postingDate.contains("checkin")) {
            postingDate = order.getStartDateByItems();
        }
        return postingDate;
    }
    
    public int getAccountNumberForPaymentMethod(Order order) {
        return Integer.valueOf(getUniqueCustomerIdForOrder(order));
    }
    
    public String getAccountDescriptionForPaymentMethod(Order order) {
        String paymentId = order.getPaymentApplicationId();
        if (paymentId == null) {
            return "";
        }
        
        StorePaymentConfig config = paymentManager.getStorePaymentConfiguration(paymentId);
        return config.accountingDescription;
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
    
    public Map<String, List<Order>> groupOrders(List<Order> orders) {
        
        Map<String, List<Order>> retMap = new HashMap();
        
        for (Order order : orders) {
            String paymentId = order.getPaymentApplicationId();
            String subType = getSubType(paymentId);
            if (retMap.get(subType) == null) {
                retMap.put(subType, new ArrayList());
            }
            retMap.get(subType).add(order);
        }

        return retMap;
    }
    
    public boolean supportDirectTransfer() {
        return false;
    }
    
    public String getSubType(String paymentId) {
        // InvoicePayment
        if(paymentId == null) {
            return "no method";
        }
        
        if (paymentId.equals("70ace3f0-3981-11e3-aa6e-0800200c9a66")) {
            return "invoice";
        }
        
        // EhfPayment
        if (paymentId.equals("bd13472e-87ee-4b8d-a1ae-95fb471cedce")) {
            return "invoice";
        }
        
        return "other";
    }
    
    public String createLineText(CartItem item) {
        String lineText = "";
        String startDate = "";
        DateFormat sourceFormat = new SimpleDateFormat("dd.MM.yyyy");
        if(item.startDate != null) {
            startDate = sourceFormat.format(item.startDate);
        }

        String endDate = "";
        if(item.endDate != null) {
            endDate = sourceFormat.format(item.endDate);
        }
        
        String metaData = item.getProduct().metaData;
        if(metaData == null) {
            metaData = "";
        }
        if(!item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().name + " "+metaData+" " + item.getProduct().additionalMetaData + " (" + startDate + " - " + endDate + ")";
        } else {
            lineText = item.getProduct().name + " " + metaData + " (" + startDate + " - " + endDate + ")";
        }
         
        return lineText;
    }
    
    public String nullAndCsvCheck(String text) {
        if(text == null) {
            return "";
        }
        return text.replaceAll(",", " ");
    }

    public abstract String getSystemName();

    void markTransferred() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getPreviouseEndDate());
        cal.add(Calendar.MINUTE, 2);
        List<Order> orders = orderManager.getOrdersToTransferToAccount(cal.getTime());
        
        orders.stream().forEach(order -> { 
            order.transferredToAccountingSystem = true;
            orderManager.saveObject(order);
        });
    }

    
    public void setConfig(String key, String value) {
        config.configs.put(key, value);
        saveObject(config);
    }
    
    public String getConfig(String key) {
        return config.configs.get(key);
    }

    public HashMap<String, String> getConfigs() {
        return config.configs;
    }

    public HashMap<String, String> getConfigOptions() {
        return new HashMap();
    }

    private void markOrdersAsFailedTrasfer(SavedOrderFile file) {
        file.ordersTriedButFailed.stream().forEach(orderId -> { 
            Order order = orderManager.getOrderSecure(orderId);
            order.triedTransferredToAccountingSystem = true;
            orderManager.saveOrder(order);
        });
    }
    
    
    public void directTransfer(String orderId) {
        Order order = orderManager.getOrder(orderId);
        
        if(checkTaxCodes(order)) {
            return;
        }
        
        handleDirectTransfer(orderId);
    }
    
    public abstract void handleDirectTransfer(String orderId);
    
    public void clearLog() {
        logEntries.clear();
    }

    private List<Order> distinctOrders(List<Order> orders) {
        Map<String, Order> distinctOrders = new HashMap();
        
        orders.stream().forEach(order -> distinctOrders.put(order.id, order));
        
        return new ArrayList(distinctOrders.values());
    }
    
    public String createBankTransferFile() {
        return null;
    }

    /**
     * If this returns true the system should not transfer advanced 
     * records to the accotuning system, like products, orders, etc.
     * 
     * Only a record of accounts and financial difference for each day by using OrderDailyBreaker.
     * 
     * @return 
     */
    abstract boolean isPrimitive();

    public void transfer(List<DayIncome> incomes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}