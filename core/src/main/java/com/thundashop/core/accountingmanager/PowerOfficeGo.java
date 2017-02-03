/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

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
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@ForAccountingSystem(accountingSystem="poweroffice")
public class PowerOfficeGo extends AccountingTransferOptions implements AccountingTransferInterface {
    private String token;

    @Override
    public void setManagers(AccountingManagers managers) {
        this.managers = managers;
    }

    @Override
    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public void setConfig(AccountingTransferConfig config) {
        this.config = config;
    }

    @Override
    public SavedOrderFile generateFile() {
        this.token = createAccessToken();
        if(token == null || token.isEmpty()) {
            /* @TODO HANDLE PROPER WARNING */
            managers.webManager.logPrint("Failed to fetch access token, authentication failed.");
            return null;
        }
        
        managers.webManager.logPrint("Access token: " + token);
        HashMap<String, User> users = new HashMap();
        HashMap<String, Product> products = new HashMap();
        
        for(Order order : orders) {
            if(getUniqueCustomerIdForOrder(order) == null) {
                User user = managers.userManager.getUserById(order.userId);
                users.put(user.id, user);
            }
        }
        
        for(Order order : orders) {
            if(order.cart == null) {
                continue;
            }
            for(CartItem item : order.cart.getItems()) {
                Product product = managers.productManager.getProduct(item.getProduct().id);
                if(product.accountingSystemId == null || product.accountingSystemId.isEmpty()) {
                    managers.webManager.logPrint("Failed to since product id is missing on product : " + product.name + " order: " + order.incrementOrderId);
                    return null;
                }
                products.put(product.id, product);
            }
        }
        
        for(User user : users.values()) {
            if(!createUpdateUser(user)) {
                managers.webManager.logPrint("failed Transferring user, accounting id " + user.accountingId + ", name" + user.fullName + ", customerid " + user.customerId);
                return null;
            } else {
                managers.webManager.logPrint("Transferred user, accounting id " + user.accountingId + ", name" + user.fullName + ", customerid " + user.customerId);
            }
        }

        SavedOrderFile file = new SavedOrderFile();
        String transferId = transferOrders(orders);
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
            user.externalAccountingId = findExternalAccountId(user.accountingId);
            
        }
        customer.setUser(user);
        customer.code = getAccountingId(user.id) + "";
        Gson gson = new Gson();
        try {
            String htmlType = "POST";
            String data = gson.toJson(customer);
            String result = managers.webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, htmlType);
            ApiCustomerResponse resp = gson.fromJson(result, ApiCustomerResponse.class);
            if(resp.success) {
                user.accountingId = customer.code + "";
                user.externalAccountingId = resp.data.id + "";
                managers.userManager.saveUser(user);
                return true;
            } else {
                /* @TODO HANDLE PROPER WARNING */
                managers.webManager.logPrint("Failed to transfer customer: " + result + "(" + user.customerId + " - " + user.fullName);
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
            String auth = "e5fdab3b-97b5-4041-bddb-5b2a48ccee1c:" + config.password;
            String res = managers.webManager.htmlPostBasicAuth("https://go.poweroffice.net/OAuth/Token", "grant_type=client_credentials", false, "UTF-8", auth);
            AccessToken token = gson.fromJson(res, AccessToken.class);
            return token.access_token;
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private String transferOrders(List<Order> orders) {
        String endpoint = "http://api.poweroffice.net/Import/";
        List<PowerOfficeGoSalesOrder> salesOrdersToTransfer = new ArrayList();
        List<PowerOfficeGoImportLine> importLinesToTransfer = new ArrayList();
        for(Order order : orders) {
            if(order.isInvoice()) {
                PowerOfficeGoSalesOrder goOrder = createGoSalesOrderLine(order);
                salesOrdersToTransfer.add(goOrder);
            } else {
                if(order.cart.getItems().size() > 0) {
                    List<PowerOfficeGoImportLine> lines = createGoImportLine(order);
                    importLinesToTransfer.addAll(lines);
                }
            }
        }
        SalesOrderTransfer transferObject = new SalesOrderTransfer();
        transferObject.description = "getshop file of type : " + config.subType;
        transferObject.salesOrders = salesOrdersToTransfer;
        transferObject.importLines = importLinesToTransfer;
        
        Gson gson = new Gson();
        String data = gson.toJson(transferObject);
        try {
            String result = managers.webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, "POST");
            ApiOrderTransferResponse resp = gson.fromJson(result, ApiOrderTransferResponse.class);
            if(resp.success) {
                for(Order order : orders) {
                    order.transferredToAccountingSystem = true;
                    order.dateTransferredToAccount = new Date();
                    managers.orderManager.saveOrder(order);
                    
//                    PowerOfficeGoPoster poster = new PowerOfficeGoPoster(resp.data, token);
//                    poster.start();
                    
                    return resp.data;
                }
            } else {
                /* @TODO HANDLE PROPER WARNING */
                managers.webManager.logPrint("Failed to transfer customer: " + result);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private PowerOfficeGoSalesOrder createGoSalesOrderLine(Order order) throws ErrorException, NumberFormatException {
        String uniqueId = getUniqueCustomerIdForOrder(order);
        PowerOfficeGoSalesOrder goOrder = new PowerOfficeGoSalesOrder();
        if(uniqueId == null) {
            User user = managers.userManager.getUserById(order.userId);
            goOrder.customerCode = new Integer(user.accountingId);
        } else {
            goOrder.customerCode = new Integer(uniqueId);
        }
        goOrder.reference = order.incrementOrderId + "";
        goOrder.mergeWithPreviousOrder = false;
        goOrder.salesOrderLines = new ArrayList();
        goOrder.orderNo = (int)order.incrementOrderId;
        if(order.cart != null) {
            for(CartItem item : order.cart.getItems()) {
                PowerOfficeGoSalesOrderLines line = new PowerOfficeGoSalesOrderLines();
                line.description = createLineText(item);
                line.productCode = managers.productManager.getProduct(item.getProduct().id).accountingSystemId;
                line.quantity = item.getCount();
                line.salesOrderLineUnitPrice = item.getProduct().priceExTaxes;
                goOrder.salesOrderLines.add(line);
            }
        }
        return goOrder;
    }
    
    
    private String createLineText(CartItem item) {
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
        
        if(!item.getProduct().additionalMetaData.isEmpty()) {
            lineText = item.getProduct().name + " " + item.getProduct().additionalMetaData + " (" + startDate + " - " + endDate + ")";
        } else {
            lineText = item.getProduct().name + " " + item.getProduct().metaData + " (" + startDate + " - " + endDate + ")";
        }
         
        return lineText;
    }
    

    private List<PowerOfficeGoImportLine> createGoImportLine(Order order) {
        int bilags = 1;
        List<PowerOfficeGoImportLine> lines = new ArrayList();
        Calendar cal = Calendar.getInstance();
        cal.setTime(order.rowCreatedDate);
        cal.add(Calendar.DAY_OF_YEAR, 14);
        Date endDate = order.getEndDateByItems();
        if(endDate == null) {
            endDate = order.rowCreatedDate;
        }
        
        
        PowerOfficeGoImportLine totalline = new PowerOfficeGoImportLine();
        totalline.description = "GetShop order: " + order.incrementOrderId;
        totalline.invoiceNo = (int)order.incrementOrderId;
        totalline.amount = managers.orderManager.getTotalAmount(order);
        totalline.currencyAmount = managers.orderManager.getTotalAmount(order);
        totalline.postingDate = endDate;
        totalline.documentDate = order.rowCreatedDate;
        totalline.documentNumber = (int)order.incrementOrderId;
        totalline.dueDate = cal.getTime();
        totalline.currencyCode = "NOK";
        
        String uniqueId = getUniqueCustomerIdForOrder(order);
        if(uniqueId != null) {
            totalline.customerCode = new Integer(uniqueId);
        } else {
            User user = managers.userManager.getUserById(order.userId);
            totalline.customerCode = new Integer(user.accountingId);
        }
        lines.add(totalline);

        bilags++;        
        if(order.cart != null) {
            for(CartItem item : order.cart.getItems()) {
                Product prod =  managers.productManager.getProduct(item.getProduct().id);
                PowerOfficeGoImportLine line = new PowerOfficeGoImportLine();
                line.accountNumber = new Integer(prod.accountingAccount);
                line.description = createLineText(item);
                line.productCode = prod.accountingSystemId;
                line.invoiceNo = (int)order.incrementOrderId;
                int count = item.getCount();
                line.amount = (item.getProduct().price * count) * -1;
                if(count < 0) {
                    count *= -1;
                }
                line.quantity = count;
                line.postingDate = endDate;
                line.documentDate = order.rowCreatedDate;
                line.documentNumber = (int)order.incrementOrderId;
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
            String result = managers.webManager.htmlPostBasicAuth(addr, null, false, "ISO-8859-1", token, "Bearer", false, "GET");
            System.out.println(result);
        }catch(Exception e) {
            e.printStackTrace();
        }
        return "";
        
    }


}
