package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class PmsStatisticsBuilder {
    private final List<PmsBooking> bookings;
    private final List<PmsBooking> allBookings;
    private final boolean pricesExTax;
    private HashMap<Integer, PmsBudget> budget;
    private final UserManager userManager;
    private final OrderManager orderManager;
    private final BookingEngine bookingEngine;
    private HashMap<String, Integer> cachedSquareMetersForType = new HashMap();
    HashMap<String, PmsAdditionalItemInformation> addiotionalItemInfo = new HashMap();
    private List<Booking> allBookingsInEngine;

    PmsStatisticsBuilder(List<PmsBooking> bookingsInFilter, 
            boolean pricesExTax, 
            UserManager userManager, 
            List<PmsBooking> allBookings, 
            HashMap<String, PmsAdditionalItemInformation> addiotionalItemInfo,
            OrderManager orderManager, 
            BookingEngine bookingEngine) {
        this.bookings = bookingsInFilter;
        this.userManager = userManager;
        this.pricesExTax = pricesExTax;
        this.allBookings = allBookings;
        this.orderManager = orderManager;
        this.addiotionalItemInfo = addiotionalItemInfo;
        this.bookingEngine = bookingEngine;
    }

    public Integer getSquareMetres(PmsBookingRooms room) {
        if(room.bookingItemId == null || room.bookingItemId.isEmpty()) {
            return getAverageSquareMetersForType(room.bookingItemTypeId);
        }
        
        PmsAdditionalItemInformation additional = addiotionalItemInfo.get(room.bookingItemId);
        if(additional != null) {
            return additional.squareMetres;
        }
        
        return 0;
    }
    
    PmsStatistics buildStatistics(PmsBookingFilter filter, Integer totalRooms, PmsInvoiceManager invoiceManager, List<Booking> allBookings) {
        PmsStatistics statics = new PmsStatistics();
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
        List<String> roomsAddedForGuests = new ArrayList();
        while(true) {
            invoiceManager.gsTiming("Generating statistcs: " + cal.getTime());
            StatisticsEntry entry = buildStatsEntry(cal);
            entry.date = cal.getTime();
            entry.totalRoomsOriginal = totalRooms;
            if(filter.removeClosedRooms) {
                entry.totalRooms = totalRooms - getClosedRoomsForDay(entry.date, allBookings);
            } else {
                entry.totalRooms = totalRooms;
            }
            entry.bugdet = 0.0;
            int month = cal.get(Calendar.MONTH);
            month++;
            if(budget.containsKey(month) && budget.get(month).coverage_percentage != null) {
                entry.bugdet = budget.get(month).coverage_percentage.doubleValue();
            }
            statics.addEntry(entry);
            long start = System.currentTimeMillis();
            int counter = 0;
            
            for(PmsBooking booking : bookings) {
                if(!booking.confirmed) {
                    continue;
                }
                if(booking.testReservation) {
                    continue;
                }
                if(booking.isDeleted) {
                    continue;
                }
                if(!booking.isActiveOnDay(cal.getTime())) {
                    continue;
                }
                if(!filter.segments.isEmpty()) {
                    if(!filter.segments.contains(booking.segmentId) && !filter.segments.contains("none") && !booking.segmentId.isEmpty()) {
                        continue;
                    }
                }
                counter++;

                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    // With the new accounting system as of 1th of january 2019, this makes no sense any more.
                    // All orders should be part of the statistic, regardless of its paid or not.
                    if(((!booking.payedFor && !invoiceManager.isRoomPaidFor(room.pmsBookingRoomId)) && !filter.includeVirtual) && room.isEnded() && room.beforeFirstOfJan2019()) {
                        if(room.isActiveOnDay(cal.getTime())) {
                            entry.roomsNotIncluded++;
                        }
                        continue;
                    }
                    if(filter.itemFilter != null && !filter.itemFilter.isEmpty()) {
                        if(room.bookingItemId == null || room.bookingItemId.isEmpty() || !filter.itemFilter.contains(room.bookingItemId)) {
                            continue;
                        }
                    }
                    if(!filter.typeFilter.isEmpty() && !filter.typeFilter.contains(room.bookingItemTypeId)) {
                        continue;
                    }
                    if(room.isActiveOnDay(cal.getTime())) {
                        entry.roomsIncluded.add(room.pmsBookingRoomId);
                        Double price = room.getDailyPrice(booking.priceType, cal);
                        if(filter.removeAddonsIncludedInRoomPrice) {
                            price = room.getPriceForDayWithoutAddonsIncluded(booking.priceType, cal);
                        } else {
                            price = room.getPriceForDay(booking.priceType, cal);
                        }


                        if(!pricesExTax) {
                            price /= 1 + (room.taxes/100);
                        }
                        entry.roomsPrice.put(room.pmsBookingRoomId, price); 
                        entry.roomsPriceForecasted.put(room.pmsBookingRoomId, price);
                        entry.totalPrice += price;
                        entry.roomsRentedOut++;
                        if(room.checkingInAtDay(cal.getTime())) {
                            entry.arrivals++;
                        }
                        addGuests(entry, room, booking);
                        if(!roomsAddedForGuests.contains(room.pmsBookingRoomId)) {
                            addUniqueGuests(entry, room, booking);
                            roomsAddedForGuests.add(room.pmsBookingRoomId);
                        }
                    }
                    if(room.isEndingToday(cal.getTime())) {
                        entry.departures++;
                    }
                }
                 for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(!filter.typeFilter.isEmpty() && !filter.typeFilter.contains(room.bookingItemTypeId)) {
                        continue;
                    }
                    if(room.isActiveOnDay(cal.getTime()) || room.isEndingToday(cal.getTime())) {
                        for(PmsBookingAddonItem addon : room.addons) {
                            if(addon != null && addon.addonType != null && addon.addonType.equals(PmsBookingAddonItem.AddonTypes.BREAKFAST)) {
                                Calendar cal2 = Calendar.getInstance();
                                cal2.setTime(addon.date);
                                cal2.add(Calendar.DAY_OF_YEAR, 1);
                                if(!room.isSameDay(cal2.getTime(), cal.getTime())) {
                                    continue;
                                }
                            } else {
                                if(!room.isSameDay(addon.date, cal.getTime())) {
                                    continue;
                                }
                            }
                            Integer count = entry.addonsCount.get(addon.addonType);
                            Double addonPrice = entry.addonsPrice.get(addon.addonType);
                            if(count == null) { count = 0; }
                            if(addonPrice == null) { addonPrice = 0.0; }
                            count += addon.count;
                            addonPrice += (addon.price*addon.count);
    //                            addonPriceEx += addon.priceExTaxes;

                            if (addon.addonType != null) {
                                entry.addonsCount.put(addon.addonType, count);
                                entry.addonsPrice.put(addon.addonType, addonPrice);
                            }
                        }
                    }
                 }
            }
            
            entry.finalize();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.endDate)) {
                break;
            }
