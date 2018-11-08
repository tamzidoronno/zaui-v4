package com.thundashop.core.ordermanager.data;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

public class OrderTransaction implements Serializable {
    public Date date;
    public Double amount;
    public String userId;
    public String transactionId = UUID.randomUUID().toString();
    public Integer transactionType = 1;
    public String refId = "";
    public boolean transferredToAccounting = false;
    
    @Transient
    public String orderId = "";
}
