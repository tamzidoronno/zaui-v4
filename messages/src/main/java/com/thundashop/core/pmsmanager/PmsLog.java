package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

public class PmsLog extends DataCommon {
    public Date dateEntry = new Date();
    public String logText = "";
    public String userId = "";
    public String bookingId = "";
    public String bookingItemType = "";
    public String bookingItemId = "";
    public String roomId = "";
    public boolean includeAll = false;
    
    @Transient
    public String userName = "";
    @Transient
    public String roomName = "";
}
