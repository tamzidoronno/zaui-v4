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
public class ProjectCost extends DataCommon {
    public String registeredByUserId;
    public String projectId;

    public Date from;
    public Date to;
    
    public boolean within(Date startDate, Date endDate) { 

        if (startDate == null || endDate == null || this.from == null || this.to == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.from.getTime()+1;
        long EndDate1 = endDate.getTime();
        long EndDate2 = this.to.getTime()-1;
        
        return (StartDate1 <= EndDate2) && (StartDate2 <= EndDate1);
    }
    
    
    public boolean completlyWithin(Date startDate, Date endDate) {
        Date start = this.from;
        Date end = this.to;
        
        if (start == null || end == null || startDate == null || endDate == null) {
            return false;
        }
        
        if (startDate.equals(start) && endDate.equals(end))
            return true;
        
        if (start.after(startDate) && end.before(endDate))
            return true;
        
        if (start.equals(startDate) && end.before(endDate))
            return true;

        if (start.after(startDate) && end.equals(endDate))
            return true;

        return false;
    }
    
}
