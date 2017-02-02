package com.thundashop.core.pmsmanager;

import java.util.Date;

/**
 *
 * @author boggi
 */
public class PmsSubscriptionOverview {
    String userId;
    String usersName;
    String roomName;
    Double price;
    boolean paid = false;
    long end;
    long start;
    
    String orderPaymentType;
    Date orderCreationDate;
    Date invoicedTo;
    Date lastOrderInvoicedTo;
    Double orderValue;
    String paymentType;
    Integer cardsSaved = 0;
    Date latestInvoiceEndDate;
    Date latestInvoiceStartDate;
    boolean confirmed = false;
}
