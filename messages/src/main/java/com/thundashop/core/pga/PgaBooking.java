/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.pga;

import com.thundashop.core.pmsmanager.PmsGuests;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class PgaBooking {
    public String pmsBookingRoomId = "";
    public List<PmsGuests> guests = new ArrayList();

    public PmsGuests getGuest(String guestId) {
        return guests.stream().filter(o -> o.guestId != null && o.guestId.equals(guestId)).findAny().orElse(null);
    }
}
