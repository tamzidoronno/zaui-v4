
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class PmsBooking extends DataCommon {
    public List<PmsBookingRooms> rooms = new ArrayList();
    public List<PmsBookingDateRange> dates = new ArrayList();
    public String sessionId;
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public BookingContactData contactData = new BookingContactData();
    public String language = "nb_NO";
    public String userId = "";
    List<Booking> bookingEngineItems = new ArrayList();
    
}
