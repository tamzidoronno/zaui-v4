/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class DayEntry implements Serializable, Cloneable {
    /**
     * This is set to true if the
     * entry is an entry that represent a 
     * actual income.
     * 
     * This will be the total amount of money paid on the order and the 
     * day its paid.
     */
    public boolean isIncome = false;
    public String cartItemId;
    public String productId;
    public Integer count;
    public String orderId;
    public long incrementalOrderId;
    public BigDecimal amount;
    public BigDecimal amountExTax;
    public Date date;
    public String accountingNumber;
    
    /**
     * Reference to the freepost that is used for this. If null its not created
     * if a freepost.
     */
    public String freePostId = null;
    
    public boolean isOffsetRecord = false;
    
    /**
     * Is set to true if this entry has 
     * and order rowcreated date is yesterday or earlier
     */
    public boolean isPrePayment = false;
    
    /**
     * Is set to true if this entry has 
     * and order rowcreated date is next accounting day or later
     */
    public boolean isAccrued = false;
    
    /**
     * If this entry is a income entry 
     * it has the total prePaidAmount for the order
     */
    public BigDecimal prePaidAmount;
    public BigDecimal prePaidAmountExTaxes;
    
    /**
     * If this entry is a income entry 
     * it has the total accrued for the order
     */
    public BigDecimal accruedAmount;
    public BigDecimal accruedAmountExTaxes;
    
    /**
     * This is the different between the total amount, prepaid and accruedAmount
     */
    public BigDecimal sameDayPayment;
    public BigDecimal sameDayPaymentExTaxes;
    
    /**
     * Will be true if this is an actual income post.
     */
    public boolean isActualIncome = false;
    public boolean isTaxTransaction = false;
    
    /**
     * Unique transactionid for each registered payment transaction
     */
    public String orderTransactionId = "";
    
    /**
     * If this record is connected to multiple 
     * The transactionids are listed here.
     */
    public List<String> orderTransactionIds = new ArrayList<>();
    
    /**
     * If this is set to other then blank or zero all payments
     * should be grouped by this id.
     * 
     * Normally used if there is one payment for multiple orders ( etc OCR / KID invoices )
     * 
     * Accounting integrations should take this value into consideration 
     */
    public String batchId = "";
    
    
    @Transient
    public HashMap<String, String> metaData = new HashMap<>();
    

    @Override
    protected DayEntry clone() throws CloneNotSupportedException {
        return (DayEntry) super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
    
    public String getAccountingNumber() {
        return accountingNumber;
    }
    
    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmountExTaxes() {
        if(amountExTax != null) {
            return amountExTax;
        }
        if(amount != null) {
            return amount;
        }
        return new BigDecimal(0.0);
    }
}
