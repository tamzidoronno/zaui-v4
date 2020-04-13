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
    
    /**
     * type 1 = Manuall
     * type 2 = OCR
     */
    public Integer transactionType = 1; 
    
    public String refId = "";
    public boolean transferredToAccounting = false;
    public String addedToZreport = null;
    
    public Date rowCreatedDate = new Date();
    public String comment = "";
    
    @Transient
    public String orderId = "";
    
    public String accountingDetailId = "";
    
    /**
     * All with the same batchId should be grouped together as they are a concatignated payment.
     */
    public String batchId = "";
    
    public boolean isReferenceId(String refId) {
        if(refId.isEmpty()) {
            return false;
        }
        return refId.equals(this.refId);
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
    
    
    public boolean canBeDeleted() {
        return transactionType != null 
                && !transactionType.equals(Order.OrderTransactionType.OCR) 
                && !transferredToAccounting;
    }
}
