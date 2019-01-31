package com.thundashop.core.ordermanager.data;

import java.util.Date;

public class OrderShipmentLogEntry {
    
    public static class Type {
        public static String email = "email";
        public static String phone = "phone";
        public static String accounting = "accounting";
        public static String ehf = "ehf";
        public static String debtCollector = "debtCollector";
    }
    
    public Date date;
    public String address;
    public String type;
}
