/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class C3ForskningsUserPeriode extends DataCommon {
    public Date start;
    public Date end;
    public int percents;
    public String userId;
    
    boolean isDateWithin(Date dateToCheck) {
        if (start.equals(dateToCheck))
            return true;
        
        if (end.equals(dateToCheck)) 
            return true;
        
        if (start.before(dateToCheck) && end.after(dateToCheck))
            return true;
        
        return false;
    }

}