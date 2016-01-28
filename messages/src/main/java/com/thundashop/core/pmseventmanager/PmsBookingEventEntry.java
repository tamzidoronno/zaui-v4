package com.thundashop.core.pmseventmanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import java.util.ArrayList;
import java.util.List;

public class PmsBookingEventEntry extends DataCommon {
    public String title = "";
    public String shortdesc = "";
    public String imageId = "";
    public String category = "";
    
    public String starttime = "";
    public String description = "";
    public List<PmsBookingEventLink> lenker = new ArrayList();
    public List<PmsBookingDateRange> dateRanges = new ArrayList();
    public List<String> roomNames = new ArrayList();
}
