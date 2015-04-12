package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

public class SmsLogEntry extends DataCommon {
    public String to = "";
    public String message = "";
    public String clicatellSenderResponse = "";
    public String apiId = "";
    public String prefix = "";
    public Date date = new Date();
    public Long delivered;
    public Integer status;
}
