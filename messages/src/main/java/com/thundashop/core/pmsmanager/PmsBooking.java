
package com.thundashop.core.pmsmanager;

import com.google.gson.Gson;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.DataCommon;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PmsBooking extends DataCommon {
 
    public List<PmsBookingRooms> rooms = new ArrayList(); 
    public HashMap<Long, PmsBookingComment> comments = new HashMap();
    public String sessionId;
    public Date sessionStartDate = null;
    public Date sessionEndDate = null;
    
    public List<PmsBookingAddonItem> addons = new ArrayList();
    public List<String> bookingEngineAddons = new ArrayList();
    public RegistrationRules registrationData = new RegistrationRules();
    public String language = "nb_NO";
    public String userId = "";
    public Integer state = 0;
    public List<String> orderIds = new ArrayList();
    public Integer priceType = 1;
    public boolean confirmed = false;
    public boolean unConfirmed = false;
    public boolean isDeleted = false;
    public boolean payedFor = false;
    public PmsRepeatingData lastRepeatingData = null;
    public String invoiceNote = "";
    public String couponCode = "";
    public String wubookchannelid = "";
    public String wubookchannelreservationcode = "";
    public String wubookreservationid = "";
    public String countryCode = "";
    
    boolean containsSearchWord(String searchWord) {
        searchWord = searchWord.toLowerCase();
        for(PmsBookingRooms room : rooms) {
            if(room.containsSearchWord(searchWord)) {
                return true;
            }
        }
        
        for(String value : registrationData.resultAdded.values()) {
            if(value != null) {
                if(value.toLowerCase().contains(searchWord)) {
                    return true;
                }
            }
        }
        
        return false;
    }

    PmsBooking copy() {
        Gson res = new Gson();
        String copytext = res.toJson(this);
        return res.fromJson(copytext, PmsBooking.class);
    }

    PmsBooking copyUnsecure() {
        PmsBooking result = new PmsBooking();
        PmsBooking currentCopy = copy();
        for(PmsBookingRooms room : currentCopy.rooms) {
            PmsBookingRooms roomCopied = new PmsBookingRooms();
            roomCopied.date = room.date;
            roomCopied.bookingItemId = room.bookingItemId;
            roomCopied.bookingItemTypeId = room.bookingItemTypeId;
            result.confirmed = currentCopy.confirmed;
            
            result.rooms.add(roomCopied);
        }
        
        return result;
    }

    boolean hasStayAfter(Date startInvoiceFrom) {
        for(PmsBookingRooms room : rooms) {
            if(room.date.end.after(startInvoiceFrom)) {
                return true;
            }
        }
        return false;
    }

    boolean isEnded() {
        for(PmsBookingRooms room : rooms) {
            if(room.date.end.after(new Date())) {
                return false;
            }
        }
        return true;
    }

    Date getEndDate() {
        Date endDate = null;
        for(PmsBookingRooms room : rooms) {
            if(endDate == null || room.date.end.after(endDate)) {
                endDate = room.date.end;
            }
        }
        return endDate;
    }
    
    Date getStartDate() {
        Date startDate = null;
        for(PmsBookingRooms room : rooms) {
            if(startDate == null || room.date.start.before(startDate)) {
                startDate = room.date.start;
            }
        }
        return startDate;
    }

    boolean isEndedOverTwoMonthsAgo() {
        Date ended = getEndDate();
        if(ended == null) {
            return false;
        }
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MONTH, -2);
        if(now.getTime().after(ended)) {
            return true;
        }
        return false;
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    boolean isCompletedBooking() {
        if(sessionId == null || sessionId.isEmpty()) {
            return true;
        }
        return false;
    }

    PmsBookingRooms getRoom(String pmsBookingRoomId) {
        for(PmsBookingRooms room : rooms) {
            if(room.pmsBookingRoomId.equals(pmsBookingRoomId)) {
                return room;
            }
        }
        
        return null;
    }

    public static class PriceType {
        public static Integer daily = 1;
        public static Integer monthly = 2;
        public static Integer weekly = 3;
        public static Integer minutly = 4;
        public static Integer hourly = 5;
        public static Integer secondly = 6;
        public static Integer progressive = 7;
        public static Integer interval = 8;
    }
       
    public static class BookingStates {
        public static Integer STARTED = 0;
        public static Integer COMPLETED = 1;
        public static Integer DELETED = 2;
    }
    
    void attachBookingItems(List<Booking> bookingsToAdd) {
        for(PmsBookingRooms room : rooms) {
            for(Booking booking : bookingsToAdd) {
                if(room.pmsBookingRoomId.equals(booking.externalReference)) {
                    room.bookingId = booking.id;
                }
            }
        }
    }
    
    PmsBookingRooms findRoom(String roomId) {
        for(PmsBookingRooms room : rooms) {
            if(room.pmsBookingRoomId.equals(roomId)) {
                return room;
            }
        }
        return null;
    }

    boolean isActiveInPeriode(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.isActiveInPeriode(startDate, endDate)) {
                return true;
            }
        }
        return false;
    }    

    boolean checkingInBetween(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.checkingInBetween(startDate, endDate)) {
                return true;
            }
        }
        return false;
    }

    boolean checkingOutBetween(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.checkingOutBetween(startDate, endDate)) {
                return true;
            }
        }
        return false;
    }
    
    public void printInvoicedTo() {
        for(PmsBookingRooms room : rooms) {
            if(room.invoicedTo != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(room.invoicedTo);
                if(cal.get(Calendar.MONTH) == 2) {
                    System.out.println(room.invoicedTo);
                }
            }
        }
    }
}
