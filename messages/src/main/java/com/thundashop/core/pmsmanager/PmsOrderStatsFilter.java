package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.Date;

public class PmsOrderStatsFilter implements Serializable {
    public Date start;
    public Date end;
    public String paymentMethod;
    public String displayType;
    public String priceType;
    public Integer paymentStatus;
}
