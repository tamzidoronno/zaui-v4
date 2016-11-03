/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.google.gson.Gson;
import com.powerofficego.data.AccessToken;
import com.powerofficego.data.ApiCustomerResponse;
import com.powerofficego.data.Customer;
import com.powerofficego.data.PowerOfficeGoProduct;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.common.GetShopLogging;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ForAccountingSystem(accountingSystem="poweroffice")
public class PowerOfficeGo implements AccountingTransferInterface {

    private List<User> users;
    private List<Order> orders;
    private AccountingTransferConfig config;
    private AccountingManagers managers;
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
            User user = managers.userManager.getUserById(order.userId);
            users.put(user.id, user);
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
            createUpdateProduct(product);
        }
        
        for(User user : users.values()) {
            System.out.println("Need to transfer / update user : " + user.fullName);
            createUpdateUser(user);
        }

        for(Order order : orders) {
            transferOrder(order);
        }
        
        return new SavedOrderFile();
    }

    private void createUpdateUser(User user) {
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
            } else {
                /* @TODO HANDLE PROPER WARNING */
                System.out.println("Failed to transfer customer: " + result);
            }
        }catch(Exception e) {
            /* @TODO HANDLE PROPER WARNING */
            e.printStackTrace();
        }
    }

    private String createAccessToken() {
        try {
            Gson gson = new Gson();
            String auth = "e5fdab3b-97b5-4041-bddb-5b2a48ccee1c:07a89265-8ad7-425d-bdb1-d87e6c5af134";
            String res = managers.webManager.htmlPostBasicAuth("https://go.poweroffice.net/OAuth/Token", "grant_type=client_credentials", false, "UTF-8", auth);
            AccessToken token = gson.fromJson(res, AccessToken.class);
            return token.access_token;
        }catch(Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void transferOrder(Order order) {
    }

    private void createUpdateProduct(Product product) {
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
            } else {
                /* @TODO HANDLE PROPER WARNING */
                System.out.println("Failed to transfer customer: " + result);
            }
        }catch(Exception e) {
            /* @TODO HANDLE PROPER WARNING */
            e.printStackTrace();
        }
    }
    
}
