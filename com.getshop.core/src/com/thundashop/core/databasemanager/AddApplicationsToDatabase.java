package com.thundashop.core.databasemanager;

import com.thundashop.core.appmanager.ApplicationPool;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
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

        List<String> allowed = new ArrayList();
        allowed.add("large");
        allowed.add("xlarge");

        List<String> allowed2 = new ArrayList();
        allowed2.add("cell");
        allowed2.add("small");
        allowed2.add("medium");
        allowed2.add("large");
        allowed2.add("xlarge");

//        ApplicationSettings proMeisterLogin = createSettings(
//                "ProMeisterLogin",
//                "2f98236f-b36d-4d5c-93c6-0ad99e5b3dc6",
//                allowed2,
//                "",
//                ApplicationSettings.Type.System, true);
//        proMeisterLogin.isPublic = false;
//        proMeisterLogin.isResponsive = true;
//        proMeisterLogin.allowedStoreIds = new ArrayList();
//        proMeisterLogin.allowedStoreIds.add("d27d81b9-52e9-4508-8f4c-afffa2458488");
//        proMeisterLogin.allowedStoreIds.add("2fac0e57-de1d-4fdf-b7e4-5f93e3225445");
//        apps.add(proMeisterLogin);

//        
//        ApplicationSettings hotelvask = createSettings(
//                "HotelbookingCleaning",
//                "020b57c2-8f80-46e1-a420-cb163f4fb2d2",
//                allowed,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        hotelvask.isPublic = false;
//        apps.add(hotelvask);
//         
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
        ApplicationSettings promeisterUserOverview = createSettings(
                "ProMeisterUserOverview",
                "18013065-9122-4181-8ba7-8be3e0b5b445",
                allowed2,
                "",
                ApplicationSettings.Type.Webshop, false);
        promeisterUserOverview.isPublic = false;
        promeisterUserOverview.allowedStoreIds = new ArrayList();
        promeisterUserOverview.allowedStoreIds.add("d27d81b9-52e9-4508-8f4c-afffa2458488");
        promeisterUserOverview.allowedStoreIds.add("2fac0e57-de1d-4fdf-b7e4-5f93e3225445");
        apps.add(promeisterUserOverview);
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
        Credentials credentials = new Credentials(ApplicationPool.class);
        credentials.manangerName = "ApplicationPool";
        credentials.password = "ADFASDF";
        credentials.storeid = "all";

        for (ApplicationSettings app : addApplications()) {
            app.storeId = "all";
//
//            DataCommon data = null;
//            if (app.id != null) {
//                data = database.getObject(credentials, app.id);
//            }
////
//            if (data == null) {
                database.save(app, credentials);
//            } else {
//                System.out.println("Skipping " + app.appName + ", already exists");
//            }

        }
    }

    public void showLinks() {
        for (ApplicationSettings app : addApplications()) {
            System.out.println("ln -s ../../../applications/apps/" + app.appName + " " + "ns_" + app.id.replace("-", "_"));
        }
        System.out.println("Or for kai: ");
        for (ApplicationSettings app : addApplications()) {
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
