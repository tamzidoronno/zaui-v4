package com.thundashop.core.pmsmanager;

import java.util.Date;

public class TimeRepeaterDateRange {
    Date start;
    Date end;

    boolean isBetweenTime(Date dateToCheck) {
        if(start.equals(dateToCheck)) {
            return true;
        }
        if(dateToCheck.after(start) && dateToCheck.before(end)) {
            return true;
        }
        return false;
    }
    
}