package com.thundashop.core.ordermanager.data;

import java.util.Date;

public class OrderResult {
   public String orderId;
   public long incOrderId;
   public String user;
   public String userId;
   public Integer status;
   public Date orderDate;
   public Date dueDate;
   public Date paymentDate;
   public Date start;
   public Date end;
   public double amountExTaxes = 0;
   public double amountIncTaxes = 0;
   public double amountPaid = 0;
   public double restAmount = 0;

    public void setOrder(Order ord) {
        amountPaid = ord.getTransactionAmount();
        
        incOrderId = ord.incrementOrderId;
        userId = ord.userId;
        status = ord.status;
        orderId = ord.id;
        paymentDate = ord.paymentDate;
        orderDate = ord.rowCreatedDate;
        amountExTaxes = ord.getTotalAmount() - ord.getTotalAmountVat();
        amountIncTaxes = ord.getTotalAmount();
        dueDate = ord.getDueDate();
        
        amountExTaxes = Math.round(amountExTaxes*100)/100;
        amountIncTaxes = Math.round(amountIncTaxes*100)/100;
        
        restAmount = amountIncTaxes - amountPaid;
        start = ord.getStartDateByItems();
        end = ord.getEndDateByItems();
    }
}
