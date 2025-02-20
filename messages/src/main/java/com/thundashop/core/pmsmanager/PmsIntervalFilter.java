package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsIntervalFilter implements Serializable {
    Date start;
    Date end;
    Integer interval;
    boolean compactMode = false;
    String selectedDefinedFilter = "";

    public PmsIntervalFilter(Date start, Date end, Integer interval) {
        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    public List<String> types = new ArrayList();
    
    /**
     * If there are added any ids to this array the 
     * result will only contain rooms with the corrosponding pmsbooking
     */
    public List<String> pmsBookingIds = new ArrayList();
}
