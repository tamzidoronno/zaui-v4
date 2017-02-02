/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class ClosedOrderPeriode extends DataCommon {
    public Date startDate;
    public Date endDate;
    public String paymentTypeId;
    
    public boolean interCepts(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || this.startDate == null || this.endDate == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.startDate.getTime();
        long EndDate1 = endDate.getTime();
        long EndDate2 = this.endDate.getTime();
        return (StartDate1 < EndDate2) && (StartDate2 < EndDate1);
    }

    public boolean within(Date date) {
        if (date == null) {
            return false;
        }
        
        long StartDate1 = this.startDate.getTime();
        long EndDate1 = this.endDate.getTime();
        long inDate = date.getTime();
        
        return (StartDate1 <= inDate && inDate >= EndDate1);
    }
}