//            if(true) {
//                break;
//            }
        }
        
        return statics;
    }

    private static boolean isPaidFor(PmsBooking booking, Calendar cal) {
        return !booking.payedFor && cal.getTime().before(new Date()) && cal.get(Calendar.YEAR) >= 2017;
    }
    
    public LinkedList<SalesStatisticsEntry> buildOrderStatistics(PmsBookingFilter filter, OrderManager orderManager) {
        LinkedList<SalesStatisticsEntry> result = new LinkedList();
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
                    
        List<Order> ordersToUse = new ArrayList();

        if (filter.includeVirtual) {
            ordersToUse = orderManager.getAllOrderIncludedVirtual();
        } else {
            ordersToUse = orderManager.getOrders(null, null, null);
        }
        
        while(true) {
            SalesStatisticsEntry entry = new SalesStatisticsEntry();
            entry.date = cal.getTime();
            result.add(entry);
            


            for(Order order : ordersToUse) {
                if(cal.getTime() == null || order == null) {
                    continue;
                }
                if(order.status == Order.Status.CANCELED) {
                    continue;
                }
                if(order.testOrder) {
                    continue;
                }
                if(order.cart == null) {
                    continue;
                }
                if(order.createdOnDay(cal.getTime())) {
                    Double total = orderManager.getTotalAmountExTaxes(order);
                    entry.totalPrice += total;
                    entry.numberOfOrders++;
                    entry.date = cal.getTime();
                    for(CartItem item : order.cart.getItems()) {
                        entry.nights += item.getCount();
                    }
                    entry.addPayment(order.payment.paymentType, total);
                }
            }
            
            entry.bookingValue = addBookingValues(cal);
            
            entry.finalize();
            
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.endDate)) {
                break;
            }
        }
        
        return result;
    }

    private StatisticsEntry buildStatsEntry(Calendar cal) {
        StatisticsEntry entry = new StatisticsEntry();
        
        return entry;
    }

    void setBudget(HashMap<Integer, PmsBudget> budget) {
        this.budget = budget;
    }

    private void addGuests(StatisticsEntry entry, PmsBookingRooms room, PmsBooking booking) {
        
        String countryCode = booking.countryCode;
        if(countryCode == null || countryCode.isEmpty()) {
            HashMap<Integer, String> list = PhoneCountryCodeList.getList();
            try {
                Integer prefix = new Integer(room.guests.get(0).prefix);
                if(list.containsKey(prefix)) {
                    countryCode = list.get(prefix);
                }
            }catch(Exception e) {
                countryCode = "NO";
            }
        }
        User user = userManager.getUserById(booking.userId);
        
        if(user == null) {
            user = new User();
//            return;
        }
        int totalGuests = 0;
        if(entry.guests.containsKey(countryCode)) {
            totalGuests = entry.guests.get(countryCode);
        }
        totalGuests += room.numberOfGuests;
        entry.guests.put(countryCode, totalGuests);
        entry.guestCount += room.numberOfGuests;

        if(booking.isConference) {
            int confGuests = 0;
            if(entry.guestsConference.containsKey(countryCode)) {
                confGuests = entry.guestsConference.get(countryCode);
            }
            confGuests += room.numberOfGuests;
            entry.guestsConference.put(countryCode, confGuests);
        } else if(user.company != null && !user.company.isEmpty()) {
            int confCompany = 0;
            if(entry.guestsCompany.containsKey(countryCode)) {
                confCompany = entry.guestsCompany.get(countryCode);
            }
            confCompany += room.numberOfGuests;
            entry.guestsCompany.put(countryCode, confCompany);
        } else {
            int regularGuests = 0;
            if(entry.guestsRegular.containsKey(countryCode)) {
                regularGuests = entry.guestsRegular.get(countryCode);
            }
            regularGuests += room.numberOfGuests;
            entry.guestsRegular.put(countryCode, regularGuests);
        }
    }

    private void addUniqueGuests(StatisticsEntry entry, PmsBookingRooms room, PmsBooking booking) {
            String countryCode = booking.countryCode;
            if(countryCode == null || countryCode.isEmpty()) {
                try {
                    HashMap<Integer, String> list = PhoneCountryCodeList.getList();
                    Integer prefix = new Integer(room.guests.get(0).prefix);
                    if(list.containsKey(prefix)) {
                        countryCode = list.get(prefix);
                    }
                }catch(Exception e) {
                    countryCode = "NO";
                }
            }

            if(countryCode != null && countryCode.equals("")) {
                countryCode = "NO";
            }
            
            int regularGuests = 0;
            if(entry.uniqueGuests.containsKey(countryCode)) {
                regularGuests = entry.uniqueGuests.get(countryCode);
            }
            regularGuests += room.numberOfGuests;
            entry.uniqueGuests.put(countryCode, regularGuests);
    }

    private Double addBookingValues(Calendar cal) {
        Double res = 0.0;
        for(PmsBooking booking : allBookings) {
            if(!booking.isCompletedBooking()) {
                continue;
            }
            if(booking.isDeleted) {
                continue;
            }
            if(booking.createdOnDay(cal.getTime())) {
                booking.calculateTotalCost();
                res += booking.getTotalPrice();
            }
        }
        return res;
    }

    private Integer getAverageSquareMetersForType(String bookingItemTypeId) {
        if(cachedSquareMetersForType.containsKey(bookingItemTypeId)) {
            return cachedSquareMetersForType.get(bookingItemTypeId);
        }
        double meters = 0.0;
        int count = 0;
        for(BookingItem item : bookingEngine.getBookingItemsByType(bookingItemTypeId)) {
            count++;
            PmsAdditionalItemInformation additional = addiotionalItemInfo.get(item.id);
            if(additional != null) {
                meters += additional.squareMetres;
            }
        }
        int result;
        result = (int)(meters / count);
        cachedSquareMetersForType.put(bookingItemTypeId, result);
        return result;
    }

    private Integer getClosedRoomsForDay(Date date, List<Booking> allBookingsInEngine) {
        int count = 0;
        for(Booking booking : allBookingsInEngine) {
            if(!booking.source.toLowerCase().contains("closed")) {
                continue;
            }
            if(booking.startDate.before(date) && booking.endDate.after(date)) {
                System.out.println(booking.source + " : " + date + " : " + booking.startDate + " - " + booking.endDate);
                count++;
            }
        }
        
        return count;
    }
    
}
