package com.thundashop.core.jomres;

import com.thundashop.core.common.DataCommon;

public class JomresLog extends DataCommon {
    private String message;
    private Long timeStamp;

    public JomresLog() {
    }

    public JomresLog(String message, Long timeStamp) {
        this.message = message;
        this.timeStamp = timeStamp;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }
}
