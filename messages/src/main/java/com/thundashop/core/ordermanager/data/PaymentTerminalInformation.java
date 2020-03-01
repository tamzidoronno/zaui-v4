/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

/**
 *
 * @author ktonder
 */
public class PaymentTerminalInformation {
    public String paymentType;
    public String cardInfo = "";
    public int sessionNumber;
    public String issuerName = "";
    public String transactionNumber = "";
    
    public String getCardInfo() {
        return cardInfo;
    }

    public int getSessionNumber() {
        return sessionNumber;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }   
}