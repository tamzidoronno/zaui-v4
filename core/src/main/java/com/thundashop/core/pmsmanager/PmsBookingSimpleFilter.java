
package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class PmsBookingSimpleFilter { 
 
    private final PmsManager manager;
    private final PmsInvoiceManager pmsInvoiceManager;

    PmsBookingSimpleFilter(PmsManager aThis, PmsInvoiceManager pmsInvoiceManager) {
        this.manager = aThis;
        this.pmsInvoiceManager = pmsInvoiceManager;
    }

    LinkedList<PmsRoomSimple> filterRooms(PmsBookingFilter filter) {
        LinkedList<PmsRoomSimple> result = new LinkedList();
        List<PmsBooking> bookings = manager.getAllBookings(filter);
        this.manager.gsTiming("GetAll bookings");
        int i = 0;
        for(PmsBooking booking : bookings) {
            if(booking.containsOrderId(filter.searchWord)) {
                for(PmsBookingRooms room : booking.getAllRoomsIncInactive()) {
                    result.add(convertRoom(room, booking));
                    i++;
                }
            } else {
                List<PmsBookingRooms> rooms = booking.getActiveRooms();
                if(filter.includeDeleted) {
                    rooms = booking.getAllRoomsIncInactive();
                }
                for(PmsBookingRooms room : rooms) {
                    if(inFilter(room, filter, booking)) {
                        result.add(convertRoom(room, booking));
                        i++;
                    }
                }
            }
            if(i >= 200) {
                break;
            }
        }
        this.manager.gsTiming("before sorting list");
        sortList(result, filter.sorting);
        if(filter.groupByBooking) {
            result = groupByBooking(result);
        }
        return result;
    }

    
    private List<PmsRoomSimple> sortList(List<PmsRoomSimple> result, String sorting) {
        if (sorting == null) {
            sorting = "";
        }

        if (sorting.equals("visitor") || sorting.equals("visitor_desc")) {
            Collections.sort(result, new Comparator<PmsRoomSimple>(){
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2){
                    return o1.owner.compareTo(o2.owner);
                }
            });
        } else if (sorting.equals("state") || sorting.equals("state_desc")) {
            Collections.sort(result, new Comparator<PmsRoomSimple>(){
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2){
                    return o1.progressState.compareTo(o2.progressState);
                }
            });
            
        } else if (sorting.equals("periode") || sorting.equals("periode_desc")) {
            Collections.sort(result, new Comparator<PmsRoomSimple>() {
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2) {
                    if(o1.start < o2.start) {
                        return -1;
                    }
                    return 1;
                }
            });
        } else if (sorting.equals("invoicedto") || sorting.equals("invoicedto_desc")) {
            Collections.sort(result, new Comparator<PmsRoomSimple>() {
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2) {
                    if(o1.invoicedTo == null || o2.invoicedTo == null) {
                        return -1;
                    }
                    return o1.invoicedTo.compareTo(o2.invoicedTo);
                }
            });
        } else if (sorting.equals("room") || sorting.equals("room_desc")) {
            Collections.sort(result, new Comparator<PmsRoomSimple>() {
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2) {
                    return o1.room.compareTo(o2.room);
                }
            });
        } else if (sorting.equals("price") || sorting.equals("price_desc")) {
            Collections.sort(result, new Comparator<PmsRoomSimple>() {
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2) {
                    if(o1.price < o2.price) {
                        return -1;
                    }
                    return 1;
                }
            });
        } else {
            Collections.sort(result, new Comparator<PmsRoomSimple>() {
                public int compare(PmsRoomSimple o1, PmsRoomSimple o2) {
                    return o2.regDate.compareTo(o1.regDate);
                }
            });
        }

        if (sorting.contains("_desc")) {
            Collections.reverse(result);
        }

        return result;
    }

    
    public PmsRoomSimple convertRoom(PmsBookingRooms room, PmsBooking booking) {
        PmsRoomSimple simple = new PmsRoomSimple();
        simple.start = room.date.start.getTime();
        simple.end = room.date.end.getTime();
        simple.bookingItemId = room.bookingItemId;
        simple.bookingTypeId = room.bookingItemTypeId;
        if(room.booking != null) {
            simple.bookingTypeId = room.booking.bookingItemTypeId;
        }
        simple.bookingEngineId = room.bookingId;
        simple.addons = room.addons;
        simple.price = room.price;
        simple.checkedIn = false;
        simple.checkedOut = false;
        simple.invoicedTo = room.invoicedTo;
        simple.regDate = booking.rowCreatedDate;
        simple.keyIsReturned = room.keyIsReturned;
        simple.wubookreservationid = booking.wubookreservationid;
        simple.wubookchannelid = booking.wubookchannelreservationcode;
        simple.testReservation = booking.testReservation;
        simple.orderIds = booking.orderIds;
        simple.channel = booking.channel;
        simple.numberOfNights = room.getNumberOfDays();
        simple.numberOfRoomsInBooking = booking.getActiveRooms().size();
        simple.createOrderAfterStay = booking.createOrderAfterStay;
        simple.cleaningComment = room.cleaningComment;
        simple.bookingComments = booking.comments;
        simple.totalUnsettledAmount = pmsInvoiceManager.getTotalUnpaidOnRoom(room, booking, false);
        simple.hasUnchargedPrePaidOrders = pmsInvoiceManager.hasUnchargedPrePaidOrders(room, booking);
        simple.totalCost = room.totalCost;
        simple.totalUnpaidCost = pmsInvoiceManager.getTotalUnpaidOnRoom(room, booking,true);
        simple.requestedEndDate = room.requestedEndDate;
        simple.userId = booking.userId;
        simple.language = room.language;
        simple.segmentName = "";
        if(booking.segmentId != null && !booking.segmentId.isEmpty()) {
            simple.segmentName = manager.pmsCoverageAndIncomeReportManager.getSegment(booking.segmentId).name;
        }
        if(simple.language == null || simple.language.isEmpty()) {
            simple.language = booking.language;
        }
        simple.countryCode = booking.countryCode;
        if(simple.countryCode == null || simple.countryCode.isEmpty()) {
            simple.countryCode = manager.getDefaultCountryCode();
        }
       
        
        if(manager.hasLockSystemActive()) {
            simple.code = room.code;
        }
        simple.pmsRoomId = room.pmsBookingRoomId;
        simple.bookingId = booking.id;
        simple.nonrefundable = room.nonrefundable;
        User user = manager.userManager.getUserByIdUnfinalized(booking.userId);
        if(user != null) {
            simple.owner = user.fullName;
            simple.ownerDesc = user.description;
            simple.ownersEmail = user.emailAddress;
            simple.ownersPrefix = user.prefix;
            simple.ownersPhone = user.cellPhone;
        }
        simple.guest = room.guests;
        if(room.bookingItemId != null && !room.bookingItemId.isEmpty() && manager.bookingEngine.getBookingItem(room.bookingItemId) != null) {
            simple.room = manager.bookingEngine.getBookingItem(room.bookingItemId).bookingItemName;
        }
        if(room.bookingItemTypeId != null && !room.bookingItemTypeId.isEmpty()) {
            BookingItemType type = manager.bookingEngine.getBookingItemType(room.bookingItemTypeId);
            if(type != null) {
                simple.roomType = manager.bookingEngine.getBookingItemType(room.bookingItemTypeId).name;
            }
        }
        
        simple.paidFor = pmsInvoiceManager.isRoomPaidForWithBooking(room.pmsBookingRoomId, booking);
        if(room.isDeleted() || booking.isDeleted) {
            simple.progressState = "deleted";
        } else if(room.isStarted() && !room.isEnded()) {
            if(manager.hasLockSystemActive() && !room.addedToArx) {
                simple.progressState = "waitingforlock";
            } else {
                simple.progressState = "active";
            } 
        } else if(room.isEnded()) {
            simple.progressState = "ended";
        } else if(booking.confirmed) {
            simple.progressState = "confirmed";
        }
        
        if(!simple.paidFor && 
                manager.getConfigurationSecure().getRequirePayments() && 
                !simple.progressState.equals("deleted") && !room.forceAccess && !room.addedToArx) {
            simple.progressState = "notpaid";
        } 
        
        if(!booking.confirmed && !room.deleted && !booking.isDeleted) {
            simple.progressState = "unconfirmed";
        }
        
        if(booking.testReservation) {
            simple.progressState = "test";
        }
        
        if(room.blocked) {
            simple.progressState = "blocked";
        }
        
        if(room.isOverBooking()) {
            simple.progressState = "overbook";
        }
        if(room.deletedByChannelManagerForModification) {
            simple.progressState = "replaced";
        }
        if(room.addedToWaitingList) {
            simple.progressState = "waiting";
        }
        
        simple.checkedIn = room.checkedin;
        simple.checkedOut = room.checkedout;
        
        simple.numberOfGuests = room.numberOfGuests;
        simple.transferredToArx = room.addedToArx;
        simple.priceType = booking.priceType;
        
        for(PmsBookingAddonItem item : room.addons) {
            if(item.addonType == PmsBookingAddonItem.AddonTypes.LATECHECKOUT) { simple.latecheckout = true; }
            if(item.addonType == PmsBookingAddonItem.AddonTypes.EXTRABED) { simple.extrabed = true; }
            if(item.addonType == PmsBookingAddonItem.AddonTypes.EXTRACHILDBED) { simple.extrabed = true; }
            if(item.addonType == PmsBookingAddonItem.AddonTypes.EARLYCHECKIN) { simple.earlycheckin = true; }
        }
        return simple;
    }


    private boolean inFilter(PmsBookingRooms room, PmsBookingFilter filter, PmsBooking booking) {
        if (filter.searchWord != null && !filter.searchWord.isEmpty()) {
            User user = manager.userManager.getUserById(booking.userId);
            if(booking.id != null && booking.id.equals(filter.searchWord)) {
                return true;
            } else if (user != null && user.fullName != null && user.fullName.toLowerCase().contains(filter.searchWord.toLowerCase())) {
                return true;
            } else if (room.containsSearchWord(filter.searchWord)) {
                return true;
            }

            if (room.bookingItemId != null && !room.bookingItemId.isEmpty()) {
                BookingItem item = manager.bookingEngine.getBookingItem(room.bookingItemId);
                if (item != null && item.bookingItemName != null && item.bookingItemName.contains(filter.searchWord)) {
                    return true;
                }
            }
             if(booking.wubookreservationid != null && booking.wubookreservationid.equals(filter.searchWord)) {
                return true;
            }
            if(booking.wubookchannelreservationcode != null && booking.wubookchannelreservationcode.equals(filter.searchWord)) {
                return true;
            }
            if(booking.incrementBookingId != null && (booking.incrementBookingId+"").equals(filter.searchWord)) {
                return true;
            }
        } else if (filter.bookingId != null && filter.bookingId.equals(booking.id)) {
            return true;
        } else if (filter.filterType == null || filter.filterType.equals("registered")) {
            if (filter.startDate == null || (booking.rowCreatedDate.after(filter.startDate) && booking.rowCreatedDate.before(filter.endDate))) {
                return true;
            }
        } else if (filter.filterType == null || filter.filterType.equals("unpaid")) {
            if(!pmsInvoiceManager.isRoomPaidFor(room.pmsBookingRoomId)) {
                return true;
            }
        } else if (filter.filterType == null || filter.filterType.equals("requestedending")) {
            if(room.requestedEndDate(filter.startDate, filter.endDate)) {
                return true;
            }
        } else if (filter.filterType == null || filter.filterType.equals("afterstayorder")) {
            if(booking.createOrderAfterStay) {
                return true;
            }
        } else if (filter.filterType.equals("activecheckin")) {
            if((room.isActiveInPeriode(filter.startDate, filter.endDate) || room.isStartingToday(filter.startDate)) && !room.isEndingToday(filter.startDate)) {
                return true;
            }
        } else if (filter.filterType.equals("waiting") && !room.deletedByChannelManagerForModification) {
            if((room.isActiveInPeriode(filter.startDate, filter.endDate) || room.isStartingToday(filter.startDate)) && !room.isEndingToday(filter.startDate) && (room.addedToWaitingList || room.isOverBooking())) {
                return true;
            }
        } else if (filter.filterType.equals("activecheckout")) {
            if((room.isActiveInPeriode(filter.startDate, filter.endDate) || room.isEndingToday(filter.endDate)) && !room.isStartingToday(filter.startDate)) {
                return true;
            }
        } else if (filter.filterType.equals("inhouse")) {
            if(room.checkedin && !room.checkedout) {
                return true;
            }
        } else if (filter.filterType.equals("active")) {
            if (room.isActiveInPeriode(filter.startDate, filter.endDate)) {
                 return true;
            }
        } else if (filter.filterType.equals("uncofirmed")) {
            if (!booking.confirmed) {
                 return true;
            }
        } else if (filter.filterType.equals("checkin")) {
            if (room.checkingInBetween(filter.startDate, filter.endDate)) {
                return true;
            }
        } else if (filter.filterType.equals("checkout")) {
            if (room.checkingOutBetween(filter.startDate, filter.endDate)) {
                return true;
            }
        } else if (filter.filterType.equals("deleted")) {
            if (booking.isDeleted) {
               return true;
            }
        }
        return false;
    }

    private LinkedList<PmsRoomSimple> groupByBooking(LinkedList<PmsRoomSimple> result) {
        List<String> bookingsAdded = new ArrayList();
        LinkedList<PmsRoomSimple> toReturn = new LinkedList();
        for(PmsRoomSimple simple : result) {
            if(!bookingsAdded.contains(simple.bookingId)) {
                toReturn.add(simple);
                bookingsAdded.add(simple.bookingId);
            }
        }
        return toReturn;
    }
    
}
