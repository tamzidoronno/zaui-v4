/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.thundashop.core.bookingengine.data.Booking;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class OptimalBookingTimeLine {
    public String uuid = UUID.randomUUID().toString();
    public List<Booking> bookings = new ArrayList();
}
