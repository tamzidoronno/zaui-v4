package com.thundashop.core.ordermanager.data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

class OrderTransaction implements Serializable {
    public Date date;
    public Double amount;
    public String userId;
    public String transactionId = UUID.randomUUID().toString();
    public Integer transactionType = 1;
    public String refId = "";
}
