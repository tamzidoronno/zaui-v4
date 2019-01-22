
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

public class PmsBookingDateRange implements Serializable {
    public Date start;
    public Date end;
    public Date cleaningDate;
    public Date exitCleaningDate;
    public boolean isDeleted = false;
    
    @Transient
    public long endTimeStamp;
    @Transient
    public long startTimeStamp;
    
    public boolean within(Date startDate, Date endDate) {
        if (startDate == null || endDate == null || this.start == null || this.end == null) {
            return false;
        }
        
        long StartDate1 = startDate.getTime();
        long StartDate2 = this.start.getTime();
        long EndDate1 = endDate.getTime(); 
        long EndDate2 = this.end.getTime();
        return (StartDate1 < EndDate2) && (StartDate2 < EndDate1);
    }

}
