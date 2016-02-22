
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsBookingDateRange implements Serializable {
    public Date start;
    public Date end;
    public Date cleaningDate;
    public Date exitCleaningDate;
}
