package com.thundashop.core.ordermanager.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class OrderResult {
   public String orderId;
   public long incOrderId;
   public String user;
   public String userId;
   public Integer status;
   public Date orderDate;
   public Date dueDate;
   public Date paymentDate;
   public Date markedPaidDate;
   public Date start;
   public Date end;
   public double amountExTaxes = 0;
   public double amountIncTaxes = 0;
   public double amountPaid = 0;
   public double restAmount = 0;
   public boolean closed = false;
   public String invoiceNote = "";
   public List<OrderShipmentLogEntry> shipmentLog = new ArrayList();
   public String paymentType = "";
   public String cashPointId = "";

    public void setOrder(Order ord) {
        amountPaid = ord.getTransactionAmount();
        paymentType = ord.payment.paymentType;
        incOrderId = ord.incrementOrderId;
        userId = ord.userId;
        status = ord.status;
        orderId = ord.id;
        paymentDate = ord.paymentDate;
        orderDate = ord.rowCreatedDate;
        amountExTaxes = ord.getTotalAmount() - ord.getTotalAmountVat();
        amountIncTaxes = ord.getTotalAmount();
        dueDate = ord.getDueDate();
        closed = ord.closed;
        markedPaidDate = ord.markedPaidDate;
        invoiceNote = ord.invoiceNote;
        shipmentLog = ord.shipmentLog;
        cashPointId = ord.getCashPointId();
        
        amountExTaxes = Math.round(amountExTaxes*100)/100;
        amountIncTaxes = Math.round(amountIncTaxes*100)/100;
        
        restAmount = amountIncTaxes - amountPaid + ord.cashWithdrawal;
        start = ord.getStartDateByItems();
        end = ord.getEndDateByItems();
    }

    public boolean isConnectedToCashPointId(String cashPointId) {
        return cashPointId.equals(this.cashPointId);
    }

    public Date getMarkedPaidDate() {
        if(markedPaidDate != null) {
            return markedPaidDate;
        }
        return paymentDate;
    }
    
    public boolean isInvoice() {
        if(paymentType != null && paymentType.toLowerCase().contains("invoice")) {
            return true;
        }
        return false;
    }
}