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
/**
 *
 * @author ktonder
 */
public class C3Hour extends DataCommon {
    public Date from;
    public Date to;
    public double hours;
    public String registeredByUserId;
    public String workPackageId;
    public String projectId;

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

}
