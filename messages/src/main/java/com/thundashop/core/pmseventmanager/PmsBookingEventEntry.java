package com.thundashop.core.pmseventmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class PmsBookingEventEntry extends DataCommon {
    public String title = "";
    public String shortdesc = "";
    public String imageId = "";
    public String category = "";
    
    public String description = "";
    public List<PmsBookingEventLink> lenker = new ArrayList();
}
