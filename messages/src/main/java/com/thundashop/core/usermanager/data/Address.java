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
        public static String INVOICE = "invoice";
        public static String MAIN = "main";
    }
    
    public String phone = "";
    public String prefix = "";
    public String emailAddress = "";
    public String fullName = "";
    public String co = "";
    public String postCode = "";
    public String address = "";
    public String address2;
    public String city = "";
    public String type;
    public String countrycode;
    public String countryname;
    public String customerNumber;
    public String vatNumber;
    public String reference = "";
    public String province;
    

    public boolean isSame(User user, Address address) {

        return checkField(address.phone, phone)
                && checkField(address.emailAddress, this.emailAddress)
                && (checkField(address.fullName, this.fullName) || checkField(user.fullName, this.fullName))
                && checkField(address.postCode, this.postCode)
                && checkField(address.address, this.address)
                && checkField(address.address2, this.address2)
                && checkField(address.co, this.co)
                && checkField(address.prefix, this.prefix)
                && checkField(address.city, this.city)
                && checkField(address.type, this.type)
                && checkField(address.countrycode, this.countrycode)
                && checkField(address.countryname, this.countryname)
                && checkField(address.customerNumber, this.customerNumber)
                && checkField(address.vatNumber, this.vatNumber)
                && checkField(address.reference, this.reference)
                && checkField(address.province, this.province);
    }
   
    private boolean checkField(String a, String b) {
        if (a == null && b != null)
            return false;
        
        if (b == null && a != null)
            return false;
        
        if (a == null && b == null)
            return true;
        
        return a.equals(b);
    }
   
}
