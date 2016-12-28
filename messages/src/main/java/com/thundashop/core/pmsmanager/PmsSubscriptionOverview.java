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
    Double orderValue;
    String paymentType;
    Integer cardsSaved = 0;
}
