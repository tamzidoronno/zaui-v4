/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.remote;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.data.User;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class CleanupOrderMessSemLagerHotell {
    private String sessid;

    String webaddress = "www.semlagerhotell.no";
    private String host = "backend.getshop.com";
    private int port = 21; // port 5555
    
//    String webaddress = "20065.local.getshop.com";
//    private String host = "localhost";
//    private int port = 25554; // port 5555
    
    private String username = "boggibill@gmail.com";
    private String password = "gakkgakk";
    public GetShopApi api;
    
    public static void main(String[] args) throws Exception {
        new CleanupOrderMessSemLagerHotell().start(args);
    }
    
    public void start(String[] arsg) throws Exception {
        sessid = UUID.randomUUID().toString();
        
        System.out.println("Connecting to : " + webaddress + " port : " + port);
        api = new GetShopApi(port, host, sessid, webaddress);
        try {
            User result = api.getUserManager().logOn(username, password);
            if (!api.getUserManager().isLoggedIn()) {
                System.out.println("Failed to logon to backend");
                System.exit(0);
            } 
            
            
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.YEAR, 2015);
            cal.set(Calendar.MONTH, 8);
            cal.set(Calendar.DAY_OF_MONTH, 1);
            
            List<Order> orders = api.getOrderManager().getOrders(null, null, null);
            for (Order order : orders) {
                if(order.createdDate.after(cal.getTime()) && order.incrementOrderId != 20687 && order.incrementOrderId != 20691 && order.incrementOrderId != 20687) {
//                    System.out.println(order.createdDate);
//                    order.transferedToAccountingSystem = false;
//                    api.getOrderManager().saveOrder(order);
                    
                    User user = api.getUserManager().getUserById(order.userId);
                    user.isTransferredToAccountSystem = false;
                    api.getUserManager().saveUser(user);
                }
            }
            
            
//            Order order = api.getOrderManager().getOrder("19e3a5cc-18e2-42ae-a150-8fea538ddc9c");
//            order.reference = "1420";
//            api.getOrderManager().saveOrder(order);
//            api.getOrderManager().setAllOrdersAsTransferedToAccountSystem();
//            System.out.println("Success");
//            User dag = api.getUserManager().getUserById("81f6e570-2e09-4bf2-a81d-5b678acb173f");
//            dag.birthDay = "993804231";
//            dag.company = api.getUtilManager().getCompanyFromBrReg("993804231");
//            api.getUserManager().saveUser(dag);
//            System.out.println("Connected to backend");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
