/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.thundashop.core.getshopaccounting.DayEntry;
import com.thundashop.core.getshopaccounting.DayIncome;
import com.thundashop.core.getshopaccounting.DiffReport;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ktonder
 */
public class DiffReportCreator {
    
    public List<DiffReport> createReport(List<DayIncome> lockedReport, List<DayIncome> currentReport, boolean incTaxes) {
        
        for (DayIncome dayIncome : lockedReport) {
            List<DayEntry> foundEntriesToRemoveLockedReport = new ArrayList();
            List<DayEntry> foundEntriesToRemoveCurrentReport = new ArrayList();
            
            for (DayEntry o : dayIncome.dayEntries) {
      
                DayEntry equalEntry = isFoundAndEqual(currentReport, o);
                if (equalEntry != null) {
                    if (o.orderId != null && o.orderId.equals("82c0ba76-8e97-479e-8a50-078436e6adfd")) {
                        System.out.println("Found 1");
                    }
                    foundEntriesToRemoveLockedReport.add(o);
                    foundEntriesToRemoveCurrentReport.add(equalEntry);    
                } else {
                    if (o.orderId != null && o.orderId.equals("82c0ba76-8e97-479e-8a50-078436e6adfd")) {
                        System.out.println("Not found 1");
                    }
                }
            }
            
            dayIncome.dayEntries.removeIf(o -> isFoundAndEqual2(foundEntriesToRemoveLockedReport, o));
            
            for (DayIncome dayIncomeCurrent : currentReport) {
                dayIncomeCurrent.dayEntries.removeIf(o -> isFoundAndEqual2(foundEntriesToRemoveCurrentReport, o));
            }
        }
       
        if (incTaxes) {
            for (DayIncome dayIncomeCurrent : currentReport) {
                dayIncomeCurrent.dayEntries.removeIf(o -> o.isTaxTransaction);
            }
            for (DayIncome dayIncomeCurrent : lockedReport) {
                dayIncomeCurrent.dayEntries.removeIf(o -> o.isTaxTransaction);
            }
        }
        
        List<String> ordersWithDiffInCurrentReport = getOrdersInReport(currentReport);
        List<String> ordersWithDiffInLockedReport = getOrdersInReport(lockedReport);
        
        List<String> allOrders = new ArrayList(ordersWithDiffInCurrentReport);
        allOrders.addAll(ordersWithDiffInLockedReport);
        allOrders = allOrders.stream()
                .distinct()
                .collect(Collectors.toList());
        
        
        ArrayList retList = new ArrayList();
        
        allOrders.stream().forEach(orderId -> {
            DiffReport rep = new DiffReport();
            rep.orderId = orderId;
            rep.currentReportDayIncomes = getDayIncomesForOrder(currentReport, orderId);
            rep.lockedReportDayIncomes = getDayIncomesForOrder(lockedReport, orderId);
            if (orderId.equals("82c0ba76-8e97-479e-8a50-078436e6adfd")) {
                System.out.println("Found: ");
            }
            retList.add(rep);
        });
        
        return retList;
    }
    
    public Map<String, List<DayEntry>> getDayIncomesForOrder(List<DayIncome> report, String orderId) {
        SimpleDateFormat simpleDateFormatter = new SimpleDateFormat("dd.MM.yyyy");
        return report.stream()
                .flatMap(o -> o.dayEntries.stream())
                .filter( o -> o.orderId != null)
                .filter(o -> o.orderId.equals(orderId))
                .collect(Collectors.groupingBy(o -> {
                    return simpleDateFormatter.format(o.date);
                }));
    }

    private List<String> getOrdersInReport(List<DayIncome> currentReport) {
        List<String> ordersInCurrentReport = currentReport.stream()
                .flatMap(o -> o.dayEntries.stream())
                .filter( o -> o.orderId != null)
                .map(o -> o.orderId)
                .distinct()
                .collect(Collectors.toList());
        return ordersInCurrentReport;
    }

    private DayEntry isFoundAndEqual(List<DayIncome> currentReport, DayEntry o) {
        for (DayIncome income : currentReport) {
            for (DayEntry entry : income.dayEntries) {
                if (isEqual(entry, o)) {
                    return entry;
                }
            }
        }
        
        return null;
    }

    private boolean isNullEqual(Object prePaidAmount, Object prePaidAmount0) {
        return prePaidAmount == null && prePaidAmount0 == null;
    }

    private boolean oneNullAnotherNot(Object a, Object b) {
        if (a == null && b != null) {
            return true;
        }
        
        if (b == null && a != null) {
            return true;
        }
        
        return false;
    }

    private boolean isEqual(DayEntry entry, DayEntry o) {
        
        if (entry.date == null) {
            return false;
        }

        if (entry.accountingNumber.equals(o.accountingNumber) 
                && entry.date.equals(o.date) 
                && entry.isAccrued == o.isAccrued
                && !oneNullAnotherNot(entry.accruedAmount, o.accruedAmount) && (isNullEqual(entry.accruedAmount, o.accruedAmount) || entry.accruedAmount.equals(o.accruedAmount))
                && !oneNullAnotherNot(entry.accruedAmountExTaxes, o.accruedAmountExTaxes) && (isNullEqual(entry.accruedAmountExTaxes, o.accruedAmountExTaxes) || entry.accruedAmountExTaxes.equals(o.accruedAmountExTaxes))
                && entry.amount.equals(o.amount)
                && entry.amountExTax.equals(o.amountExTax)
                && !oneNullAnotherNot(entry.cartItemId, o.cartItemId) && (isNullEqual(entry.cartItemId, o.cartItemId) || entry.cartItemId.equals(o.cartItemId))
                && !oneNullAnotherNot(entry.freePostId, o.freePostId) && (isNullEqual(entry.freePostId, o.freePostId) || entry.freePostId.equals(o.freePostId))
                && entry.incrementalOrderId ==  o.incrementalOrderId
                && entry.isActualIncome == entry.isActualIncome
                && entry.isIncome == entry.isIncome
                && entry.isOffsetRecord == entry.isOffsetRecord
                && entry.isPrePayment == entry.isPrePayment
                && entry.isTaxTransaction == entry.isTaxTransaction
                && entry.orderId.equals(entry.orderId)
                && !oneNullAnotherNot(entry.prePaidAmount, o.prePaidAmount) && (isNullEqual(entry.prePaidAmount, o.prePaidAmount) || entry.prePaidAmount.equals(entry.prePaidAmount))
                && !oneNullAnotherNot(entry.prePaidAmountExTaxes, o.prePaidAmountExTaxes) && (isNullEqual(entry.prePaidAmountExTaxes, o.prePaidAmountExTaxes) || entry.prePaidAmountExTaxes.equals(entry.prePaidAmountExTaxes))
                && !oneNullAnotherNot(entry.sameDayPayment, o.sameDayPayment) && (isNullEqual(entry.sameDayPayment, o.sameDayPayment) || entry.sameDayPayment.equals(entry.sameDayPayment))
                && !oneNullAnotherNot(entry.sameDayPaymentExTaxes, o.sameDayPaymentExTaxes) && (isNullEqual(entry.sameDayPaymentExTaxes, o.sameDayPaymentExTaxes) || entry.sameDayPaymentExTaxes.equals(entry.sameDayPaymentExTaxes))) {   
            return true;
        }
        
        return false;
    }

    private boolean isFoundAndEqual2(List<DayEntry> foundEntriesToRemoveLockedReport, DayEntry o) {
        for (DayEntry entry : foundEntriesToRemoveLockedReport) {
            if (isEqual(entry, o)) {
                return true;
            }
        }
        
        return false;
    }
}
