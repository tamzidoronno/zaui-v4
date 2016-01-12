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
        List<String> allowed2 = new ArrayList();
        allowed2.add("cell");
        allowed2.add("small");
        allowed2.add("medium");
        allowed2.add("large");
        allowed2.add("xlarge");
        
        Application SedoxUploadHistory = createSettings("SedoxUploadHistory",
        "783ecee4-382b-4803-ba54-046ddd1ecf7e",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        SedoxUploadHistory.isPublic = true;
        SedoxUploadHistory.isFrontend = true;
        SedoxUploadHistory.moduleId = "sedox";
        SedoxUploadHistory.defaultActivate = false;
        apps.add(SedoxUploadHistory);

        Application SedoxFiluploadCount = createSettings("SedoxFiluploadCount",
        "ae288b19-9934-4e34-ade7-e8abba43b477",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        SedoxFiluploadCount.isPublic = true;
        SedoxFiluploadCount.isFrontend = true;
        SedoxFiluploadCount.moduleId = "sedox";
        SedoxFiluploadCount.defaultActivate = false;
        apps.add(SedoxFiluploadCount);

        Application SedoxCreditHistory = createSettings("SedoxCreditHistory",
        "07c7565c-040d-4425-962d-a326c782bb4e",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        SedoxCreditHistory.isPublic = true;
        SedoxCreditHistory.isFrontend = true;
        SedoxCreditHistory.moduleId = "sedox";
        SedoxCreditHistory.defaultActivate = false;
        apps.add(SedoxCreditHistory);

        Application SedoxSearchResult = createSettings("SedoxSearchResult",
        "366dbe67-0829-47c8-b6ed-47bc3a136338",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        SedoxSearchResult.isPublic = true;
        SedoxSearchResult.isFrontend = true;
        SedoxSearchResult.moduleId = "sedox";
        SedoxSearchResult.defaultActivate = false;
        apps.add(SedoxSearchResult);

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
