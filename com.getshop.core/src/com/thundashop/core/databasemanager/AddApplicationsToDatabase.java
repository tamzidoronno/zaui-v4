/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.appmanager.ApplicationPool;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.pagemanager.data.PageArea;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class AddApplicationsToDatabase {
    
    String Marketing = "MarketingApplication";
    String System = "SystemApplication";
    String Webshop = "WebshopApplication";
    String Payment = "PaymentApplication";
    String Reporting = "ReportingApplication";
    String Shipment = "ShipmentApplication";
    
    ArrayList<String> emtpy = new ArrayList();
    
    @Autowired
    public Database database;
    
    
    @SuppressWarnings("empty-statement")
    private ApplicationSettings createSettings(String appName, String id, List<String> allowedAreas, String description, String type) {
        ApplicationSettings applicationSettings = new ApplicationSettings();
        applicationSettings.appName = appName;
        applicationSettings.allowedAreas = allowedAreas;
        applicationSettings.id = id;
        applicationSettings.description = description;
        applicationSettings.price = 0.0;
        applicationSettings.type = type;
        applicationSettings.isPublic = true;
        applicationSettings.ownerStoreId = "cdae85c1-35b9-45e6-a6b9-fd95c18bb291";
        applicationSettings.userId = "3241047c-4c78-4465-a0ae-588111c570ff";
        return applicationSettings;
    }
    
    private List<ApplicationSettings> addApplications() {
        List<ApplicationSettings> apps = new ArrayList();
        apps.add(createSettings(
                "StockControl", 
                "a93d64e4-b7fa-4d55-a804-ea664b037e72", 
                emtpy, 
                "This application gives you the ability to control your stocks, simply change how many you have in stock on each product.", 
                Marketing));
        
        apps.add(createSettings(
                "Users", 
                "ba6f5e74-87c7-4825-9606-f2d3c93d292f", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "", 
                System));
        
        apps.add(createSettings(
                "Facebook", 
                "ba885f72-f571-4a2e-8770-e91cbb16b4ad", 
                emtpy, 
                "", 
                Marketing));
        
        apps.add(createSettings(
                "MainMenu", 
                "bf35979f-6965-4fec-9cc4-c42afd3efdd7", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "PayPal", 
                "c7736539-4523-4691-8453-a6aa1e784fc1", 
                emtpy, 
                "", 
                Payment));
        
        apps.add(createSettings(
                "ApplicationDisplayer", 
                "c841f5a5-ecd5-4007-b9da-2c7538c07212", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "", 
                System));
        
        apps.add(createSettings(
                "News", 
                "dabb8a85-f593-43ec-bf0d-240467118a40", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "A news application makes you push news to your WebShop. And share to subscribers and push to facebook if the Facebook application has been added.", 
                System));
        
        apps.add(createSettings(
                "ProductManager", 
                "dcd22afc-79ba-4463-bb5c-38925468ae26", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "Add a product to a page, add description, price, title, and other attributes. And you are ready to start selling the product. Combine it with a left menu, a category list to create a flexible and powerful webshop.", 
                Webshop));
        
        apps.add(createSettings(
                "ColumnProducts", 
                "de0b9b83-a41d-4fca-a2d4-3d3945cc8b9e", 
                Arrays.asList(PageArea.Type.RIGHT, PageArea.Type.LEFT), 
                "Add this left or right column at a product, and a new related products will be displayed.", 
                Webshop));
        
        apps.add(createSettings(
                "Login", 
                "df435931-9364-4b6a-b4b2-951c90cc0d70", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "Dibs", 
                "d02f8b7a-7395-455d-b754-888d7d701db8", 
                emtpy, 
                "", 
                Payment));
        
        apps.add(createSettings(
                "Footer", 
                "d54f339d-e1b7-412f-bc34-b1bd95036d83", 
                Arrays.asList(PageArea.Type.BOTTOM), 
                "", 
                System));
        
        apps.add(createSettings(
                "Banner", 
                "d612904c-8e44-4ec0-abf9-c03b62159ce4", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "This is the perfect application if you need to stash your page a bit more, simply create banners and add them to this application and then it will rotate", 
                Webshop));
        
        apps.add(createSettings(
                "Settings", 
                "d755efca-9e02-4e88-92c2-37a3413f3f41", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "Translation", 
                "ee1f3649-cfd8-41d5-aa5b-682216f376b6", 
                emtpy, 
                "Not satisfied with the text \\\"hardcoded\\\" on your page?<br>Add this application and tune the text yourself.", 
                Reporting));
        
        apps.add(createSettings(
                "CreateStore", 
                "e2554f70-ecdb-47a6-ba37-79497ea65986", 
                emtpy, 
                "Internal usage for create webshops", 
                System));
        
        apps.add(createSettings(
                "Gallery", 
                "e72f97ad-aa1f-4e67-bcfd-e64607f05f93", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "This gallery gives you the ability to display images into a gallery, and even connect the images to products, categorise them by combining a category displayer, or by using a left menu.", 
                Marketing));
        
        apps.add(createSettings(
                "CategoryLister", 
                "e9d04f19-6eaa-4a17-9b7b-aa387dbaed92", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "Create categories and add a list of products to the category, attach images to the categories, and name them as you want.", 
                Webshop));
        
        apps.add(createSettings(
                "WebShopList", 
                "e9864616-96d6-485f-8cb0-e17cdffbcfec", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "OpenSRS", 
                "fb076580-c7df-471c-b6b7-9540e4212441", 
                emtpy, 
                "OpenSRS is a domain name API.", 
                System));
        
        apps.add(createSettings(
                "GoogleAnalytics", 
                "0cf21aa0-5a46-41c0-b5a6-fd52fb90216f", 
                emtpy, 
                "Google analytics is by far the best reporting tool for webadministrator, by adding this application you will enable your googla analytics account to communicate with your webshop.", 
                Reporting));
        
        apps.add(createSettings(
                "LeftMenu", 
                "00d8f5ce-ed17-4098-8925-5697f6159f66", 
                Arrays.asList(PageArea.Type.LEFT), 
                "Get started building yourself a left / right menu, you can do it by adding this application!<br><br>This menu support subentries and hardlinking, and for each entry a page is created where you can add products / other applications.", 
                Webshop));
        
        ApplicationSettings reporting = createSettings(
                "Reporting", 
                "04259325-abfa-4311-ab81-b89c60893ce1", 
                emtpy, 
                "Keep track of your products, orders, pages, etc with this application. Generate reports on a hourly, daily, weekly and monthly basis.", 
                Reporting);
        reporting.isSingleton = true;
        apps.add(reporting);
        
        apps.add(createSettings(
                "Shipper", 
                "098bb0fe-eb51-42c6-9fbb-dadb7b52dd56", 
                emtpy, 
                "this application gives you the ability to setup fixed shipping price", 
                Shipment));
        
        apps.add(createSettings(
                "TopMenu", 
                "1051b4cf-6e9f-475d-aa12-fc83a89d2fd4", 
                emtpy, 
                "A topmenu enables you to display top menu entries", 
                System));
        
        apps.add(createSettings(
                "Chat", 
                "2afb045b-fa01-4398-8582-33f212bb8db8", 
                emtpy, 
                "Answering your customers questions, means more sale. By adding this excellent chat application, you can answer on request at any time from you mobile devices or your stationary computer.", 
                Webshop));
        
        apps.add(createSettings(
                "Bring", 
                "2da52bbc-a392-4125-92b6-eec1dc4879e9", 
                emtpy, 
                "fraktguiden is a norwegian application for keeping track of norwegian shipment prices, shipment tracking etc.", 
                Shipment));
        
        apps.add(createSettings(
                "SedoxCarList", 
                "2ebd7c69-eba3-4b7e-85c6-0e0bd274aad0", 
                emtpy, 
                "This application displays tuning data for car, boats, trucks, tractors, etc", 
                Webshop));
        
        apps.add(createSettings(
                "OrderManager", 
                "27716a58-0749-4601-a1bc-051a43a16d14", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "ContentManager", 
                "320ada5b-a53a-46d2-99b2-9b0b26a7105a", 
                Arrays.asList(PageArea.Type.LEFT, PageArea.Type.MIDDLE, PageArea.Type.RIGHT), 
                "A content manager allows you to add content to whatever page you would like to add content to. It supports text, images, and even basic HTML, its like word for web.", 
                Webshop));
        
        ApplicationSettings smsStatic = createSettings(
                "SmsStatistic", 
                "39f1485a-85b8-4f09-ba70-0e33c16f8dc6", 
                emtpy, 
                "By adding this application you will see some static over your sms usage.", 
                Marketing);
        smsStatic.isSingleton = true;
        apps.add(smsStatic);
        
        apps.add(createSettings(
                "Account", 
                "6c245631-effb-4fe2-abf7-f44c57cb6c5b", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "Calendar", 
                "6f3bc804-02a1-44b0-a17d-4277f0c6dee8", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "Keep your customers up to date of what events you are hosting with this application", 
                Marketing));
        
        apps.add(createSettings(
                "Search", 
                "626ff5c4-60d4-4faf-ac2e-d0f21ffa9e87", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "Designs", 
                "636149b5-f3c9-4b63-99e1-83eeb5742e05", 
                emtpy, 
                "Our designs", 
                System));
        
        apps.add(createSettings(
                "BreadCrumb", 
                "7093535d-f842-4746-9256-beff0860dbdf", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "Booking", 
                "74ea4e90-2d5a-4290-af0c-230a66e09c78", 
                emtpy, 
                "give your customers ability to book into your events or appointments", 
                Marketing));
        
        apps.add(createSettings(
                "Mail", 
                "8ad8243c-b9c1-48d4-96d5-7382fa2e24cd", 
                emtpy, 
                "You need this application if you want this webshop send emails from your email account", 
                Marketing));
        
        apps.add(createSettings(
                "ProductList", 
                "8402f800-1e7e-43b5-b3f7-6c7cabbf8942", 
                emtpy, 
                "this applications lists products for you webshop, in the body of every page (except for system pages). from this application you can also add products. combine this application with a leftmenu, and/or a category displayer to create a powerful webshop.", 
                Webshop));
        
        apps.add(createSettings(
                "CartManager", 
                "900e5f6b-4113-46ad-82df-8dafe7872c99", 
                emtpy, 
                "", 
                System));
        
        apps.add(createSettings(
                "Contact", 
                "96de3d91-41f2-4236-a469-cd1015b233fc", 
                Arrays.asList(PageArea.Type.MIDDLE), 
                "Add this contact form, configure it, and you are ready to start receiving questions and feedback from your users per email.", 
                Webshop));
        
        apps.add(createSettings(
                "Logo", 
                "974beda7-eb6e-4474-b991-5dbc9d24db8e", 
                emtpy, 
                "", 
                System));

        return apps;
    }
    
    public void insert() throws ErrorException {
        Credentials credentials = new Credentials(ApplicationPool.class);
        credentials.manangerName = "ApplicationPool";
        credentials.password = "ADFASDF";
        credentials.storeid = "all";
        
        for (ApplicationSettings app : addApplications()) {
            app.storeId = "all";
            database.save(app, credentials);
        }
    }
    
    public static void main(String args[]) throws ErrorException {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        context.getBean(AddApplicationsToDatabase.class).insert();
        java.lang.System.exit(1);
    }
}
