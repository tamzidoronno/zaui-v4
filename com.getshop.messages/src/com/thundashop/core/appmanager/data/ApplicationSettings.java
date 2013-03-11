package com.thundashop.core.appmanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.List;

public class ApplicationSettings extends DataCommon {
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

}
