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
public class SedoxCreditAccount implements Serializable, Cloneable {
    public List<SedoxCreditHistory> history = new ArrayList();
    public boolean allowNegativeCredit = false;
    private int balance;

    void addOrderToCreditHistory(SedoxOrder order, SedoxSharedProduct sedoxProduct, int transactionSedoxId) {
        SedoxCreditHistory historyEntry = new SedoxCreditHistory();
        historyEntry.amount = -1 * order.creditAmount;
        historyEntry.description = sedoxProduct.getName();
        historyEntry.transactionReference = transactionSedoxId;
        balance = (int) (balance + historyEntry.amount);
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
        balance = (int) (balance + historyEntry.amount);
        historyEntry.newBalance = balance;
        history.add(historyEntry);
    }

    void setBalance(double d) {
        this.balance = (int) d;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
