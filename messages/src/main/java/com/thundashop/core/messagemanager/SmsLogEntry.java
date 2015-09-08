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
    boolean tryPoll = true;

    boolean isOutDated() {
        if(rowCreatedDate != null) {
            long then = rowCreatedDate.getTime() / 1000;
            long now = new Date().getTime() / 1000;
            long diff = now - then;
            if(diff > 3600) {
                return true;
            }
        }
        return false;
    }

    boolean delivered() {
        return errorCode.equals("delivered");
    }

    boolean undelivered() {
        return errorCode.equals("undelivered");
    }
}
