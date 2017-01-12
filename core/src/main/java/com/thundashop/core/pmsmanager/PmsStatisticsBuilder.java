package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
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

/**
 *
 * @author boggi2
 */
class PmsStatisticsBuilder {
    private final List<PmsBooking> bookings;
    private final boolean pricesExTax;
    private HashMap<Integer, PmsBudget> budget;
    private final UserManager userManager;

    PmsStatisticsBuilder(List<PmsBooking> allBookings, boolean pricesExTax, UserManager userManager) {
        this.bookings = allBookings;
        this.userManager = userManager;
        this.pricesExTax = pricesExTax;
    }

    PmsStatistics buildStatistics(PmsBookingFilter filter, Integer totalRooms) {
        PmsStatistics statics = new PmsStatistics();
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
        List<String> roomsAddedForGuests = new ArrayList();
        while(true) {
            StatisticsEntry entry = buildStatsEntry(cal);
            entry.date = cal.getTime();
            entry.totalRooms = totalRooms;
            entry.bugdet = 0.0;
            int month = cal.get(Calendar.MONTH);
            month++;
            if(budget.containsKey(month) && budget.get(month).coverage_percentage != null) {
                entry.bugdet = budget.get(month).coverage_percentage.doubleValue();
            }
            statics.addEntry(entry);
            
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
                if(!booking.payedFor && cal.getTime().before(new Date()) && cal.get(Calendar.YEAR) >= 2017) {
                    continue;
                }
                
                if(booking.isEnded() && !booking.payedFor) {
                    continue;
                }
                
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(!filter.typeFilter.isEmpty() && !filter.typeFilter.contains(room.bookingItemTypeId)) {
                        continue;
                    }
                    if(room.isActiveOnDay(cal.getTime())) {
                        Double price = room.getDailyPrice(booking.priceType, cal);
                        if(!pricesExTax) {
                            price /= 1 + (room.taxes/100);
                        }

                        entry.totalPrice += price;
                        entry.roomsRentedOut++;
                        addGuests(entry, room, booking);
                        if(!roomsAddedForGuests.contains(room.pmsBookingRoomId)) {
                            addUniqueGuests(entry, room, booking);
                            roomsAddedForGuests.add(room.pmsBookingRoomId);
                        }
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

                            entry.addonsCount.put(addon.addonType, count);
                            entry.addonsPrice.put(addon.addonType, addonPrice);
                        }
                    }
                 }
            }
            
            entry.finalize();
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.endDate)) {
                break;
            }
        }
        
        return statics;
    }
    
    public LinkedList<SalesStatisticsEntry> buildOrderStatistics(PmsBookingFilter filter, OrderManager orderManager) {
        LinkedList<SalesStatisticsEntry> result = new LinkedList();
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
        while(true) {
            SalesStatisticsEntry entry = new SalesStatisticsEntry();
            entry.date = cal.getTime();
            result.add(entry);

            for(Order order : orderManager.getOrders(null, null, null)) {
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
            countryCode = "NO";
        }

        User user = userManager.getUserById(booking.userId);
        
        int totalGuests = 0;
        if(entry.guests.containsKey(countryCode)) {
            totalGuests = entry.guests.get(countryCode);
        }
        totalGuests += room.numberOfGuests;
        entry.guests.put(countryCode, totalGuests);
        
        if(booking.isConference) {
            int confGuests = 0;
            if(entry.guestsConference.containsKey(countryCode)) {
                confGuests = entry.guestsConference.get(countryCode);
            }
            confGuests += room.numberOfGuests;
            entry.guestsConference.put(countryCode, confGuests);
        } else if(!user.company.isEmpty()) {
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
                countryCode = "NO";
            }
            
            int regularGuests = 0;
            if(entry.uniqueGuests.containsKey(countryCode)) {
                regularGuests = entry.uniqueGuests.get(countryCode);
            }
            regularGuests += room.numberOfGuests;
            entry.uniqueGuests.put(countryCode, regularGuests);
    }
    
}
