package com.thundashop.core.pmsmanager;

import java.io.Serializable;

public class PmsChannelConfig implements Serializable {
    public String channel;
    public String humanReadableText;
    public String preferredPaymentType;
    public boolean ignoreUnpaidForAccess = false;
    public boolean displayOnBookingProcess = false;
}
