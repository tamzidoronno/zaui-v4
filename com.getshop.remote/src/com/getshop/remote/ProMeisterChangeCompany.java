/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.remote;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.usermanager.data.User;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class ProMeisterChangeCompany {
    private String sessid;

    private String address;
    private String username;
    private String password;
    public GetShopApi api;

    
    public ProMeisterChangeCompany() throws Exception {
        sessid = UUID.randomUUID().toString();
        String serveraddr = "mecademo.getshop.com";
        System.out.println("Connecting to : " + serveraddr + " port : " + 5555);
        api = new GetShopApi(5555, "backend.getshop.com", sessid, "mecademo.getshop.com");
        try {
            User result = api.getUserManager().logOn("kjell.teige@autoakademiet.no", "abcd1234");
            if (!api.getUserManager().isLoggedIn()) {
                System.out.println("Failed to logon to backend");
                System.exit(0);
            } 
            
            User dag = api.getUserManager().getUserById("81f6e570-2e09-4bf2-a81d-5b678acb173f");
            dag.birthDay = "993804231";
            dag.company = api.getUtilManager().getCompanyFromBrReg("993804231");
            api.getUserManager().saveUser(dag);
            System.out.println("Connected to backend");
        } catch (Exception e) {
            e.printStackTrace();
        }

        
    }
    
    
    public static void main(String[] args) throws Exception {
        new ProMeisterChangeCompany();
    }
}
