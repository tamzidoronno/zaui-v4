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
        
        Application SedoxPartners = createSettings("SedoxPartners",
        "cb8e7f1e-8659-4e08-9e26-145c548d5873",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        SedoxPartners.isPublic = true;
        SedoxPartners.isFrontend = true;
        SedoxPartners.moduleId = "sedox";
        SedoxPartners.defaultActivate = false;
        apps.add(SedoxPartners);

        Application Company = createSettings("Company",
        "a6d68820-a8e3-4eac-b2b6-b05043c28d78",
        allowed2,
        " ",
        Application.Type.System, true);
        Company.isPublic = true;
        Company.isFrontend = true;
        Company.defaultActivate = true;
        Company.moduleId = "other";
        apps.add(Company);
        
        
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
        
        Application AutoTekAssistSaving = createSettings("AutoTekAssistSaving",
        "497d3093-47cf-4e38-bd2c-5117476f8409",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        AutoTekAssistSaving.isPublic = false;
        AutoTekAssistSaving.isFrontend = true;
        AutoTekAssistSaving.moduleId = "other";
        AutoTekAssistSaving.defaultActivate = false;
        AutoTekAssistSaving.allowedStoreIds.add("ba6ef325-58a4-4bee-ac56-6f1420a150f1");
        apps.add(AutoTekAssistSaving);

        Application SedoxFileUpload = createSettings("SedoxFileUpload",
        "92e3b7d6-c3ac-463e-90ba-4f966613f8dd",
        allowed2,
        " ",
        Application.Type.Marketing, true);
        SedoxFileUpload.isPublic = true;
        SedoxFileUpload.isFrontend = true;
        SedoxFileUpload.moduleId = "sedox";
        SedoxFileUpload.defaultActivate = false;
        apps.add(SedoxFileUpload);

        
        Application AutotekTheme = createSettings("AutotekTheme",
        "ca8104cd-92ac-4826-8bf0-09609a48f294",
        allowed2,
        " ",
        Application.Type.Theme, true);
        AutotekTheme.isPublic = false;
        AutotekTheme.isFrontend = false;
        AutotekTheme.moduleId = "other";
        AutotekTheme.defaultActivate = false;
        AutotekTheme.allowedStoreIds.add("ba6ef325-58a4-4bee-ac56-6f1420a150f1");
        apps.add(AutotekTheme);

        Application ProMeisterTheme = createSettings("ProMeisterTheme",
        "8a48c4d7-09e5-40e2-bdb3-72f706729d27",
        allowed2,
        " ",
        Application.Type.Theme, true);
        ProMeisterTheme.isPublic = false;
        ProMeisterTheme.isFrontend = false;
        ProMeisterTheme.moduleId = "other";
        ProMeisterTheme.defaultActivate = false;
        ProMeisterTheme.allowedStoreIds.add("6524eb45-fa17-4e8c-95a5-7387d602a69b");
        apps.add(ProMeisterTheme);

        Application XLedger = createSettings("XLedger",
        "54b33da0-11d2-48cb-afb2-2da4b2bdb7be",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        XLedger.isPublic = true;
        XLedger.isFrontend = false;
        XLedger.moduleId = "WebShop";
        XLedger.defaultActivate = false;
        apps.add(XLedger);

        
        Application PmsEventCalendar = createSettings("PmsEventCalendar",
        "27e174dc-b08c-4bf7-8179-9ea8379c91da",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsEventCalendar.isPublic = true;
        PmsEventCalendar.isFrontend = true;
        PmsEventCalendar.moduleId = "booking";
        PmsEventCalendar.defaultActivate = false;
        apps.add(PmsEventCalendar);

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
