/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.google.gson.Gson;
import com.powerofficego.data.AccessToken;
import com.powerofficego.data.ApiCustomerResponse;
import com.powerofficego.data.ApiOrderTransferResponse;
import com.powerofficego.data.Customer;
import com.powerofficego.data.PowerOfficeGoOrder;
import com.powerofficego.data.PowerOfficeGoProduct;
import com.powerofficego.data.PowerOfficeGoSalesOrderLines;
import com.powerofficego.data.SalesOrderTransfer;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.common.GetShopLogging;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import static org.apache.poi.hssf.usermodel.HeaderFooter.file;

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
            System.out.println("Failed to fetch access token");
            return null;
        }
        
        System.out.println("Access token: " + token);
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
                products.put(product.id, product);
            }
        }
        
        for(Product product : products.values()) {
            if(!createUpdateProduct(product)) {
                System.out.println("Failed to transfer products");
                return null;
            }
        }
        
        for(User user : users.values()) {
            System.out.println("Need to transfer / update user : " + user.fullName);
            if(!createUpdateUser(user)) {
                System.out.println("faield to transfer user");
                return null;
            }
        }

        SavedOrderFile file = new SavedOrderFile();
        if(transferOrders(orders)) {
            file.orders= new ArrayList();
            for(Order ord : orders) {
                file.orders.add(ord.id);
            }
        }
        
        return file;
    }

    private boolean createUpdateUser(User user) {
        String endpoint = "http://api.poweroffice.net/customer/";
        Customer customer = new Customer();
        customer.setUser(user);

        Gson gson = new Gson();
        try {
            String htmlType = "POST";
            customer.code = (new Integer(customer.code) + 1000) + "";
            String data = gson.toJson(customer);
            String result = managers.webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, htmlType);
            ApiCustomerResponse resp = gson.fromJson(result, ApiCustomerResponse.class);
            if(resp.success) {
                user.accountingId = resp.data.id + "";
                managers.userManager.saveUser(user);
                return true;
            } else {
                /* @TODO HANDLE PROPER WARNING */
                System.out.println("Failed to transfer customer: " + result);
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

    private boolean transferOrders(List<Order> orders) {
        String endpoint = "http://api.poweroffice.net/Import/";
        List<PowerOfficeGoOrder> ordersToTransfer = new ArrayList();
        for(Order order : orders) {
            User user = managers.userManager.getUserById(order.userId);
            PowerOfficeGoOrder goOrder = new PowerOfficeGoOrder();
            goOrder.customerCode = new Integer(user.accountingId);
            goOrder.mergeWithPreviousOrder = false;
            goOrder.salesOrderLines = new ArrayList();
            if(order.cart != null) {
                for(CartItem item : order.cart.getItems()) {
                    PowerOfficeGoSalesOrderLines line = new PowerOfficeGoSalesOrderLines();
                    line.description = createLineText(item);
                    line.productCode = managers.productManager.getProduct(item.getProduct().id).accountingSystemId;
                    line.quantity = item.getCount();
                    line.salesOrderLineUnitPrice = item.getProduct().priceExTaxes;
                }
            }
            ordersToTransfer.add(goOrder);
        }
        SalesOrderTransfer transferObject = new SalesOrderTransfer();
        transferObject.salesOrders = ordersToTransfer;
        
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
                    return true;
                }
            } else {
                /* @TODO HANDLE PROPER WARNING */
                System.out.println("Failed to transfer customer: " + result);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        return false;
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
    

    private boolean createUpdateProduct(Product product) {
        String endpoint = "http://api.poweroffice.net/Product/";
        
        PowerOfficeGoProduct toUpdate = new PowerOfficeGoProduct();
        toUpdate.insertProduct(product);

        Gson gson = new Gson();
        try {
            String htmlType = "POST";

            String data = gson.toJson(toUpdate);
            String result = managers.webManager.htmlPostBasicAuth(endpoint, data, true, "ISO-8859-1", token, "Bearer", false, htmlType);
            ApiCustomerResponse resp = gson.fromJson(result, ApiCustomerResponse.class);
            if(resp.success) {
                product.accountingSystemId = resp.data.id + "";
                managers.productManager.saveProduct(product);
                return true;
            } else {
                /* @TODO HANDLE PROPER WARNING */
                System.out.println("Failed to transfer customer: " + result);
            }
        }catch(Exception e) {
            /* @TODO HANDLE PROPER WARNING */
            e.printStackTrace();
        }
        return false;
    }

}
