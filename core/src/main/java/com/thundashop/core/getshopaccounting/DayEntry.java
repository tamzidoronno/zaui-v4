/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class DayEntry implements Serializable, Cloneable {
    /**
     * This is set to true if the
     * entry is an entry that represent a 
     * actual income.
     */
    public boolean isIncome = false;
    public String cartItemId;
    public String orderId;
    public long incrementalOrderId;
    public BigDecimal amount;
    public Date date;
    public String accountingNumber;
    
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
    
    /**
     * If this entry is a income entry 
     * it has the total accrued for the order
     */
    public BigDecimal accruedAmount;
    
    /**
     * This is the different between the total amount, prepaid and accruedAmount
     */
    public BigDecimal sameDayPayment;

    @Override
    protected DayEntry clone() throws CloneNotSupportedException {
        return (DayEntry) super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
