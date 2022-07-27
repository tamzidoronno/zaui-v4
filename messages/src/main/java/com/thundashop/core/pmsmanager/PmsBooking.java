
package com.thundashop.core.pmsmanager;

import com.google.gson.Gson;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.RegistrationRules;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopLogHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import org.mongodb.morphia.annotations.Transient;

public class PmsBooking extends DataCommon {
 
    public List<PmsBookingRooms> rooms = new ArrayList(); 
    public List<String> notificationsSent = new ArrayList();
    public HashMap<Long, PmsBookingComment> comments = new HashMap();
    public String shortId = "";
    public String sessionId = null;
    public Date sessionStartDate = null;
    public Date sessionEndDate = null;
    public boolean silentNotification = false;
    public boolean autoSendPaymentLink = false;
    
    public List<String> bookingEngineAddons = new ArrayList();
    public RegistrationRules registrationData = new RegistrationRules();
    public String language = "";
    public String browserLanguage = "";
    public String userId = "";
    public String bookedByUserId = "";
    public String conferenceId = "";
    public Integer state = 0;
    public List<String> orderIds = new ArrayList();
    public List<String> suggestedUserIds = new ArrayList();
    public HashMap<String, Long> incOrderIds = new HashMap();
    public Integer priceType = 1;
    public boolean confirmed = false;
    public Date confirmedDate = null;
    public Date completedDate = null;
    public boolean unConfirmed = false;
    public boolean isDeleted = false;
    public boolean payedFor = false;
    public boolean avoidCreateInvoice = false;
    public boolean createOrderAfterStay = false;
    public boolean testReservation = false;
    public boolean forceGuestsPassportVerification = false;
    public PmsRepeatingData lastRepeatingData = null;
    public String invoiceNote = "";
    public Integer dueDays;
    public Integer periodesToCreateOrderOn;
    public String discountType = "";
    public String couponCode = "";
    public String pmsPricingCode = "";
    public String wubookchannelid = "";
    public String wubookchannelreservationcode = "";
    public String wubookreservationid = "";
    public String alternativeOrginasation = "";
    public List<String> wubookModifiedResId = new ArrayList();
    public boolean wubookNoShow = false;
    public boolean transferredToRateManager = false;
    public boolean avoidAutoDelete = false;
    public boolean setGuestsSameAsBooker = false;
    public Integer incrementBookingId = null;
    
    public String countryCode = "";
    public boolean needCapture;
    public String wubookChannelReservationId;
    public String channel = "";
    public boolean ignoreCheckChangesInBooking = false;
    public boolean ignoreWubook = false;
    public String deletedBySource = "";
    private double totalPrice;
    public String paymentType = "";
    public Date orderCreatedAfterStay;
    boolean isConference = false;
    double bookingAmountDiff;
    public double totalUnsettledAmount = 0.0;
    Double unsettled;
    public boolean nonrefundable = false;
    public boolean tryAutoCharge = false;
    public HashMap<String, String> recieptEmail = new HashMap();
    
    //Jomres Related Properties
    public long jomresBookingId=0;
    public long jomresChannelId=0;
    public String jomresReservationCode="";
    public Date jomresLastModified=null;
    
    @Administrator
    public String secretBookingId = "";
    public boolean ignoreNoShow = false;
    boolean quickReservation = false;
    public String latestwubookreservationid = "";
    public boolean agreedToTermsAndConditions = false;
    
    public boolean segmentClosed = false;
    
    public BrowserVersion browserUsed;
    
    /**
     * First startdate of all rooms
     */
    @Transient
    public Date startDate;
    
    /**
     * Last enddate of all rooms
     */
    @Transient
    public Date endDate;
    
    @Transient
    boolean isAddedToEventList;
       
    @Administrator
    public String token = UUID.randomUUID().toString();
    
    /**
     * If this is set to true it will not show up
     * in the checklist of tasks to do.
     */
    public boolean ignoreUnsettledAmount = false;
    public String segmentId = "";
    public boolean payLater = false;
    
    /**
     * If collected by the gived OTA this will be set to true.
     * Normally this is virtual credit cards.
     */
    public boolean isExpediaCollect = false;
    public boolean isBookingComVirtual = false;
    public boolean isPrePaid = false;
    public boolean agreedToSpam = false;
    public boolean travellingBusiness = false;
    public int fixedBySystemProcess = 0;
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public boolean hasRoom(String roomId) {
        for(PmsBookingRooms r : rooms) {
            if(r.pmsBookingRoomId.equals(roomId)) {
                return true;
            }
        }
        return false;
    }
        
    public List<PmsBookingRooms> getRoomsWithForcedAccess() {
        List<PmsBookingRooms> res = new ArrayList();
        for(PmsBookingRooms r : getActiveRooms()) {
            if(r.forceAccess) {
                res.add(r);
            }
        }
        return res;
    }
    
