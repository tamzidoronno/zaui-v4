package com.thundashop.core.pmseventmanager;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PmsBookingEventEntry extends DataCommon {
    public String title = "";
    public String shortdesc = "";
    public String imageId = "";
    public String logoId = "";
    public String category = "";
    public String arrangedBy = "";
    public String location = "";
    public String contact = "";
    
    public String starttime = "";
    public String description = "";
    public List<PmsBookingEventLink> lenker = new ArrayList();
    public List<PmsBookingDateRange> dateRanges = new ArrayList();
    public List<String> roomNames = new ArrayList();
    public HashMap<String, PmsBookingEventEntry> overrideEntries = new HashMap();
    public String bookingId = "";

    void saveDay(PmsBookingEventEntry entry, String day) {
        if(day == null || day.isEmpty()) {
            return;
        }
        entry = entry.copy();
        entry.roomNames = new ArrayList();
        entry.dateRanges = new ArrayList();
        
        entry.id = "";
        overrideEntries.put(day, entry);
    }

    public PmsBookingEventEntry getDay(String day) {
        if(day == null || day.isEmpty()) {
            return this;
        }
        PmsBookingEventEntry data = overrideEntries.get(day);
        if(data == null) {
            return this;
        }
        data.dateRanges = this.dateRanges;
        data.roomNames = this.roomNames;
        
        return data.copy();
    }

    private PmsBookingEventEntry copy() {
        try {
            DataCommon res = this.clone();
            return (PmsBookingEventEntry) res;
        }catch(CloneNotSupportedException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    void finalizeSubEntries() {
        for(PmsBookingEventEntry entry : overrideEntries.values()) {
            entry.dateRanges = dateRanges;
            entry.roomNames = roomNames;
        }
    }

}
