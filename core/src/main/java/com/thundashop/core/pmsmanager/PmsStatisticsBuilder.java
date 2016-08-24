package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.ordermanager.OrderManager;
import com.thundashop.core.ordermanager.data.Order;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author boggi2
 */
class PmsStatisticsBuilder {
    private final List<PmsBooking> bookings;
    private final boolean pricesExTax;

    PmsStatisticsBuilder(List<PmsBooking> allBookings, boolean pricesExTax) {
        this.bookings = allBookings;
        this.pricesExTax = pricesExTax;
    }

    PmsStatistics buildStatistics(PmsBookingFilter filter, Integer totalRooms) {
        PmsStatistics statics = new PmsStatistics();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
        while(true) {
            StatisticsEntry entry = buildStatsEntry(cal);
            entry.date = cal.getTime();
            entry.totalRooms = totalRooms;
            statics.addEntry(entry);
            for(PmsBooking booking : bookings) {
                if(!booking.confirmed) {
                    continue;
                }
                if(booking.testReservation) {
                    continue;
                }
                for(PmsBookingRooms room : booking.getActiveRooms()) {
                    if(room.isActiveOnDay(cal.getTime())) {
                        Double price = room.getDailyPrice(booking.priceType, cal);
                        if(!pricesExTax) {
                            price /= 1 + (room.taxes/100);
                        }
                        if(booking.payedFor) {
                            entry.totalPrice += price;
                        }
                        entry.roomsRentedOut++;
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
                if(order.createdOnDay(cal.getTime())) {
                    Double total = orderManager.getTotalAmountExTaxes(order);
                    GetShopLogHandler.logPrintStatic(order.incrementOrderId + " - " + total, null);
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
    
}
