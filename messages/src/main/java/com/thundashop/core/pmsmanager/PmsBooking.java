
package com.thundashop.core.pmsmanager;

import com.google.gson.Gson;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopLogHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PmsBooking extends DataCommon {
 
    public List<PmsBookingRooms> rooms = new ArrayList(); 
    public List<String> notificationsSent = new ArrayList();
    public HashMap<Long, PmsBookingComment> comments = new HashMap(); 
    public String sessionId = null;
    public Date sessionStartDate = null;
    public Date sessionEndDate = null;
    public boolean silentNotification = false;
    
    public List<String> bookingEngineAddons = new ArrayList();
    public RegistrationRules registrationData = new RegistrationRules();
    public String language = "nb_NO";
    public String userId = "";
    public String bookedByUserId = "";
    public Integer state = 0;
    public List<String> orderIds = new ArrayList();
    public HashMap<String, Long> incOrderIds = new HashMap();
    public Integer priceType = 1;
    public boolean confirmed = false;
    public Date confirmedDate = null;
    public boolean unConfirmed = false;
    public boolean isDeleted = false;
    public boolean payedFor = false;
    public boolean avoidCreateInvoice = false;
    public boolean createOrderAfterStay = false;
    public boolean testReservation = false;
    public PmsRepeatingData lastRepeatingData = null;
    public String invoiceNote = "";
    public Integer dueDays;
    public String discountType = "";
    public String couponCode = "";
    public String wubookchannelid = "";
    public String wubookchannelreservationcode = "";
    public String wubookreservationid = "";
    public List<String> wubookModifiedResId = new ArrayList();
    public boolean wubookNoShow = false;
    public boolean transferredToRateManager = false;
    public boolean forceGrantAccess = false;
    public boolean avoidAutoDelete = false;
    
    public String countryCode = "";
    public boolean needCapture;
    public String wubookChannelReservationId;
    public String channel = "";
    public boolean ignoreCheckChangesInBooking = false;
    public String deletedBySource = "";
    private double totalPrice;
    public String paymentType = "";
    public Date orderCreatedAfterStay;
    boolean isConference = false;
    
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    boolean containsSearchWord(String searchWord) {
        searchWord = searchWord.toLowerCase();
        for(PmsBookingRooms room : rooms) {
            if(room.containsSearchWord(searchWord)) {
                return true;
            }
        }
        if(containsOrderId(searchWord)) {
            return true;
        }
        
        for(String value : registrationData.resultAdded.values()) {
            if(value != null) {
                if(value.toLowerCase().contains(searchWord)) {
                    return true;
                }
            }
        }
        
        if(wubookreservationid != null && wubookreservationid.equals(searchWord)) {
            return true;
        }
        if(wubookchannelreservationcode != null && wubookchannelreservationcode.equals(searchWord)) {
            return true;
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
            roomCopied.copyDeleted(room);
            result.confirmed = currentCopy.confirmed;
            
            result.rooms.add(roomCopied);
        }
        
        return result;
    }

    public String createSummary(List<BookingItemType> types) {
        String res = "Reg data: <br>\r\n";
        try {
            for(String field : registrationData.resultAdded.keySet()) {
                res += field + " : " + registrationData.resultAdded.get(field) + "<br>\r\n";
            }

            res += "<br>Rooms:<br>\r\n";
            for(PmsBookingRooms room : getAllRoomsIncInactive()) {
                BookingItemType typeToUse = null;
                if(room.bookingItemTypeId != null) {
                    for(BookingItemType type : types) {
                        if(type.id.equals(room.bookingItemTypeId)) {
                            typeToUse = type;
                        }
                    }
                }
                res += room.date.start + " - " + room.date.end + " - ";
                if(typeToUse != null) {
                    res += " type: " + typeToUse.name;
                }
                if(!room.guests.isEmpty()) {
                    res += ", guest: " + room.guests.get(0).name + " - ";
                }
                
                res += " deleted, " + room.deleted;
                res += "<br>\r\n";
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        
        return res;
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

    public Date getEndDate() {
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

    public boolean isCompletedBooking() {
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

    void updateItem(PmsBookingAddonItem item) {
        for(PmsBookingRooms room : rooms) {
            room.updateItem(item);
        }
    }
    
    void dump() {
        for(String key : registrationData.resultAdded.keySet()) {
            GetShopLogHandler.logPrintStatic(key + " : " + registrationData.resultAdded.get(key), null);
        }
    }

    public List<PmsBookingRooms> getActiveRooms() {
        List<PmsBookingRooms> result = new ArrayList();
        for(PmsBookingRooms room : rooms) {
            if(room.isDeleted()) {
                continue;
            }
            result.add(room);
        }
        return result;
    }

    public void addRoom(PmsBookingRooms room) {
        if(!rooms.contains(room)) {
            rooms.add(room);
        }
    }

    void addRooms(List<PmsBookingRooms> allToAdd) {
        rooms.addAll(allToAdd);
    }

    void removeRooms(List<PmsBookingRooms> toRemove) {
        rooms.removeAll(toRemove);
    }

    List<PmsBookingRooms> getAllRoomsIncInactive() {
        return rooms;
    }

    void removeAllRooms() {
        rooms.clear();
    }

    void removeRoom(PmsBookingRooms room) {
        rooms.remove(room);
    }

    public int getTotalDays() {
        int total = 0;
        for(PmsBookingRooms room : rooms) {
            total += room.getNumberOfDays();
        }
        return total;
    }

    boolean isBookedAfterOpeningHours() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(rowCreatedDate);
        cal2.add(Calendar.DAY_OF_YEAR, 3);
        if(cal2.getTime().before(new Date())) {
            return false;
        }
        
        int today = cal.get(Calendar.HOUR_OF_DAY);
        int weekDay = cal.get(Calendar.DAY_OF_WEEK);
        
        Calendar sameDay = Calendar.getInstance();
        sameDay.setTime(rowCreatedDate);
        
        boolean isSameDay = false;

        if((sameDay.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR)) &&
                (sameDay.get(Calendar.YEAR) == cal.get(Calendar.YEAR))) {
            isSameDay = true;
        }

        if(weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY) {
            return true;
        } else if(today > 15 && isSameDay) {
            return true;
        }
        
        if(weekDay == Calendar.FRIDAY && today > 15) {
            Calendar startCal = Calendar.getInstance();
            startCal.setTime(rowCreatedDate);
            startCal.add(Calendar.DAY_OF_YEAR, -2);
            if(new Date().after(startCal.getTime())) {
                return true;
            }
        }
        
        return false;
    }

    boolean isOld(int minute) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(rowCreatedDate);
        cal.add(Calendar.MINUTE, (minute));
        return cal.getTime().before(new Date());
    }

    boolean hasSentNotification(String notice) {
        notice = notice.toLowerCase();
        for(String sent : notificationsSent) {
            if(sent == null) {
                continue;
            }
            sent = sent.toLowerCase();
            if(sent.contains(notice)) {
                return true;
            }
        }
        return false;
    }

    boolean isRegisteredToday() {
        PmsBookingRooms room = new PmsBookingRooms();
        return room.isSameDay(rowCreatedDate, new Date());
    }

    public boolean containsOrderId(String searchWord) {
        for(Long orderId : incOrderIds.values()) {
            String ordId = orderId + "";
            if(ordId.equals(searchWord)) {
                return true;
            }
        }
        return false;
    }

    void calculateTotalCost() {
        double total = 0.0;
        if(priceType.equals(PriceType.daily)) {
            for(PmsBookingRooms room : rooms) {
                room.calculateTotalCost();
                if(room.isDeleted()) {
                    continue;
                }
                total += room.totalCost;
            }
        }
        totalPrice = total;
    }

    boolean transferredToLock() {
        for(PmsBookingRooms room : rooms) {
            if(room.addedToArx) {
                return true;
            }
        }
        return false;
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
    
    public PmsBookingRooms findRoom(String roomId) {
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
    
    boolean isActiveOnDay(Date day) {
        for(PmsBookingRooms room : rooms) {
            if(room.isActiveOnDay(day)) {
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
                    GetShopLogHandler.logPrintStatic(room.invoicedTo, null);
                }
            }
        }
    }
    
    public boolean hasAddonOfType(String type) {
        return rooms.stream().anyMatch(room -> room.hasAddonOfType(type));
    }
    
    public int getTotalGuestCount() {
        return getActiveRooms().stream().mapToInt(room -> room.guests.size()).sum();
    }
}
