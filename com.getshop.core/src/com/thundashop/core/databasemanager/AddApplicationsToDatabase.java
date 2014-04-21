
package com.thundashop.core.databasemanager;

import com.thundashop.core.appmanager.ApplicationPool;
import com.thundashop.core.appmanager.data.ApplicationSettings;
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

        ApplicationSettings hotelbooking = createSettings(
                "Hotelbooking",
                "d16b27d9-579f-4d44-b90b-4223de0eb6f2",
                emtpy,
                "",
                ApplicationSettings.Type.Webshop, false);
        hotelbooking.isPublic = false;
        apps.add(hotelbooking);
//
//        List<String> allowed = new ArrayList();
//        allowed.add("large");
//        allowed.add("xlarge");
//        
//        ApplicationSettings newsLetterApp = createSettings(
//                "NewsLetter",
//                "adbdc94d-a201-4586-aa07-1e4d46ca0fd6",
//                allowed,
//                "",
//                ApplicationSettings.Type.Marketing, false);
//        newsLetterApp.isPublic = true;
//        apps.add(newsLetterApp);
//
//        ApplicationSettings bottomButton = createSettings(
//                "BottomButton",
//                "1081551b-1ecc-46cd-a192-63567ce129ab",
//                emtpy,
//                "",
//                ApplicationSettings.Type.Webshop, false);
//        bottomButton.isPublic = true;
//        apps.add(bottomButton);
       
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
