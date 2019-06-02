/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class PmsRoomPaymentSummaryRow implements Serializable {
    /**
     * All product ids accosicated to this date and type room or booking
     */
    public List<String> productIds = new ArrayList();
    
    /**
     * Key = orderid
     * 
     * All cartitem ids accosicated to this date and type room or booking
     */
    public HashMap<String, List<String>> cartItemIds = new HashMap();
    
    /**
     * The calculated amount in the booking
     */
    public double priceInBooking;
    
    /**
     * Total amount that all orders sumed together is for the date.
     */
    public double createdOrdersFor;
    
    /**
     * Does consider paymentstatus of the order (status = 7) 
     */
    public double actuallyPaidAmount;
    
    /**
     * This is the count needed to be created orders for.
     */
    public int count;
    
    /**
     * The count of this product in the order.
     */
    public int countFromOrders = 0;
    
    /**
     * The count of this product in the booking.
     */
    public int countFromBooking = 0;
    
    /**
     * The date where this should be accounted for.
     */
    public String date;
    
    /**
     * Will be true if this is accosiated with an an addon that is included
     * in the roomprice.
     */
    boolean includedInRoomPrice = false;
    
    /**
     * Sepeartes if this is a accomodation row.
     */
    boolean isAccomocation = false;
    
    /**
     * Use this price when you want to create and order.
     */
    double priceToCreateOrders = 0D;
    
    /**
     * What product id should be used when creating the next order.
     */
    public String createOrderOnProductId = "";
    
    /**
     * field createdOrdersFor based on payment type.
     */
    HashMap<String, Double> createdOrdersForByPaymentMethods;

    public Date getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date date2 = sdf.parse(date.trim());
            return date2;
        } catch (ParseException ex) {
            Logger.getLogger(PmsRoomPaymentSummaryRow.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }

    public boolean needToCreateOrderFor() {
        double diff = priceInBooking - createdOrdersFor;
        boolean needToCreate =  !(diff < 0.1 && diff > -0.01);
        return needToCreate;
    }

    boolean isEmpty() {
        if (count == 0 && countFromBooking == 0 && countFromOrders == 0)
            return true;
        
        double totalInBooking = countFromBooking * priceInBooking;
        double totalInOrder = countFromOrders * createdOrdersFor;
        
        return totalInBooking == 0 && totalInOrder == 0;
    }
}
