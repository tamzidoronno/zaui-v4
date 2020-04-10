/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pga;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsConference;
import com.thundashop.core.pmsmanager.PmsConferenceEvent;
import com.thundashop.core.pmsmanager.PmsRoomPaymentSummary;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPgaManager {
    public PmsConference getConference(String token);
    public List<PmsBookingRooms> getRooms(String token);
    public PmsBooking getBooking(String token);
    public User getUser(String token);
    public List<PmsConferenceEvent> getEvents(String token);
    public String getBookingItemTypeName(String bookingItemTypeId);
    public void updateGuests(String token, PgaBooking booking);
    public List<PmsRoomPaymentSummary> getSummaries(String token);
}