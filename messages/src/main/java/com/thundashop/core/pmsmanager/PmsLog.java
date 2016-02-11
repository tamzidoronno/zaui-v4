package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.io.Serializable;
import java.util.Date;

public class PmsLog extends DataCommon {
    public Date dateEntry = new Date();
    public String logText = "";
    public String userId = "";
    public String bookingId = "";
    public String bookingItemType = "";
    public String bookingItemId = "";
    public String roomId = "";
}
