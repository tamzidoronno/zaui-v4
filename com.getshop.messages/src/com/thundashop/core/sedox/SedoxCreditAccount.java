/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SedoxCreditAccount implements Serializable {
    public List<SedoxCreditHistory> history = new ArrayList();
    public boolean allowNegativeCredit = false;
    private double balance;

    void addOrderToCreditHistory(SedoxOrder order, SedoxProduct sedoxProduct, int transactionSedoxId) {
        SedoxCreditHistory historyEntry = new SedoxCreditHistory();
        historyEntry.amount = -1 * order.creditAmount;
        historyEntry.description = sedoxProduct.toString();
        historyEntry.transactionReference = transactionSedoxId;
        balance = balance + historyEntry.amount;
        historyEntry.newBalance = balance;
        history.add(historyEntry);
    }
    
    public double getBalance() {
        return balance;
    }

    void updateCredit(SedoxCreditOrder sedoxCreditOrder, String description) {
        SedoxCreditHistory historyEntry = new SedoxCreditHistory();
        historyEntry.amount = sedoxCreditOrder.amount;
        
        if (description != null) {
            historyEntry.description = description;
        } else {
            historyEntry.description = "Added credit for order: " + sedoxCreditOrder.magentoOrderId + ", amount " + historyEntry.amount + " credits";
        }
        
        historyEntry.transactionReference = sedoxCreditOrder.magentoOrderId;
        balance = balance + historyEntry.amount;
        historyEntry.newBalance = balance;
        history.add(historyEntry);
    }

    void setBalance(double d) {
        this.balance = d;
    }
}
