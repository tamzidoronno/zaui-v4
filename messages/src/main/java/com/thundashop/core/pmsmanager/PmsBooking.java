
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class PmsBooking extends DataCommon {
    public static class BookingStates {
        public static Integer STARTED = 0;
        public static Integer COMPLETED = 1;
        public static Integer DELETED = 2;
    }
    
    public List<PmsBookingRooms> rooms = new ArrayList();
    public String sessionId;
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public BookingContactData contactData = new BookingContactData();
    public String language = "nb_NO";
    public String userId = "";
    public Integer state = 0;
    List<Booking> bookingEngineItems = new ArrayList();
}
