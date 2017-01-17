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
    
    boolean isStartDateWithin(Date from, Date to) {
        if (from.equals(start))
            return true;
        
        if (to.equals(start)) 
            return true;
        
        if (from.before(start) && to.after(start))
            return true;
        
        return false;
    }

    boolean isEndDateWithin(Date from, Date to) {
        if (from.equals(end))
            return true;
        
        if (to.equals(end)) 
            return true;
        
        if (from.before(end) && to.after(end))
            return true;
        
        return false;
    }


    boolean intercepts(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || this.start == null || this.end == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.start.getTime()+1;
        long EndDate1 = endDate.getTime();
        long EndDate2 = this.end.getTime()-1;
        return (StartDate1 <= EndDate2) && (StartDate2 <= EndDate1);
    }

}