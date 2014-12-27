package com.thundashop.core.appmanager.data;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Setting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

public class Application extends DataCommon implements Comparator<Application> {
    public static class Type {

        public static String Marketing = "MarketingApplication";
        public static String System = "SystemApplication";
        public static String Webshop = "WebshopApplication";
        public static String Payment = "PaymentApplication";
        public static String Reporting = "ReportingApplication";
        public static String Shipment = "ShipmentApplication";
        public static String Theme = "ThemeApplication";
    }
    
    /**
     * This is the settings that is common for all instances.
     */
    @Transient
    public HashMap<String, Setting> settings = new HashMap<>();

    /**
     * Widgets is allocated spaces in an app. A app is defining the widgets it
     * has, and thereby allows other applications to give data to the widgets.
     * This maps has the widgetid as key and the value as the function on this
     * application it should try to run.
     */
    public HashMap<String, String> connectedWidgets = new HashMap();

    public String appName;
    public String description;
    public List<String> allowedAreas;
    public boolean isSingleton;
    public boolean renderStandalone;
    public boolean isPublic;
    public boolean isFrontend = false;
    public boolean isResponsive = false;
    public Double price;
    public String userId;
    public String type;
    public String ownerStoreId;
    public String clonedFrom;
    public int trialPeriode = 7;
    public boolean pageSingelton;
    public boolean hasDashBoard = true;
    public boolean defaultActivate = false;
    public List<String> allowedStoreIds = new ArrayList();
    public List<ApiCallsInUse> apiCallsInUse = new ArrayList();
    
    @Transient
    public ApplicationModule applicationModule;

    public boolean activeAppOnModuleActivation = false;
    public String moduleId = "other";
    
    
    @Override
    public int compare(Application o1, Application o2) {
        return o1.appName.compareTo(o2.appName);
    }

    public void complete() {
        if (type != null && type.equals(Type.Theme)) {
            allowedAreas = new ArrayList();
            allowedAreas.add("themes");
        }
    }
    
    public Application jsonClone() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return gson.fromJson(json, Application.class);
    }

}
