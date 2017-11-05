
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
}
