/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.vippsmanager;

/**
 *
 * @author boggi
 */
public class StartTransactionResponse {
    class TransactionInfo {
        Integer amount;
        String status;
        String transactionId;
        String timeStamp;
        String message;
    }
    public String orderId;
    public String merchantSerialNumber;
    public TransactionInfo transactionInfo;
}
