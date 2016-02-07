/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class Day implements Serializable {
    public Date startDate;
    public Date endDate;
    
    public boolean isValid() {
        return endDate.after(startDate);
    }

    boolean isInFuture() {
        Date date = new Date();
        return date.before(startDate);
    }
}
