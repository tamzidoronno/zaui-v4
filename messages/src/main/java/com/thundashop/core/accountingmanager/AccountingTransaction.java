/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import java.math.BigDecimal;
import java.util.Date;

/**
 * We are using from (not including) start to end (and included) 
 * 
 * @author ktonder
 */
public class AccountingTransaction {
    public String description = "";
    public Date start;
    public Date end;
    public int accountNumber;
    public BigDecimal credit = new BigDecimal(0);
    public BigDecimal debit = new BigDecimal(0);;
    
    public boolean isFor(int accountNumber, Date date) {
        long startLong = start.getTime();
        long endLong = end.getTime();
        long toCheck = date.getTime();
        
        if (startLong < toCheck && toCheck <= endLong && this.accountNumber == accountNumber) {
            return true;
        }
        
        return false;
    }

    public BigDecimal getSum() {
        return debit.subtract(credit);
    }
}
