package com.thundashop.core.appmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApplicationSettings extends DataCommon implements Comparator<ApplicationSettings> {
    public static class Type {
        public static String Marketing = "MarketingApplication";
        public static String System = "SystemApplication";
        public static String Webshop = "WebshopApplication";
        public static String Payment = "PaymentApplication";
        public static String Reporting = "ReportingApplication";
        public static String Shipment = "ShipmentApplication";
        public static String Theme = "ThemeApplication";
    }
    
    public String appName;
    public String description;
    public List<String> allowedAreas;
    public boolean isSingleton;
    public boolean renderStandalone;
    public boolean isPublic;
    public Double price;
    public String userId;
    public String type;
    public String ownerStoreId;
    public String clonedFrom;
    public int trialPeriode = 7;
    
    @Override
    public int compare(ApplicationSettings o1, ApplicationSettings o2) {
        return o1.appName.compareTo(o2.appName);
    }

    public void complete() {
        if (type.equals(Type.Theme)) {
            allowedAreas = new ArrayList();
            allowedAreas.add("themes");
        }
    }
}
