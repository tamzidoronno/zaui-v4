/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

/**
 *
 * @author boggi
 */
public class AdditionalCrmData {
    Integer numberOfBookings;
    Integer numberOfRooms;
    Integer numberOfGuests;
    boolean invoiceAfterStay;
    boolean hasDiscount;
    Double discount = 0.0;
    Double price = 0.0;
    Integer discountType;
}
