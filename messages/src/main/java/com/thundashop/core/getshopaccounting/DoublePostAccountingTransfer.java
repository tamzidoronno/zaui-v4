/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class DoublePostAccountingTransfer extends DataCommon {
    public Date start;
    public Date end;
    public String userId;

    public List<DayIncome> incomes = new ArrayList();
    
    public List<Date> transferredToAccountingDates = new ArrayList();

    public boolean isWithinOrEqual(Date start, Date end) {
        Date startDate = this.start;
        Date endDate = this.end;
        
        if (start == null || end == null || startDate == null || endDate == null) {
            return false;
        }
        
        if (start.equals(startDate) && end.equals(endDate))
            return true;
        
        if (start.equals(startDate) && endDate.before(end))
            return true;
        
        if (startDate.after(start) && end.equals(endDate))
            return true;
        
        if (startDate.after(start) && endDate.before(end))
            return true;
     
        return false;
    }

    void addTransferredDate(Date date) {
        transferredToAccountingDates.add(date);
    }
}
