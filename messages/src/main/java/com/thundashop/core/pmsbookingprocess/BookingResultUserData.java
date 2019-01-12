/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author boggi
 */
public class BookingResultUserData {
    public String name;
    public String email;
    public String address = "";
    public String postcode = "";
    public String city = "";
    public String phone;
    public String phonePrefix;

    void setUser(User usr) {
        name = usr.fullName;
        email = usr.emailAddress;
        phone = usr.cellPhone;
        phonePrefix = usr.prefix;
        if(usr.address != null) {
            address = usr.address.address;
            postcode = usr.address.postCode;
            city = usr.address.city;
        }
    }
    
}
