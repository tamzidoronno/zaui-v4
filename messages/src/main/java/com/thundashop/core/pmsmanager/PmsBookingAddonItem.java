package com.thundashop.core.pmsmanager;

import java.io.Serializable;

public class PmsBookingAddonItem  implements Serializable {
    static class AddonTypes {
        public static Integer BREAKFAST = 1;
        public static Integer PARKING = 2;
        public static Integer LATECHECKOUT = 3;
        public static Integer EARLYCHECKIN = 4;
        public static Integer EXTRABED = 5;
    }
    
    public double price;
    public double taxes;
    public String productId;
    public Integer addonType;
    public String addonName;
    public boolean isActive = false;
    public boolean isSingle = false;
}