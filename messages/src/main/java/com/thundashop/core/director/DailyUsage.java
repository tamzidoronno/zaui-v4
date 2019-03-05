/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.director;

import com.thundashop.core.common.DataCommon;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 *
 * @author ktonder
 */
public class DailyUsage extends DataCommon {
    public int domesticSmses = 0;
    public int internationalSmses = 0;
    public int ehfs = 0;
    public String belongsToStoreId = "";
    public String systemId;
    public Date start;
    public Date end;
    
    private boolean hasBeenInvoiced = false;

    public boolean isOnDay(Date timeToGet) {
        long startL = start.getTime();
        long endL = end.getTime();
        long check = timeToGet.getTime();
        
        return startL <= check && check < endL;
    }

    public boolean isForMonth(int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        
        int thisYear = cal.get(Calendar.YEAR);
        int thisMonth = cal.get(Calendar.MONTH);
        
        return month == thisMonth && year == thisYear;
    }
    
    public String getMonthAndYear() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        return cal.get(Calendar.YEAR)+" / "+String.format("%02d", (cal.get(Calendar.MONTH)+1));
    }
    
    public boolean hasBeenInvoiced() {
        try {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = dateFormatter.parse("2019-02-01 00:00:00");
            
            if (start.before(date)) {
                return true;
            }
        } catch (ParseException ex) {
            Logger.getLogger(DailyUsage.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return hasBeenInvoiced;
    }
    
    public void markAsInvoiced() {
        this.hasBeenInvoiced = true;
    }
}
