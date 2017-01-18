package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PmsOrderStatsFilter implements Serializable {
    class PaymentMethods {
        String paymentMethod = "";
        Integer paymentStatus;
    }
    public Date start;
    public Date end;
    List<PaymentMethods> methods = new ArrayList();
    public String displayType;
    public String priceType;
}
