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
import com.thundashop.core.common.ForAccountingSystem;
import com.thundashop.core.common.GetShopLogging;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
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
        for(Order order : orders) {
            User user = managers.userManager.getUserById(order.userId);
            System.out.println("Need to transfer / update user : " + user.fullName);
            createUpdateUser(user);
            transferOrder(order);
        }
        return new SavedOrderFile();
    }

    private void createUpdateUser(User user) {
        String type = "POST";
        String endpoint = "http://api.poweroffice.net/customer/";
        if(!user.accountingId.isEmpty()) {
            type = "PUT";
        }
        Customer customer = new Customer();
        customer.setUser(user);

        Gson gson = new Gson();
        try {
            String htmlType = "POST";
            if(user.accountingId != null && !user.accountingId.isEmpty()) {
                htmlType = "PUT";
            }
            String data = gson.toJson(customer);
            String result = managers.webManager.htmlPostBasicAuth(endpoint, data, true, "UTF-8", token, "Bearer", false, htmlType);
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
