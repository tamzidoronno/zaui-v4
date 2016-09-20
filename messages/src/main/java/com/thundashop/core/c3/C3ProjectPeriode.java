/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class C3ProjectPeriode extends DataCommon {
    public Date from;
    public Date to;
    public boolean isLocked = false;
    public boolean isActive = false;

    boolean isActive() {
        return isActive;
    }

    boolean isDateWithin(Date dateToCheck) {
        if (from.equals(dateToCheck))
            return true;
        
        if (to.equals(dateToCheck)) 
            return true;
        
        if (from.before(dateToCheck) && to.after(dateToCheck))
            return true;
        
        return false;
    }
}
