package com.thundashop.core.ordermanager.data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;
import org.mongodb.morphia.annotations.Transient;

public class OrderTransaction implements Serializable {
    public Date date;
    public Double amount;
    public Double amountInLocalCurrency;
    
    /**
     * Is the difference in calculated currency and what has actually 
     * been paid.
     * 
     * Used for currency losses / gains ( negative value indicate a loss)
     */
    public Double agio;
    public String userId;
    public String transactionId = UUID.randomUUID().toString();
    public Integer transactionType = 1;
    public String refId = "";
    public boolean transferredToAccounting = false;
    public Date rowCreatedDate = new Date();
    public String comment = "";
    
    @Transient
    public String orderId = "";
    
    public String accountingDetailId = "";
    
    public boolean isReferenceId(String refId) {
        return refId.equals(refId);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OrderTransaction) {
            OrderTransaction obj2 = (OrderTransaction)obj;
            return obj2.transactionId.equals(transactionId);
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.transactionId);
        return hash;
    }
    
}
