/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
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
import com.thundashop.core.getshopaccounting.fikenservice.FikenInvoiceService;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PowerOfficeGoAccountingSystem extends AccountingSystemBase {

    private String token;

    @Override
    public List<SavedOrderFile> createFiles(List<Order> orders, Date start, Date end) {
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
                    logPrint("Failed to since product id is missing on product : " + product.name + " order: " + order.incrementOrderId);
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
        String endpoint = "http://api.poweroffice.net/customer/";
        Customer customer = new Customer();
        if((user.accountingId != null || user.accountingId.isEmpty()) && user.externalAccountingId == null || user.externalAccountingId.isEmpty()) {
            //Something is wrong here. There should be an externa account id connected to it.
//            user.externalAccountingId = findExternalAccountId(user.accountingId);
            
        }
        customer.setUser(user);
        customer.code = getAccountingAccountId(user.id) + "";
        Gson gson = new Gson();
        try {
            String htmlType = "POST";
            String data = gson.toJson(customer);
            if(!GetShopLogHandler.isDeveloper) {
                String result = webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, htmlType);
                ApiCustomerResponse resp = gson.fromJson(result, ApiCustomerResponse.class);
                if(resp.success) {
                    user.accountingId = customer.code + "";
                    user.externalAccountingId = resp.data.id + "";
                    userManager.saveUser(user);
                    return true;
                } else {
                    /* @TODO HANDLE PROPER WARNING */
                    addToLog("Failed to transfer customer: " + result + "(" + user.customerId + " - " + user.fullName);
                    addToLog(resp.summary + " : accounting id: " + user.accountingId + ", powerofficego id: " + user.externalAccountingId);
                } 
            } else {
                user.accountingId = customer.code + "";
                user.externalAccountingId = "1";
                return true;
            }
        }catch(Exception e) {
            /* @TODO HANDLE PROPER WARNING */
            e.printStackTrace();
        }
        return false;
    }

    private String createAccessToken() {
        try {
            Gson gson = new Gson();
            String auth = "e5fdab3b-97b5-4041-bddb-5b2a48ccee1c:" + getConfig("password"); //config.password;
            String res = webManager.htmlPostBasicAuth("https://go.poweroffice.net/OAuth/Token", "grant_type=client_credentials", false, "UTF-8", auth);
            AccessToken token = gson.fromJson(res, AccessToken.class);
            return token.access_token;
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String transferOrders(List<Order> orders, String subType) {
        String endpoint = "http://api.poweroffice.net/Import/";
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
                String result = webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, "POST");
                ApiOrderTransferResponse resp = gson.fromJson(result, ApiOrderTransferResponse.class);
                if(resp.success) {
                    for(Order order : orders) {
                        order.transferredToAccountingSystem = true;
                        order.dateTransferredToAccount = new Date();
                        orderManager.saveOrder(order);

                        return resp.data;
                    }
                } else {
                    /* @TODO HANDLE PROPER WARNING */
                    addToLog("Failed to transfer customer: " + result);
                }
            } else {
                addToLog("Did not transfer files to powerofficego due to developer mode");
            }
        }catch(Exception e) {
            e.printStackTrace();
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
                System.out.println("Not found user: " + order.userId);
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
                    addToLog("Product does not exists on order " + order.incrementOrderId);
                    continue;
                }
                if(prod.getAccountingAccount() == null) {
                    addToLog("Product : " + prod.name + " does not have an accounting number, orderid: " + order.incrementOrderId);
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
            System.out.println(result);
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
