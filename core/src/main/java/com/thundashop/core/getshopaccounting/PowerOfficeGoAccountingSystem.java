/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import java.util.Calendar;
import com.powerofficego.data.AccessToken;
import com.powerofficego.data.ApiCustomerResponse;
import com.powerofficego.data.ApiOrderTransferResponse;
import com.powerofficego.data.Customer;
import com.powerofficego.data.PowerOfficeGoImportLine;
import com.powerofficego.data.PowerOfficeGoSalesOrder;
import com.powerofficego.data.PowerOfficeGoSalesOrderLines;
import com.powerofficego.data.SalesOrderTransfer;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PowerOfficeGoAccountingSystem extends AccountingSystemBase {

    private static final Logger logger = LoggerFactory.getLogger(PowerOfficeGoAccountingSystem.class);

    private String token;

    @Autowired
    private PowerOfficeGoHttpClientManager httpClientManager;

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
        try {
            Map<String, List<Order>> groupedOrders = groupOrders(orders);

            ArrayList<SavedOrderFile> retFiles = new ArrayList();

            for (String subType : groupedOrders.keySet()) {
                if (groupedOrders.get(subType) == null) {
                    return new ArrayList();
                }

                SavedOrderFile file = generateFile(orders, subType);
                if(file != null) {
                    file.subtype = subType;
                    retFiles.add(file);
                }
            }

            return retFiles;
        }catch(Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    public SystemType getSystemType() {
        return SystemType.POWEROFFICEGO_API;
    }

    @Override
    public String getSystemName() {
        return "Power Office Go API";
    }

    @Override
    public void handleDirectTransfer(String orderId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public HashMap<String, String> getConfigOptions() {
        HashMap<String, String> ret = new HashMap();
        ret.put("password", "PowerOfficeGo Application Key");
        ret.put("department", "PowerOfficeGo Department");
        return ret;
    }
    
    private SavedOrderFile generateFile(List<Order> orders, String subType) {
        this.token = createAccessToken();
        if(token == null || token.isEmpty()) {
            logger.warn("Failed to fetch access token of powerOffice, authentication failed.");
            addToLog("Failed to fetch access token, authentication failed.");
            return null;
        }
        
//        addToLog("Access token: " + token);
        HashMap<String, User> users = new HashMap();
        HashMap<String, Product> products = new HashMap();
        
        for(Order order : orders) {
            if(getUniqueCustomerIdForOrder(order) == null) {
                User user = userManager.getUserByIdIncludedDeleted(order.userId);
                if (user != null) {
                    users.put(user.id, user);
                }
            }
        }
        boolean stopNow = false;
        for(Order order : orders) {
            if(order.cart == null) {
                continue;
            }
            for(CartItem item : order.cart.getItems()) {
                Product product = productManager.getProduct(item.getProduct().id);
                if(product == null) { product = productManager.getDeletedProduct(item.getProduct().id); }
                if(product.accountingSystemId == null || product.accountingSystemId.isEmpty()) {
                    logger.info("Failed to since product id is missing on product : {} order: {}" , product.name, order.incrementOrderId);
                    addToLog("Failed to since product id is missing on product : " + product.name + " order: " + order.incrementOrderId);
                    stopNow = true;
                } else {
                    products.put(product.id, product);
                }
            }
        }
        if(stopNow) {
            return null;
        }
        
        for(User user : users.values()) {
            if(!createUpdateUser(user)) {
                logger.warn("failed Transferring user, accounting id {} , name {} , getShop customerId {}",
                        user.accountingId, user.fullName, user.customerId);
                addToLog("failed Transferring user, accounting id " + user.accountingId + ", name" + user.fullName + ", getshop customerid " + user.customerId);
                return null;
            }
        }

        SavedOrderFile file = new SavedOrderFile();
        String transferId = transferOrders(orders, subType);
        
        if(!getLogEntries().isEmpty()) {
            return null;
        }
        
        if(!transferId.isEmpty()) {
            file.orders= new ArrayList();
            for(Order ord : orders) {
                file.orders.add(ord.id);
            }
            file.transferId = transferId;
            
        }
        
        return file;
    }

    private boolean createUpdateUser(User user) {
        String endpoint = "https://api.poweroffice.net/customer/";
        Customer customer = new Customer();

        if(user.address == null) {
            user.address = new com.thundashop.core.usermanager.data.Address();
        }
        
        customer.setUser(user);
        customer.code = getAccountingAccountId(user.id) + "";
        Gson gson = new Gson();

        String data = gson.toJson(customer);

        if (GetShopLogHandler.isDeveloper) {
            user.accountingId = customer.code + "";
            user.externalAccountingId = "1";
            return true;
        }

        String result = null;
        ApiCustomerResponse resp = null;

        try {
            result = httpClientManager.post(data, token, endpoint);
            resp = gson.fromJson(result, ApiCustomerResponse.class);
        } catch (Exception e) {
            logger.error("PowerOfficeGo api result: {} , postData: {} ", result, data, e);
        }

        if(resp != null && resp.success) {
            user.accountingId = customer.code + "";
            user.externalAccountingId = resp.data.id + "";
            userManager.saveUser(user);
            return true;
        } else {
            String respSummary = (resp != null) ? resp.summary : "";
            logger.warn("Failed to transfer customer, post data: {},  result: {} , user.customerId: {} , user.fullName: {} , " +
                    "summary: {} , accountingId: {} , powerOfficeGoId: {}", data, result, user.customerId, user.fullName,
                    respSummary, user.accountingId, user.externalAccountingId);
            addToLog("Failed to transfer customer: " + result + "(" + user.customerId + " - " + user.fullName);
            addToLog(respSummary + " : accounting id: " + user.accountingId + ", powerofficego id: " + user.externalAccountingId);
        }

        return false;
    }

    private String createAccessToken() {
        try {
            Gson gson = new Gson();
            String auth = "e5fdab3b-97b5-4041-bddb-5b2a48ccee1c:" + getConfig("password"); //config.password;
            String res = webManager.htmlPostBasicAuth("https://api.poweroffice.net/OAuth/Token", "grant_type=client_credentials", false, "UTF-8", auth);
            AccessToken token = gson.fromJson(res, AccessToken.class);
            return token.access_token;
        }catch(Exception e) {
            logger.error("", e);
            return "";
        }
    }

    private String transferOrders(List<Order> orders, String subType) {
        String endpoint = "https://api.poweroffice.net/Import/";
        List<PowerOfficeGoSalesOrder> salesOrdersToTransfer = new ArrayList();
        List<PowerOfficeGoImportLine> importLinesToTransfer = new ArrayList();
        for(Order order : orders) {
            if(subType.equals("invoice")) {
                PowerOfficeGoSalesOrder goOrder = createGoSalesOrderLine(order);
                if (goOrder != null) {
                    salesOrdersToTransfer.add(goOrder);
                }
                
            } else {
                if(order.cart.getItems().size() > 0) {
                    List<PowerOfficeGoImportLine> lines = createGoImportLine(order);
                    importLinesToTransfer.addAll(lines);
                }
            }
        }
        SalesOrderTransfer transferObject = new SalesOrderTransfer();
        transferObject.description = "getshop file of type : " + subType;
        transferObject.salesOrders = salesOrdersToTransfer;
        transferObject.importLines = importLinesToTransfer;
        Gson gson = new Gson();
        String data = gson.toJson(transferObject);
        try {
            if(!GetShopLogHandler.isDeveloper) {
                String result = httpClientManager.post(data, token, endpoint);
                ApiOrderTransferResponse resp = gson.fromJson(result, ApiOrderTransferResponse.class);
                if(resp.success) {
                    for(Order order : orders) {
                        order.transferredToAccountingSystem = true;
                        order.dateTransferredToAccount = new Date();
                        orderManager.saveOrder(order);
                    }
                    return resp.data;
                } else {
                    /* @TODO HANDLE PROPER WARNING */
                    logger.warn("Failed to transfer customer: {}", result);
                    addToLog("Failed to transfer customer: " + result);
                }
            } else {
                addToLog("Did not transfer files to powerofficego due to developer mode");
            }
        }catch(Exception e) {
            logger.error("", e);
        }
        return "";
    }

    private PowerOfficeGoSalesOrder createGoSalesOrderLine(Order order) throws ErrorException, NumberFormatException {
        User user = userManager.getUserByIdIncludedDeleted(order.userId);
        if (user == null) {
            return null;
        }
        
        String uniqueId = getUniqueCustomerIdForOrder(order);
        PowerOfficeGoSalesOrder goOrder = new PowerOfficeGoSalesOrder();
        if(uniqueId == null) {
            if (user == null) {
                logger.warn("Not found user: " + order.userId);
            }
            goOrder.customerCode = new Integer(user.accountingId);
        } else {
            goOrder.customerCode = new Integer(uniqueId);
        }
        goOrder.reference = getAccountingIncrementOrderId(order) + "";
        goOrder.mergeWithPreviousOrder = false;
        goOrder.salesOrderLines = new ArrayList();
        goOrder.orderNo = (int)getAccountingIncrementOrderId(order);
        goOrder.departmentCode = getConfig("department");
        if(order.cart != null) {
            for(CartItem item : order.cart.getItems()) {
                PowerOfficeGoSalesOrderLines line = new PowerOfficeGoSalesOrderLines();
                line.description = createLineText(item);
                
                Product prod = productManager.getProduct(item.getProduct().id);
                if(prod == null) { prod = productManager.getDeletedProduct(item.getProduct().id); };
                line.productCode = prod.accountingSystemId;
                line.quantity = item.getCount();
                line.salesOrderLineUnitPrice = item.getProduct().priceExTaxes;
                goOrder.salesOrderLines.add(line);
            }
        }
        return goOrder;
    }
    
    

    private List<PowerOfficeGoImportLine> createGoImportLine(Order order) {
        int bilags = 1;
        List<PowerOfficeGoImportLine> lines = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.rowCreatedDate);
        cal.add(Calendar.DAY_OF_YEAR, 14);
        Date postingDate = getAccountingPostingDate(order);
        Integer incrementOrderId = (int)getAccountingIncrementOrderId(order);
        
        PowerOfficeGoImportLine totalline = new PowerOfficeGoImportLine();
        totalline.description = "GetShop order: " + incrementOrderId;
        totalline.invoiceNo = incrementOrderId;
        totalline.documentNumber = incrementOrderId;
        totalline.amount = orderManager.getTotalAmount(order);
        totalline.currencyAmount = orderManager.getTotalAmount(order);
        totalline.postingDate = postingDate;
        totalline.documentDate = order.rowCreatedDate;
        totalline.dueDate = cal.getTime();
        totalline.currencyCode = "NOK";
        totalline.departmentCode = getConfig("department");

        String uniqueId = getUniqueCustomerIdForOrder(order);
        if(uniqueId != null) {
            totalline.customerCode = new Integer(uniqueId);
        } else {
            User user = userManager.getUserByIdIncludedDeleted(order.userId);
            totalline.customerCode = new Integer(user.accountingId);
        }
        lines.add(totalline);

        bilags++;        
        if(order.cart != null) {
            for(CartItem item : order.cart.getItems()) {
                Product prod =  productManager.getProduct(item.getProduct().id);
                PowerOfficeGoImportLine line = new PowerOfficeGoImportLine();
                if(prod == null) {
                    prod = productManager.getDeletedProduct(item.getProduct().id);
                }
                if(prod == null) {
                    logger.warn("Product does not exists on order {}", order.incrementOrderId);
                    continue;
                }
                if(prod.getAccountingAccount() == null) {
                    logger.warn("Product : {} does not have an accounting number, orderId: {}" , prod.name, order.incrementOrderId);
                    continue;
                }
                line.accountNumber = new Integer(prod.getAccountingAccount());
                line.description = createLineText(item);
                line.productCode = prod.accountingSystemId;
                line.invoiceNo = incrementOrderId;
                int count = item.getCount();
                line.amount = (item.getProduct().price * count) * -1;
                if(count < 0) {
                    count *= -1;
                }
                line.quantity = count;
                line.postingDate = postingDate;
                line.documentDate = order.rowCreatedDate;
                line.documentNumber = incrementOrderId;
                line.currencyCode = "NOK";
                line.vatCode = prod.sku;
                lines.add(line);
                bilags++;
            }
        }
        return lines;
    }

    private String findExternalAccountId(String code) {
        try {
            String param = URLEncoder.encode("(Code eq '"+code+"')", "ISO-8859-1");
            String addr = "http://api.poweroffice.net/customer?$filter="+param;
            String result = webManager.htmlPostBasicAuth(addr, null, false, "ISO-8859-1", token, "Bearer", false, "GET");
            logPrint(result);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
        
    }

    @Override
    boolean isUsingProductTaxCodes() {
        return true;
    }

    @Override
    boolean isPrimitive() {
        return false;
    }
}
