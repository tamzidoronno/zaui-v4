/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author boggi
 */
public class PmsBlockedUser implements Serializable {
    public String email = "";
    public String phone = "";
    public String addedByUser = "";
    public String id = UUID.randomUUID().toString();
    public Date addedWhen;
    
    public String getPhoneNumber() {
        return phone.replaceAll("\\D+","");
    }
    
    
    public String getEmail() {
        if(email == null) {
            return "";
        }
        return email.replaceAll("\\s+","");
    }
}
