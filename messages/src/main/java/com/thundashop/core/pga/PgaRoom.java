/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ktonder
 */
public class PgaRoom implements Serializable {
    public Date checkin;
    public Date checkout;
    public String name;
    public String prefix;
    public String phonenumber;
    public String email;
    public String code;
    public String bookingItemName;
    public boolean lateCheckoutPurchased;
    public Date nextCleaningDate = null;
    public boolean stayHasStarted = false;
}
