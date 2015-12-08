/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

/**
 *
 * @author boggi
 */
public class PmsBookingAddonItem {
    static class AddonTypes {
        public static Integer BREAKFAST = 1;
        public static Integer PARKING = 2;
        public static Integer LATECHECKOUT = 3;
        public static Integer EARLYCHECKIN = 4;
    }
    
    public double price;
    public double taxes;
    public Integer addonType;
    public String addonName;
}