/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.google.gson.Gson;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.common.DataCommon;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Order extends DataCommon implements Comparable<Order> {
    public boolean triedTransferredToAccountingSystem = false;
	public boolean transferedToAccountingSystem = false;
    public String paymentTransactionId = "";
    public Shipping shipping;
    public Payment payment = new Payment();
    public String session;
    public String trackingNumber = "";
    public long incrementOrderId = 0;
    public String reference = "";

	public Order jsonClone() {
		Gson gson = new Gson();
		String gsonOrder = gson.toJson(this);
		Order orderNew = gson.fromJson(gsonOrder, Order.class);
		orderNew.id = UUID.randomUUID().toString();
		orderNew.expiryDate = null;
		orderNew.rowCreatedDate = new Date();
		orderNew.triedTransferredToAccountingSystem = false;
		orderNew.transferedToAccountingSystem = false;
		return orderNew;
	}
    
    public static class Status  {
        public static int CREATED = 1;
        public static int WAITING_FOR_PAYMENT = 2;
        public static int PAYMENT_FAILED = 3;
        public static int COMPLETED = 4;
        public static int CANCELED = 5;
        public static int SENT = 6;
        public static int NEEDCOLLECTING = 7;
    }
    
    public Date createdDate = new Date();
	
	/** 
	 * This expiry date is used for recurring 
	 * orders. 
	 * Orders that has an expiry date will automatically be renewed with 
	 * a new order when it expires.
	 */
	public Date expiryDate;
    
	public Integer recurringDays;
	
	public Integer recurringMonths = 1;
	
    /**
     * The users id for whom placed the order
     * if order is created without user been logged in, this
     * will be empty
     */
    public String userId;
    
    public int status;
    public Cart cart;
    
    public String getDateCreated() {
        if (createdDate == null) {
            return "";
        }
        
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        return sdf.format(createdDate);
    }
    
    public void setStatusCreated() {
        status = Status.CREATED;
    }
    
    public void setStatusPaymentFailed() {
        status = Status.PAYMENT_FAILED;
    }
    
    public void setStatusCompleted() {
        status = Status.COMPLETED;
    }
    
    public void setStatusCanceled() {
        status = Status.CANCELED;
    }
    
    @Override
    public int compareTo(Order o) {
        if(o.createdDate == null || createdDate == null) {
            return 0;
        }
        return createdDate.compareTo(o.createdDate);
    }
} 