/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author boggi
 */
public class PmsBookingRooms {
    public String bookingItemTypeId = "";
    public String pmsBookingRoomId = UUID.randomUUID().toString();
    public List<PmsGuests> guests = new ArrayList();
    public PmsBookingDateRange date = new PmsBookingDateRange();
    public Integer numberOfGuests = 0;
    public String bookingEngineItemId = "";
    public double count = 1;
    public double price = 108;
    public double taxes = 8;
}
