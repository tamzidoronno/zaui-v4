/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class UserQueueMessage {
    public Serializable dataCommon;
    private final Date expiryDate;

    public UserQueueMessage() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR, 24);
        
        this.expiryDate = cal.getTime();
    }
    
    /**
     * Messages not delivered within 24 hours are to 
     * be expired
     * 
     * @return 
     */
    public boolean hasExpired() {
        Date now = new Date();
        return now.after(expiryDate);
    }
    
}
