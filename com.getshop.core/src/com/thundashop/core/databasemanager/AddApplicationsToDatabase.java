
package com.thundashop.core.databasemanager;

import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import java.net.UnknownHostException;
import java.util.ArrayList;
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

    ArrayList<String> emtpy = new ArrayList();
    @Autowired
    public Database database;

    @SuppressWarnings("empty-statement")
    private Application createSettings(String appName, String id, List<String> allowedAreas, String description, String type, boolean isSingleton) {
        Application applicationSettings = new Application();
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
    
    private List<Application> addApplications() {
        List<Application> apps = new ArrayList();
        
        List<String> allowed = new ArrayList();
        allowed.add("large");
        allowed.add("xlarge");
        
        List<String> allowed2 = new ArrayList();
        allowed2.add("cell");
        allowed2.add("small");
        allowed2.add("medium");
        allowed2.add("large");
        allowed2.add("xlarge");

        Application dashBoard = createSettings("DashBoard",
                "b81bfb16-8066-4bea-a3c6-c155fa7119f8",
                allowed,
                "",
                Application.Type.Marketing, true);
        dashBoard.isSingleton = true;
        dashBoard.isPublic = true;
		dashBoard.defaultActivate = true;
        apps.add(dashBoard);
		
        Application menu = createSettings("Menu",
                "a11ac190-4f9a-11e3-8f96-0800200c9a66",
                allowed2,
                "",
                Application.Type.Webshop, true);
        menu.isSingleton = true;
        menu.isPublic = true;
		menu.defaultActivate = true;
        apps.add(menu);
		
        Application products = createSettings("Products",
                "e073a75a-87c9-4d92-a73a-bc54feb7317f",
                allowed2,
                "",
                Application.Type.Webshop, true);
        products.isSingleton = true;
        products.isPublic = true;
		products.moduleId = "WebShop";
		products.activeAppOnModuleActivation = true;
        apps.add(products);
		
        Application applicationSelector = createSettings("ApplicationSelector",
                "f1fc4af3-656e-4294-a268-40d2a82d0aa1",
                allowed2,
                "",
                Application.Type.System, true);
        applicationSelector.isSingleton = true;
        applicationSelector.isPublic = true;
        applicationSelector.defaultActivate = true;
        apps.add(applicationSelector);
		
        Application yellowCandyTheme = createSettings("YellowCandyTheme",
                "efe5640f-64fe-4053-a0de-508349465cdc",
                allowed2,
                "",
                Application.Type.Theme, true);
        yellowCandyTheme.isPublic = true;
        yellowCandyTheme.defaultActivate = true;
        apps.add(yellowCandyTheme);
		
        Application pcstoretheme = createSettings("PcStoreTheme",
                "e70c1a0a-fc3d-4ffe-817f-f09dc679199f",
                allowed2,
                "",
                Application.Type.Theme, true);
        pcstoretheme.isPublic = true;
        pcstoretheme.defaultActivate = true;
        apps.add(pcstoretheme);
		
        Application productLists = createSettings("ProductLists",
                "f245b8ae-f3ba-454e-beb4-ecff5ec328d6",
                allowed2,
                "",
                Application.Type.Webshop, true);
        productLists.isPublic = true;
        productLists.activeAppOnModuleActivation = true;
        apps.add(productLists);
        
        Application ecomerceSettings = createSettings("ECommerceSettings",
                "9de54ce1-f7a0-4729-b128-b062dc70dcce",
                new ArrayList(),
                "",
                Application.Type.Webshop, true);
        ecomerceSettings.isPublic = true;
        ecomerceSettings.moduleId = "WebShop";
        ecomerceSettings.activeAppOnModuleActivation = true;
        apps.add(ecomerceSettings);
		
		
//        ApplicationSettings sedoxMenu = createSettings(
//                "SedoxMenu",
//                "b23a3767-1f7b-40e3-93c5-65504ebaa73c",
//                allowed2,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        sedoxMenu.isPublic = false;
//        sedoxMenu.allowedStoreIds = new ArrayList();
//        sedoxMenu.allowedStoreIds.add("608afafe-fd72-4924-aca7-9a8552bc6c81");
//        apps.add(sedoxMenu);
//        
//        ApplicationSettings sedoxLogin = createSettings(
//                "SedoxLogin",
//                "05b2baef-5fba-4f01-9fcb-04a8c80b2907",
//                allowed2,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        sedoxLogin.isPublic = false;
//        sedoxLogin.allowedStoreIds = new ArrayList();
//        sedoxLogin.allowedStoreIds.add("608afafe-fd72-4924-aca7-9a8552bc6c81");
//        apps.add(sedoxLogin);
//        
//        ApplicationSettings sedoxHeaderApp = createSettings(
//                "SedoxHeaderApp",
//                "264b0edf-b654-4ce0-9be2-0ebb3d2887af",
//                new ArrayList(),
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        sedoxHeaderApp.isPublic = false;
//        sedoxHeaderApp.allowedStoreIds = new ArrayList();
//        sedoxHeaderApp.allowedStoreIds.add("608afafe-fd72-4924-aca7-9a8552bc6c81");
//        apps.add(sedoxHeaderApp);
//        
//        
//        ApplicationSettings sedoxAdmin = createSettings(
//                "SedoxAdmin",
//                "e22e25dd-8000-471c-89a3-6927d932165e",
//                allowed,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        sedoxAdmin.isPublic = false;
//        sedoxAdmin.allowedStoreIds = new ArrayList();
//        sedoxAdmin.allowedStoreIds.add("608afafe-fd72-4924-aca7-9a8552bc6c81");
//        apps.add(sedoxAdmin);
//        
//        ApplicationSettings sedoxProductView = createSettings(
//                "SedoxProductView",
//                "23fac58b-5066-4222-860c-a9e88196b8a1",
//                allowed,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        sedoxProductView.isPublic = false;
//        sedoxProductView.allowedStoreIds = new ArrayList();
//        sedoxProductView.allowedStoreIds.add("608afafe-fd72-4924-aca7-9a8552bc6c81");
//        apps.add(sedoxProductView);
//        
//        ApplicationSettings sedoxUserPanel = createSettings(
//                "SedoxUserPanel",
//                "32b5f680-dd8d-11e3-8b68-0800200c9a66",
//                allowed,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        sedoxUserPanel.isPublic = false;
//        sedoxUserPanel.allowedStoreIds = new ArrayList();
//        sedoxUserPanel.allowedStoreIds.add("608afafe-fd72-4924-aca7-9a8552bc6c81");
//        apps.add(sedoxUserPanel);


        return apps;
    }

    public void insert() throws ErrorException {
        Credentials credentials = new Credentials();
        credentials.manangerName = "ApplicationPool";
        credentials.password = "ADFASDF";
        credentials.storeid = "all";

        for (Application app : addApplications()) {
            app.storeId = "all";
            database.save(app, credentials);
        }
    }

    public void showLinks() {
        for (Application app : addApplications()) {
            System.out.println("ln -s ../../../applications/apps/" + app.appName + " " + "ns_" + app.id.replace("-", "_"));
        }
        System.out.println("Or for kai: ");
        for (Application app : addApplications()) {
            System.out.println("ln -s ../../../com.getshop.applications/apps/" + app.appName + " " + "ns_" + app.id.replace("-", "_"));
        }

    }

    public static void main(String args[]) throws ErrorException, UnknownHostException {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        
        context.getBean(AddApplicationsToDatabase.class).insert();
        context.getBean(AddApplicationsToDatabase.class).showLinks();
        
        java.lang.System.exit(1);
    }

}
