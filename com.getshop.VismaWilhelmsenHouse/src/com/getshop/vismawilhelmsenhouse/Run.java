/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.vismawilhelmsenhouse;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.usermanager.data.User;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Run {
    private boolean local = true;
    private Integer backendport = 3224;
    private String apiAddress = "backend20.getshop.com";
    private String apiUsername = "kai@getshop.com";
    private String apiPassword = "g4kkg4kk";
    private String website = "20164.getshop.com";
    
    private String vismaPassword;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        Run run = new Run();
        run.setupConnectionsDetails();
        run.execute();
    }
    
    private void setupConnectionsDetails() {
        if (local) {
            backendport = 25554;
            apiAddress = "wilhelmsenhouse.2.0.local.getshop.com";
            apiUsername = "kai@getshop.com";
            apiPassword = "g4kkg4kk";
            website = "wilhelmsenhouse.2.0.local.getshop.com";    
            vismaPassword = "4748311484";
        } else {
            backendport = 3224;
            apiAddress = "backend20.getshop.com";
            apiUsername = "kai@getshop.com";
            apiPassword = "g4kkg4kk";
            website = "20164.getshop.com"; 
            vismaPassword = "4748311484";
        }
    }
    
    private void execute() throws Exception {
        String sessid = UUID.randomUUID().toString();
        System.out.println("Connecting to : " + apiAddress + " port : " + backendport);
        GetShopApi api = new GetShopApi(backendport, apiAddress, sessid, website);
        try {
            User result = api.getUserManager().logOn(apiUsername, apiPassword);
            if (!api.getUserManager().isLoggedIn()) {
                System.out.println("Failed to logon to backend");
                System.exit(0);
            }
            System.out.println("Connected to backend");
        } catch (Exception e) {
        }

        Visma visma = new Visma(api, vismaPassword);
        visma.parse();
    }

}