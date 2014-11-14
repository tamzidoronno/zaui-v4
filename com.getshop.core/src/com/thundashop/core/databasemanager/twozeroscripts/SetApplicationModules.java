/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.twozeroscripts;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.Credentials;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author boggi
 */
public class SetApplicationModules {

    public static void main(String[] args) throws UnknownHostException {
        Credentials cred = new Credentials();
        cred.manangerName = "ApplicationPool";
        cred.storeid = "all";
        cred.password = "asdf";
        
        Database database = new Database();
        List<Application> apps = database.getAll("ApplicationPool", "all")
                .map(o -> (Application)o)
                .collect(Collectors.toList());
        
        List<String> webshopApps = new ArrayList();
        webshopApps.add("PayPal");
        webshopApps.add("TermsAndConditions");
        webshopApps.add("Sisow");
        webshopApps.add("DIBS");
        webshopApps.add("Bring");
        webshopApps.add("PayOnDelivery");
        webshopApps.add("Coupon");
        webshopApps.add("InvoicePayment");
        webshopApps.add("StockControl");
        webshopApps.add("Proteria");
        webshopApps.add("ColumnProducts");
        
        List<String> webshopAppsMandatory = new ArrayList();
        webshopAppsMandatory.add("Taxes");
        webshopAppsMandatory.add("Product");
        webshopAppsMandatory.add("Shipper");
        webshopAppsMandatory.add("ProductLister");
        webshopAppsMandatory.add("ProductWidget");

        apps.stream().filter(app -> webshopApps.contains(app.appName)).forEach(app -> app.moduleId = "WebShop");
        apps.stream().filter(app -> webshopAppsMandatory.contains(app.appName)).forEach(app -> app.moduleId = "WebShop");
        apps.stream().filter(app -> webshopAppsMandatory.contains(app.appName)).forEach(app -> app.activeAppOnModuleActivation = true );
        
        List<String> cmsApps = new ArrayList();
        List<String> cmsAppsDefault = new ArrayList();
        
        
        cmsApps.add("MailManager");
        cmsApps.add("YouTube");
        cmsApps.add("GoogleMaps");
        cmsApps.add("Contact");
        cmsAppsDefault.add("ImageDisplayer");
        cmsAppsDefault.add("Menu");
        cmsAppsDefault.add("Banner");
        cmsAppsDefault.add("ContentManager");
        apps.stream().filter(app -> cmsApps.contains(app.appName)).forEach(app -> app.moduleId = "cms");
        apps.stream().filter(app -> cmsAppsDefault.contains(app.appName)).forEach(app -> app.moduleId = "cms");
        apps.stream().filter(app -> cmsAppsDefault.contains(app.appName)).forEach(app -> app.activeAppOnModuleActivation = true );
        
        List<String> marketingApps = new ArrayList();
        
        
        marketingApps.add("Clickatell");
        marketingApps.add("News");
        marketingApps.add("Facebook");
        marketingApps.add("Airgram");
        marketingApps.add("Chat");
        marketingApps.add("Calendar");
        marketingApps.add("NewsLetter");
        marketingApps.add("Booking");
        
        apps.stream().filter(app -> marketingApps.contains(app.appName)).forEach(app -> app.moduleId = "Marketing");
        
        
        List<String> reportingApps = new ArrayList();
       
        
        reportingApps.add("SmsStatistic");
        reportingApps.add("GoogleAnalytics");
        reportingApps.add("Reporting");
        apps.stream().filter(app -> reportingApps.contains(app.appName)).forEach(app -> app.moduleId = "reporting");
        
        apps.stream().forEach(app -> database.saveWithOverrideDeepfreeze(app, cred));
        
        
    }
}