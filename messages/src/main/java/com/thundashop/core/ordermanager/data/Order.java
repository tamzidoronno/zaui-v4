/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager.data;

import com.google.gson.Gson;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.cartmanager.data.Cart;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.DataCommon;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Order extends DataCommon implements Comparable<Order> {

    public Boolean triedTransferredToAccountingSystem = false;
    public Boolean transferredToAccountingSystem = false;
    public Date transferredToCreditor = null;
    
    /**
     * This variable is wrong and should be removed. The one above is the corrent one.
     */
    private Boolean transferedToAccountingSystem = false;
        
    public String paymentTransactionId = "";
    public Shipping shipping;
    public Payment payment = new Payment();
    public String session;
    public String trackingNumber = "";
    public long incrementOrderId = 0;
    public String reference = "";
    public boolean activated = false;
    public boolean testOrder = false;
    public boolean captured = false;
    public List<CardTransaction> transactions = new ArrayList();
    public List<OrderLog> logLines = new ArrayList();
    public List<String> notifications = new ArrayList();
    public String invoiceNote = "";
    public boolean closed = false;
    public List<String> creditOrderId = new ArrayList();
    public boolean isCreditNote = false;
    public Date startDate = null;
    public Date endDate = null;
    public Date paymentDate = null;
    public Integer paymentTerms = 15;
    public String parentOrder = "";
    public boolean sentToCustomer = false;
    private boolean cleaned = false;
    public Date dateTransferredToAccount;
    
    public Order jsonClone() {
        Gson gson = new Gson();
        String gsonOrder = gson.toJson(this);
        Order orderNew = gson.fromJson(gsonOrder, Order.class);
        orderNew.id = UUID.randomUUID().toString();
        orderNew.expiryDate = null;
        orderNew.rowCreatedDate = new Date();
        orderNew.triedTransferredToAccountingSystem = false;
        orderNew.transferredToAccountingSystem = false;
        orderNew.createdDate = new Date();

        if (orderNew.cart != null) {
            orderNew.cart.rowCreatedDate = new Date();
        }

        return orderNew;
    }

    public void checkForCorrectingTransferredToAccounting() {
        transferredToAccountingSystem = transferedToAccountingSystem;
    }
    
    public boolean useForStatistic() {
        if (status == Order.Status.CANCELED || status == Order.Status.PAYMENT_FAILED) {
            return false;
        }
        
        if (testOrder) {
            return false;
        }
        
        return true;
    }
    
    public void changePaymentType(Application paymentApplication) {
        if (payment == null) {
            payment = new Payment();
        }
        
        payment.paymentType = "ns_"+paymentApplication.id.replace("-", "_")+"\\"+paymentApplication.appName;
    }

    public boolean triedAutoPay() {
        if(payment.triedAutoPay.isEmpty()) {
            return false;
        }
        
        if(payment.triedAutoPay.size() >= 50) {
            //Tried to pay with card for 50 days.. time to give up.
            return true;
        }
        Calendar yestercal = Calendar.getInstance();
        yestercal.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterdate = yestercal.getTime();
        for(Date lastTime : payment.triedAutoPay) {
            if(lastTime.after(yesterdate)) {
                return true;
            }
        }
        
        return false;
       
    }

    public boolean createdOnDay(Date time) {
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(rowCreatedDate);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        if((createdCal.get(Calendar.YEAR) == timeCal.get(Calendar.YEAR)) && 
            (createdCal.get(Calendar.DAY_OF_YEAR) == timeCal.get(Calendar.DAY_OF_YEAR))) {
            return true;
        }
        return false;
    }
    
    public boolean paidOnDay(Date time) {
        if(paymentDate == null) {
            return false;
        }
        Calendar createdCal = Calendar.getInstance();
        createdCal.setTime(paymentDate);
        Calendar timeCal = Calendar.getInstance();
        timeCal.setTime(time);
        
        if((createdCal.get(Calendar.YEAR) == timeCal.get(Calendar.YEAR)) && 
            (createdCal.get(Calendar.DAY_OF_YEAR) == timeCal.get(Calendar.DAY_OF_YEAR))) {
            return true;
        }
        return false;
    }

    public void doFinalize() {
        if(cart == null || cart.getItems() == null) {
            return;
        }
        for(CartItem item : cart.getItems()) {
            item.doFinalize();
        }
        if(!closed) {
            if(status == Order.Status.PAYMENT_COMPLETED) {
                closed = true;
            }
            if(transferredToAccountingSystem) {
                closed = true;
            }
        }
        if(status == Order.Status.PAYMENT_COMPLETED && paymentDate == null) {
            paymentDate = rowCreatedDate;
        }
    }

    public boolean needToBeTranferredToCreditor() {
        if(transferredToCreditor != null) {
            return false;
        }
        
        if(status == Order.Status.PAYMENT_COMPLETED) {
            return false;
        }
        
        for(CartItem item : cart.getItems()) {
            if(item.periodeStart == null) {
                continue;
            }
            if(item.getProduct().minPeriode <= 0) {
                continue;
            }
            
            long diff = item.getStartingDate().getTime() - item.periodeStart.getTime();
            
            diff /= 1000;
            
            if(diff < item.getProduct().minPeriode) {
                return true;
            }
        }
        
        return false;
    }

    public boolean isInvoice() {
        if(payment != null && payment.paymentType != null && payment.paymentType.toLowerCase().contains("invoice")) {
            return true;
        }
        return false;
    }

    /**
     * Added because there was lots of bogus data added to translation, causing the orders to become huge objects.
     * 
     * @return 
     */
    public boolean cleanMe() {
        if (cart == null)
            return false;
        
        if (cleaned)
            return false;
        
        for (CartItem cartItem : cart.getItems()) {
            cartItem.getProduct().validateTranslationMatrix();
        }
        
        cleaned = true;
        return true;
    }

    public boolean hasPaymentMethod(String paymentMethod) {
        if(payment != null && payment.paymentType != null && paymentMethod != null) {
            String methodToTest = paymentMethod.replace("-", "_");
            if(payment.paymentType.contains(methodToTest)) {
                return true;
            }
        }
        return false;
    }

    public static class Status  {
        public static int CREATED = 1;
        public static int WAITING_FOR_PAYMENT = 2;
        public static int PAYMENT_FAILED = 3;
        public static int COMPLETED = 4;
        public static int CANCELED = 5;
        public static int SENT = 6;
        public static int PAYMENT_COMPLETED = 7;
        public static int COLLECTION_FAILED = 8;
        public static int NEEDCOLLECTING = 9;
        public static int SEND_TO_INVOICE = 10;
    }
    
    public Date createdDate = new Date();
    
    /**
     * This expiry date is used for recurring orders. Orders that has an expiry
     * date will automatically be renewed with a new order when it expires.
     */
    public Date expiryDate;

    public Integer recurringDays;

    public Integer recurringMonths = 1;

    /**
     * The users id for whom placed the order if order is created without user
     * been logged in, this will be empty
     */
    public String userId;
    
    public int status = Order.Status.CREATED;
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
    
    public void updateCount(String cartItemId, Integer count) {
        if (cart.getCartItem(cartItemId) == null) {
            return;
        }
        cart.setProductCount(cartItemId, count);
    }
    
    public void removeAllItemsExcept(List<String> ids) {
        List<String> toRemove = new ArrayList();
        for(CartItem item : cart.getItems()) {
            toRemove.add(item.getCartItemId());
        }
        
        toRemove.removeAll(ids);
        
        for(String remove : toRemove) {
            cart.removeItem(remove);
        }
    }
    
    public void updatePrice(String cartItemId, double price) {
        if (cart.getCartItem(cartItemId) == null) {
            return;
        }
        
        double oldPrice = cart.getCartItem(cartItemId).getProduct().price;
        cart.updatePrice(cartItemId, price);
        
        OrderLog log = new OrderLog();
        log.userId = userId;
        log.description = "CartItem price changed price from " + oldPrice + " to " + price;
    }

    public Date getStartDateByItems() {
        Date start = null;
        for(CartItem item : cart.getItems()) {
            if(item != null && item.getStartingDate() != null && (start == null || start.before(item.getStartingDate()))) {
                start = item.getStartingDate();
            }
        }
        return start;
    }


    public Date getEndDateByItems() {
        Date end = null;
        for(CartItem item : cart.getItems()) {
            if(end == null || end.after(item.getEndingDate())) {
                end = item.getEndingDate();
            }
        }
        return end;
    }
    
    public boolean matchOnString(String searchWord) {
        if (searchWord == null || searchWord.isEmpty()) 
            return true;
        
        boolean match = false;
        
        if (cart != null && cart.address != null && cart.address.fullName != null) {
            match = cart.address.fullName.toLowerCase().contains(searchWord.toLowerCase());
        }
        if(searchWord.equals(incrementOrderId + "")) {
            return true;
        }
        
        // Add more search criterias here ? :D
        
        return match;
    }
} 