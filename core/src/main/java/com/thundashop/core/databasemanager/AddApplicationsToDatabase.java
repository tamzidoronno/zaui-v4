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

        
        Application questBackManagement = createSettings("QuestBackManagement",
                "3ff6088a-43d5-4bd4-a5bf-5c371af42534",
                allowed2,
                "",
                Application.Type.Marketing, true);
        questBackManagement.isPublic = false;
        questBackManagement.isFrontend = true;
        questBackManagement.moduleId = "Marketing";
        questBackManagement.defaultActivate = false;
        questBackManagement.allowedStoreIds = new ArrayList();
        questBackManagement.allowedStoreIds.add("2ded07b4-45f8-45d9-95dc-dc43b20d21bb");
        apps.add(questBackManagement);
        
        Application questBack = createSettings("QuestBack",
                "07422211-7818-445e-9f16-ad792320cb10",
                allowed2,
                "",
                Application.Type.Marketing, true);
        questBack.isPublic = false;
        questBack.isFrontend = true;
        questBack.moduleId = "Marketing";
        questBack.defaultActivate = false;
        questBack.allowedStoreIds = new ArrayList();
        questBack.allowedStoreIds.add("2ded07b4-45f8-45d9-95dc-dc43b20d21bb");
        apps.add(questBack);
        
        Application createNewInstanceApplication = createSettings("CreateNewInstanceApplication",
                "fb19a166-5465-4ae7-9377-779a8edb848e",
                allowed2,
                "",
                Application.Type.Marketing, true);
        createNewInstanceApplication.isPublic = true;
        createNewInstanceApplication.isFrontend = true;
        createNewInstanceApplication.moduleId = "other";
        createNewInstanceApplication.defaultActivate = false;
        apps.add(createNewInstanceApplication);
        
        Application ArtistDugnadTheme = createSettings("ArtistDugnadTheme",
        "e5f96f95-f61d-4316-94a8-bfdb1a6d5a88",
        allowed2,
        " ",
        Application.Type.Theme, true);
        ArtistDugnadTheme.isPublic = false;
        ArtistDugnadTheme.isFrontend = false;
        ArtistDugnadTheme.moduleId = "other";
        ArtistDugnadTheme.defaultActivate = false;
        ArtistDugnadTheme.allowedStoreIds = new ArrayList();
        ArtistDugnadTheme.allowedStoreIds.add("31a70f56-5e43-4560-ad38-a8c5c555a45e");
        apps.add(ArtistDugnadTheme);

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
