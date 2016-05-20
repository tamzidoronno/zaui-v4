package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class PmsBookingAddonItem  implements Serializable {


    static class AddonTypes {
        public static Integer BREAKFAST = 1;
        public static Integer PARKING = 2;
        public static Integer LATECHECKOUT = 3;
        public static Integer EARLYCHECKIN = 4;
        public static Integer EXTRABED = 5;
        public static Integer CANCELLATION = 6;
    }
    
    public String addonId = UUID.randomUUID().toString();
    public Date date;
    public double price;
    public double priceExTaxes;
    public String productId;
    public Integer addonType;
    public Integer count = 1;
    public boolean isActive = false;
    public boolean isSingle = false;
}