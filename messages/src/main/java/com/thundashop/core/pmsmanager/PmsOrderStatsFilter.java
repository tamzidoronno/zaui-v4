package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsOrderStatsFilter implements Serializable {
    public Date start;
    public Date end;
    public String paymentMethod;
    public String displayType;
    public Integer paymentStatus;
}
