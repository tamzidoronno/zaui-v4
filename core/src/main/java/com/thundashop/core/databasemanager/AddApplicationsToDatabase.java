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
        
        
        Application Avtalegiro = createSettings("Avtalegiro",
        "8f5f7f8f-de42-4867-82cc-63eb0cb55fa1",
        allowed2,
        " ",
        Application.Type.Payment, true);
        Avtalegiro.isPublic = true;
        Avtalegiro.isFrontend = false;
        Avtalegiro.moduleId = "WebShop";
        Avtalegiro.defaultActivate = false;
        Avtalegiro.isSingleton = true;
        apps.add(Avtalegiro);

        
        Application SedoxDatabankTheme = createSettings("SedoxDatabankTheme",
        "4f89c95c-99dc-4ed7-9352-1e1c51f4630c",
        allowed2,
        " ",
        Application.Type.Theme, true);
        SedoxDatabankTheme.isPublic = false;
        SedoxDatabankTheme.isFrontend = true;
        SedoxDatabankTheme.moduleId = "other";
        SedoxDatabankTheme.defaultActivate = false;
        SedoxDatabankTheme.allowedStoreIds = new ArrayList();
        SedoxDatabankTheme.allowedStoreIds.add("eafea78d-1eea-403f-abbb-3b23a6e61dae");
        apps.add(SedoxDatabankTheme);

        Application SemLagerhotellTheme = createSettings("SemLagerhotellTheme",
        "d91e4e20-f4af-425c-8468-2202ccc0db1e",
        allowed2,
        " ",
        Application.Type.Theme, true);
        SemLagerhotellTheme.isPublic = false;
        SemLagerhotellTheme.isFrontend = true;
        SemLagerhotellTheme.moduleId = "other";
        SemLagerhotellTheme.defaultActivate = false;
        SemLagerhotellTheme.allowedStoreIds = new ArrayList();
        SemLagerhotellTheme.allowedStoreIds.add("c444ff66-8df2-4cbb-8bbe-dc1587ea00b7");
        apps.add(SemLagerhotellTheme);

        Application PmsBooking = createSettings("PmsBooking",
        "8dcbf529-72ae-47dd-bd6b-bd2d0c54b30a",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsBooking.isPublic = true;
        PmsBooking.isFrontend = true;
        PmsBooking.moduleId = "booking";
        PmsBooking.defaultActivate = false;
        apps.add(PmsBooking);
        
        Application PmsBookingCalendar = createSettings("PmsBookingCalendar",
        "d925273e-b9fc-480f-96fa-8fb8df6edbbe",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsBookingCalendar.isPublic = true;
        PmsBookingCalendar.isFrontend = true;
        PmsBookingCalendar.moduleId = "booking";
        PmsBookingCalendar.defaultActivate = false;
        apps.add(PmsBookingCalendar);

        Application PmsBookingProductList = createSettings("PmsBookingProductList",
        "ed7efba0-de37-4cd8-915b-cc7be10b8b8b",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsBookingProductList.isPublic = true;
        PmsBookingProductList.isFrontend = true;
        PmsBookingProductList.moduleId = "booking";
        PmsBookingProductList.defaultActivate = false;
        apps.add(PmsBookingProductList);
        
        Application AbbBetalingsTheme = createSettings("AbbBetalingsTheme",
        "69f5da4a-ffca-422a-b134-72d0345997c9",
        allowed2,
        " ",
        Application.Type.Theme, true);
        AbbBetalingsTheme.isPublic = true;
        AbbBetalingsTheme.isFrontend = true;
        AbbBetalingsTheme.moduleId = "other";
        AbbBetalingsTheme.defaultActivate = false;
        apps.add(AbbBetalingsTheme);

        
        Application PmsManagement = createSettings("PmsManagement",
        "7e828cd0-8b44-4125-ae4f-f61983b01e0a",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsManagement.isPublic = true;
        PmsManagement.isFrontend = true;
        PmsManagement.moduleId = "booking";
        PmsManagement.defaultActivate = false;
        apps.add(PmsManagement);

        Application BookingEngineManagement = createSettings("BookingEngineManagement",
        "3b18f464-5494-4f4a-9a49-662819803c4a",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        BookingEngineManagement.isPublic = true;
        BookingEngineManagement.isFrontend = true;
        BookingEngineManagement.moduleId = "booking";
        BookingEngineManagement.defaultActivate = false;
        apps.add(BookingEngineManagement);
        
        Application PmsBookingSummary = createSettings("PmsBookingSummary",
        "46b52a59-de5d-4878-aef6-13b71af2fc75",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsBookingSummary.isPublic = true;
        PmsBookingSummary.isFrontend = true;
        PmsBookingSummary.moduleId = "booking";
        PmsBookingSummary.defaultActivate = false;
        apps.add(PmsBookingSummary);

        Application PmsBookingContactData = createSettings("PmsBookingContactData",
        "d3951fc4-6929-4230-a275-f2a7314f97c1",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsBookingContactData.isPublic = true;
        PmsBookingContactData.isFrontend = true;
        PmsBookingContactData.moduleId = "booking";
        PmsBookingContactData.defaultActivate = false;
        apps.add(PmsBookingContactData);
        
        Application PmsPricing = createSettings("PmsPricing",
        "1be25b17-c17e-4308-be55-ae2988fecc7c",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsPricing.isPublic = true;
        PmsPricing.isFrontend = true;
        PmsPricing.moduleId = "booking";
        PmsPricing.defaultActivate = false;
        apps.add(PmsPricing);

        Application PmsNotifications = createSettings("PmsConfiguration",
        "9de81608-5cec-462d-898c-1266d1749320",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsNotifications.isPublic = true;
        PmsNotifications.isFrontend = true;
        PmsNotifications.moduleId = "booking";
        PmsNotifications.defaultActivate = false;
        apps.add(PmsNotifications);
        
        Application bjaroytheme = createSettings("bjaroytheme",
        "2dc6c222-527d-4297-aa8c-dc9830dc3404",
        allowed2,
        " ",
        Application.Type.Theme, true);
        bjaroytheme.isPublic = true;
        bjaroytheme.isFrontend = true;
        bjaroytheme.moduleId = "other";
        bjaroytheme.defaultActivate = false;
        apps.add(bjaroytheme);

        Application PmsCalendar = createSettings("PmsCalendar",
        "2059b00f-8bcb-466d-89df-3de79acdf3a1",
        allowed2,
        " ",
        Application.Type.Webshop, true);
        PmsCalendar.isPublic = true;
        PmsCalendar.isFrontend = true;
        PmsCalendar.moduleId = "booking";
        PmsCalendar.defaultActivate = false;
        apps.add(PmsCalendar);

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
