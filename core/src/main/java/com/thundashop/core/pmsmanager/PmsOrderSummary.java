package com.thundashop.core.pmsmanager;

import java.io.Serializable;

public class PmsOrderSummary implements Serializable {
    public Long incrementOrderId;
    public String id;
    public Double amount;
    boolean paid = false;
}
