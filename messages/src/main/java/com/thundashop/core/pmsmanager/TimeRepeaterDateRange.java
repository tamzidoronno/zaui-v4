package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class TimeRepeaterDateRange implements Serializable {
    public Date start;
    public Date end;

    boolean isBetweenTime(Date dateToCheck) {
        if(start.equals(dateToCheck)) {
            return true;
        }
        if(dateToCheck.after(start) && dateToCheck.before(end)) {
            return true;
        }
        return false;
    }

    boolean containsRange(Date start, Date end) {
        if((this.start.before(start) || this.start.equals(start)) && (this.end.after(end) || this.end.equals(end))) {
            return true;
        }
        return false;
    }
    
}