    public boolean hasForcedAccessedRooms() {
        return !getRoomsWithForcedAccess().isEmpty();
    }

    
    public boolean hasOverBooking() {
        for(PmsBookingRooms room : rooms) {
            if(room.isOverBooking()) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isStartingToday() {
        for(PmsBookingRooms room : getActiveRooms()) {
            if(room.isStartingToday()) {
                return true;
            }
        }
        return false;
    }

    public List<PmsBookingRooms> getOverBookedRooms() {
        List<PmsBookingRooms> res = new ArrayList();
        for(PmsBookingRooms room : rooms) {
            if(room.isOverBooking()) {
                res.add(room);
            }
        }
        return res;
    }
    
    
    public boolean isWeekendBooking() {
        Calendar cal = Calendar.getInstance();
        if(rowCreatedDate != null) {
            cal.setTime(rowCreatedDate);
        }
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        }
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return true;
        }
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY) {
            if(cal.get(Calendar.HOUR_OF_DAY) > 15) {
                return true;
            }
        }
        
        return false;
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
        
        if(incrementBookingId != null && (incrementBookingId + "").equals(searchWord)) {
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
        result.registrationData = registrationData;
        result.id = id;
        for(PmsBookingRooms room : rooms) {
            PmsBookingRooms roomCopied = new PmsBookingRooms();
            roomCopied.pmsBookingRoomId = room.pmsBookingRoomId;
            roomCopied.date = room.date;
            roomCopied.bookingItemId = room.bookingItemId;
            roomCopied.bookingItemTypeId = room.bookingItemTypeId;
            roomCopied.copyDeleted(room);
            result.confirmed = confirmed;
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

    boolean isEndedBefore(Date date) {
        for(PmsBookingRooms room : rooms) {
            if(room.date.end.after(date)) {
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
            for(PmsBookingAddonItem item : room.addons) {                
                if(endDate == null || (item.date != null && item.date.after(endDate))) {
                    endDate = item.date;
                }
            }
            
        }
        return endDate;
    }
    
    public Date getStartDate() {
        Date startDate = null;
        for(PmsBookingRooms room : rooms) {
            if(startDate == null || room.date.start.before(startDate)) {
                startDate = room.date.start;
            }
            for(PmsBookingAddonItem item : room.addons) {
                if(startDate == null || (item.date != null && item.date.before(startDate))) {
                    startDate = item.date;
                }
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

    public boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public boolean isCompletedBooking() {
        if(sessionId == null || sessionId.isEmpty()) {
            return true;
        }
        return false;
    }

    public PmsBookingRooms getRoom(String pmsBookingRoomId) {
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
    
    public String dump() {
        String res = "";
        for(String key : registrationData.resultAdded.keySet()) {
            res += key + " : " + registrationData.resultAdded.get(key) + "<bR>";
        }
        return res;
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

    public List<PmsBookingRooms> getAllRooms() {
        List<PmsBookingRooms> result = new ArrayList();
        for(PmsBookingRooms room : rooms) {
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

    public void calculateTotalCost() {
        if(channel.contains("jomres")){
            totalPrice = rooms.get(0).totalCost;
            return;
        }
        if(!priceType.equals(PmsBooking.PriceType.daily)) {
            return;
        }
        double total = 0.0;
        for(PmsBookingRooms room : rooms) {
            room.calculateTotalCost(priceType);
            total += room.totalCost;
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

    public List<String> getTypes() {
        HashMap<String, Integer> typesToReturn = new HashMap();
        for(PmsBookingRooms r : getActiveRooms()) {
            typesToReturn.put(r.bookingItemTypeId, 1);
        }
        return new ArrayList(typesToReturn.keySet());
    }

    boolean hasRequestedEnding(Date startDate, Date endDate) {
        for(PmsBookingRooms room : rooms) {
            if(room.requestedEndDate(startDate, endDate)) {
                return true;
            }
        }
        return false;
    }

    boolean isWubook() {
        if(channel != null && channel.contains("wubook")) {
            return true;
        }
        return false;
    }

    void unmarkOverBooking() {
        for(PmsBookingRooms room : getAllRoomsIncInactive()) {
            room.unmarkOverBooking();
        }
        for(PmsBookingRooms room : getAllRoomsIncInactive()) {
            if(room.isDeleted() && !room.deletedByChannelManagerForModification) {
                return;
            }
        }
    }

    boolean hasWaitingRooms() {
        for(PmsBookingRooms room : rooms) {
            if(room.addedToWaitingList) {
                return true;
            }
        }
        return false;
    }

    public List<PmsBookingRooms> getWaitingListRooms() {
        List<PmsBookingRooms> res = new ArrayList();
        for(PmsBookingRooms room : rooms) {
            if(room.addedToWaitingList) {
                res.add(room);
            }
        }
        return res;
    }

    boolean containsType(String type) {
        if(type == null) {
            return false;
        }
        
        for(PmsBookingRooms room : rooms) {
            if(room.bookingItemTypeId != null && room.bookingItemTypeId.equals(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This function is only working
     * @return 
     */
    boolean hasActiveApacCodes() {
        for (PmsBookingRooms room : rooms) {
            if (room.codeObject != null) {
                return true;
            }
        }
        
        return false;
    }

    void makeUniqueIds() {
        if(orderIds == null) {
            orderIds = new ArrayList();
        }
        
        HashMap<String,Integer> ids = new HashMap();
        for(String id : orderIds) {
            ids.put(id, 1);
        }
        orderIds = new ArrayList(ids.keySet());
    }

    /**
     * Is payment terminal kiosk.
     * @return 
     */
    boolean isTerminalBooking() {
        if(channel != null && channel.equals("terminal")) {
            return true;
        }
        return false;
    }

    boolean hasNoRefRooms() {
        for(PmsBookingRooms r : rooms) {
            if(r.nonrefundable) 
                return true;
        }
        return false;
    }

    public void setAllRoomsNonRefundable() {
        for(PmsBookingRooms r : rooms) {
            r.nonrefundable = true;
        }
    }

    boolean hasRoomsNotAddedToBookingEngine() {
        if(!isCompletedBooking()) {
            return false;
        }
        
        for(PmsBookingRooms room : rooms) {
            if(room.deleted) {
                continue;
            }
            if(room.addedToWaitingList) {
                continue;
            }
            if(room.isEnded()) {
                continue;
            }
            if(room.isOverBooking()) {
                continue;
            }
            if(!room.bookingConnected()) {
                return true;
            }
        }
        return false;
    }

    boolean isSynxis() {
        if(channel != null && channel.equals("wubook_37")) {
            return true;
        }
        return false;
    }

    boolean isChannel(String channelToCheck) {
        if(channelToCheck == null || channelToCheck.isEmpty()) {
            return true;
        }
        if(channelToCheck.equals("web") && (channel == null || channel.isEmpty())) {
           return true;
        }
        if(this.channel == null) {
            return false;
        }
        
        return this.channel.equals(channelToCheck);
    }

    boolean isStarted() {
        for(PmsBookingRooms r : rooms) {
            if(r.isStarted()) {
                return true;
            }
        }
        return false;
    }

    boolean isEndingAtDay(Date time) {
        for(PmsBookingRooms room : rooms) {
            if(room.isEndingToday(time)) {
                return true;
            }
        }
        return false;
    }

    void markAsCompleted() {
        if(completedDate == null) {
            completedDate = new Date();
            sessionId = "";
        }
    }

    boolean isRecentlyCompleted() {
        if(completedDate == null) {
            return true;
        }
        
        long diff = new Date().getTime() - completedDate.getTime();
        return diff < 60000;
    }

    boolean autosendPaymentLink() {
        //Send payment link on all bookings registered by ota not prepaid,
        if(channel != null && channel.startsWith("wubook_") || avoidAutoDelete || payLater) {
            if(paymentType == null || paymentType.isEmpty()) {
                return true;
            }
        }
        
        return false;
    }

    public double getUnpaidAmount() {
        double amount = 0;
        for(PmsBookingRooms r : getActiveRooms()) {
            amount += r.unpaidAmount;
        }
        return amount;
    }

    public double getUnpaidAmountForAllRooms() {
        double amount = 0;
        for(PmsBookingRooms r : getAllRoomsIncInactive()) {
            amount += r.unpaidAmount;
        }
        return amount;
    }

    boolean isOta() {
        if(channel != null && channel.contains("wubook")) {
            return true;
        }
        return false;
    }

    public boolean hasUnpaidAmount() {
        double unpaid = 0.0;
        for(PmsBookingRooms r : rooms) {
            unpaid += r.unpaidAmount;
        }
        
        return unpaid != 0.0;
    }

    boolean isInvoice() {
        return (paymentType != null && "70ace3f0-3981-11e3-aa6e-0800200c9a66".equals(paymentType));
    }

    String getPmsConferenceRoomId() {
        String roomId = "";
        for(PmsBookingRooms room : rooms) {
            if(room.isPmsConferenceRoom()) {
                return room.pmsBookingRoomId;
            }
        }
        return roomId;
    }

    Iterable<PmsBookingRooms> getActiveRoomsIncNonRefundable() {

        List<PmsBookingRooms> result = new ArrayList();
        for(PmsBookingRooms room : rooms) {
            if(room.isDeleted() && !room.nonrefundable && !nonrefundable) {
                continue;
            }
            result.add(room);
        }
        return result;
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
        if(startDate == null) {
            return false;
        }
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
    
    
    String getHigestReservationCode() {
        long highestCode = -1;
        String curCode = wubookreservationid;
        if(curCode != null && !curCode.isEmpty()) {
            highestCode = new Long(curCode);
        }
        for(String code : wubookModifiedResId) {
            long tmp = new Long(code);
            if(tmp > highestCode) {
                highestCode = tmp;
            }
        }
        return highestCode + "";
    }

}
