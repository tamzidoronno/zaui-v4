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

        Application brainTree = createSettings("BrainTree",
                "542e6a1e-9927-495c-9b6d-bb52af4ea9be",
                allowed2,
                "",
                Application.Type.Payment, true);
        brainTree.isPublic = true;
        brainTree.moduleId = "WebShop";
        brainTree.type = "PaymentApplication";
        brainTree.activeAppOnModuleActivation = false;
        apps.add(brainTree);


//        Application getShopAdmin = createSettings(
//                "GetShopAdmin",
//                "d315510d-198f-4c16-beef-54f979be58cf",
//                allowed,
//                "",
//                Application.Type.Webshop, true);
//        getShopAdmin.isPublic = true;
//        getShopAdmin.defaultActivate = true;
//        getShopAdmin.moduleId = "hidden";
//        apps.add(getShopAdmin);
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

        if (args == null || args.length == 0) {
            java.lang.System.exit(1);
        }
    }

}
