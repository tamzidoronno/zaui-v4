/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.appmanager.ApplicationPool;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.storemanager.data.Store;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    
    ArrayList<String> emtpy = new ArrayList();
    
    @Autowired
    public Database database;
    
    
    @SuppressWarnings("empty-statement")
    private ApplicationSettings createSettings(String appName, String id, List<String> allowedAreas, String description, String type, boolean isSingleton) {
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
        applicationSettings.isSingleton = isSingleton;
        return applicationSettings;
    }
    
    private List<ApplicationSettings> addApplications() {
        List<ApplicationSettings> apps = new ArrayList();

        ApplicationSettings akad = createSettings(
                "ImageDisplayer", 
                "831647b5-6a63-4c46-a3a3-1b4a7c36710a", 
                emtpy, 
                "", 
                ApplicationSettings.Type.Webshop, false);
        akad.isPublic = true;
        apps.add(akad);
        
        ApplicationSettings mailApplication = createSettings(
                "MailManager", 
                "eb03c660-186a-11e3-8ffd-0800200c9a66", 
                emtpy, 
                "", 
                ApplicationSettings.Type.Marketing, true);
        mailApplication.isPublic = true;
        apps.add(mailApplication);

        ApplicationSettings blueEnergyTheme = createSettings(
                "BlueEnergyTheme", 
                "9ffcc6a0-130e-11e3-8ffd-0800200c9a66", 
                emtpy, 
                "", 
                ApplicationSettings.Type.Theme, false);
        blueEnergyTheme.isPublic = true;
        apps.add(blueEnergyTheme);
        
        ApplicationSettings sisow = createSettings(
                "Sisow", 
                "c4d7bec0-185f-11e3-8ffd-0800200c9a66", 
                emtpy, 
                "", 
                ApplicationSettings.Type.Payment, true);
        sisow.isPublic = true;
        
        ApplicationSettings shopshopdesign1 = createSettings(
                "ShopSoDesign1", 
                "d71779ff-1d32-4c45-86cc-e576ee103f1c", 
                emtpy, 
                "", 
                ApplicationSettings.Type.Theme, false);
        shopshopdesign1.isPublic = false;
        apps.add(shopshopdesign1);
        
        ApplicationSettings partnerselection = createSettings(
                "PartnerApplicationSelection", 
                "a109b0f1-97c3-4292-a46f-4c6894a9841c", 
                emtpy, 
                "", 
                ApplicationSettings.Type.System, false);
        partnerselection.isPublic = true;
        apps.add(partnerselection);
        
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
    
    public void showLinks() {
        for (ApplicationSettings app : addApplications()) {
            System.out.println("ln -s ../../../applications/apps/"+app.appName + " " + "ns_"+app.id.replace("-", "_"));
        }
        System.out.println("Or for kai: ");
        for (ApplicationSettings app : addApplications()) {
            System.out.println("ln -s ../../../com.getshop.applications/apps/"+app.appName + " " + "ns_"+app.id.replace("-", "_"));
        }
        
    }
    
    public static void main(String args[]) throws ErrorException, UnknownHostException {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
//        
        context.getBean(AddApplicationsToDatabase.class).insert();

//        context.getBean(AddApplicationsToDatabase.class).updateThemes();
//        context.getBean(AddApplicationsToDatabase.class).updateUserPages();
//        
//        AddPageSettingsIdToAppConfigurationObject.main(args);
        context.getBean(AddApplicationsToDatabase.class).showLinks();
        java.lang.System.exit(1);
    }
    
    
    private void updateUserPages() throws ErrorException {
        Credentials credentials = new Credentials(StoreManager.class);
        credentials.manangerName = "StoreManager";
        credentials.storeid = "all";
        credentials.password = "ADSFASDF";
        
        
        for (DataCommon data : database.retreiveData(credentials)) {
            if (data instanceof Store) {
                Store store = (Store)data;
                Credentials credentials2 = new Credentials(PageManager.class);
                credentials2.manangerName = "PageManager";
                credentials2.storeid = store.id;
                credentials2.password = "ADSFASDF";

                List<DataCommon> pages = database.retreiveData(credentials2);
                for (DataCommon pageData : pages) {
                    if (pageData instanceof AppConfiguration) {
                        AppConfiguration appConfig = (AppConfiguration)pageData;
                        if (appConfig.appName.equals("Users")) {
                            database.delete(appConfig, credentials2);
                        } 
                        
                        if (appConfig.appName.equals("Crm")) {
                            database.delete(appConfig, credentials2);
                        }
                        
                        if (appConfig.id.equals("users_admin_menu")) {
                            database.delete(appConfig, credentials2);
                        }
                        
                    }
                    if (pageData instanceof Page) {
                        Page page = (Page)pageData;
                        if (page.id.equals("users") || page.id.equals("users_all_users")) {
                            database.delete(page, credentials2);
                        }
                    }
                }
            }
        }
                
    }

    private void updateThemes() throws ErrorException {
        Credentials credentials = new Credentials(StoreManager.class);
        credentials.manangerName = "StoreManager";
        credentials.storeid = "all";
        credentials.password = "ADSFASDF";
        for (DataCommon data : database.retreiveData(credentials)) {
            if (data instanceof Store) {
                Store store = (Store)data;
                if (store.configuration != null && store.configuration.theeme != null) {
                    store.configuration.hasSelectedDesign = true;
                    database.save(store, credentials);
                    
                    if (store.configuration.theeme.equals("blueandwhite")) {
                        createNewApplication("WhiteAndBlueTheme", "a84cbbb0-8f21-11e2-9e96-0800200c9a66", store.id);
                    }
                    
                    if (store.configuration.theeme.equals("slick")) {
                        createNewApplication("SlickTheme", "efcbb450-8f26-11e2-9e96-0800200c9a66", store.id);
                    } 
                    
                    if (store.configuration.theeme.equals("thered")) {
                        createNewApplication("TheRedTheme", "d147f6a0-8f31-11e2-9e96-0800200c9a66", store.id);
                    } 
                    
                    if (store.configuration.theeme.equals("widescreen")) {
                        createNewApplication("WideScreenTheme", "c2da56a0-8f2f-11e2-9e96-0800200c9a66", store.id);
                    } 
                    
                    if (store.configuration.theeme.equals("getshop")) {
                        createNewApplication("GetShopTheme", "7a4f3750-895a-11e2-9e96-0800200c9a66", store.id);
                    } 
                    
                    if (store.configuration.theeme.equals("kingroids")) {
                        createNewApplication("KingroidsTheme", "161644b0-b095-11e2-9e96-0800200c9a66", store.id);
                    } 
                }
            }
        }
    }
    
    private void createNewApplication(String appName, String appsettingsid, String storeid) {
        AppConfiguration appConfiguration = new AppConfiguration();
        appConfiguration.sticky = 0;
        appConfiguration.appName = appName;
        appConfiguration.storeId = storeid;
        appConfiguration.appSettingsId = appsettingsid;
        Credentials credentials = new Credentials(PageManager.class);
        credentials.storeid = storeid;
        credentials.manangerName = "PageManager";
        credentials.password = "ASDFASFD";
        try {
            appConfiguration.id = UUID.randomUUID().toString();
            System.out.println(appConfiguration.appSettingsId);
            database.save(appConfiguration, credentials);
        } catch (ErrorException ex) {
            ex.printStackTrace();
        }
    }

}
