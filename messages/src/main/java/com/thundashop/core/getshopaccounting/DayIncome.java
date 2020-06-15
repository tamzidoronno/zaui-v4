/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class DayIncome {
    public String id = UUID.randomUUID().toString();
    public Date start;
    public Date end;
    public boolean isFinal = false;
    public List<DayEntry> dayEntries = new ArrayList();
    public List<DayIncomeTransferToAaccountingInformation> accountingTransfer = new ArrayList();
    
    /**
     * Used for return error messages to 
     * gui.
     */
    @Transient
    public String errorMsg;

    public boolean within(Date date) {
        long startLong = start.getTime();
        long endLong = end.getTime();
        long dateLong = date.getTime();
        
        return startLong <= dateLong && dateLong < endLong;
    }

    public BigDecimal getTotal() {
        BigDecimal total = new BigDecimal(0);
        for(DayEntry entry : dayEntries) {
            if(entry.isActualIncome) {
                total = total.add(entry.amount);
            }
        }
        return total;
    }
    
    public Map<String, List<DayEntry>> getGroupedByAccountExTaxes() {
        return dayEntries.stream()
                .filter(o -> !o.isTaxTransaction)
                .collect(Collectors.groupingBy(DayEntry::getAccountingNumber));
    }

    public boolean transferredToAccounting() {
        return dayEntries.isEmpty() || accountingTransfer.size() > 0;
    }
}
