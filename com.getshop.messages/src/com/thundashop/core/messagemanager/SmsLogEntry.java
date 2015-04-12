package com.thundashop.core.messagemanager;

import com.thundashop.core.common.DataCommon;
import java.util.Date;

public class SmsLogEntry extends DataCommon {
    public String to = "";
    public String message = "";
    public String responseCode = "";
    public String errorCode = "";
    public String apiId = "";
    public String prefix = "";
    public Date date = new Date();
    public Date delivered = null;
    public String status = null;
    public String from;
    public String msgId;
    public String network;
}
