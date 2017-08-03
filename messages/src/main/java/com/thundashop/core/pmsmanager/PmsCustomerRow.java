/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.Date;

/**
 *
 * @author boggi
 */
public class PmsCustomerRow {
    public String name;
    public Integer customerId;
    public String customerType;
    public Integer numberOfBookings;
    public Double totalPrice;
    public Integer numberOfOrders;
    public Date latestBooking;
    
    public String preferredPaymentType = "";
    public boolean invoiceAfterStay = false;
    public boolean hasDiscount = false;
    public boolean blocked = false;
    
}
