/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager.data;

import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class Address extends DataCommon {
    
    public static class Type {
        public static String SHIPMENT = "shipment";
        public static String MAIN = "main";
    }
    
    public String phone;
    public String emailAddress;
    public String fullName;
    public String postCode;
    public String address;
    public String address2;
    public String city;
    public String type;
    public String countrycode;
    public String countryname;
    public String customerNumber;
    public String vatNumber;
}
