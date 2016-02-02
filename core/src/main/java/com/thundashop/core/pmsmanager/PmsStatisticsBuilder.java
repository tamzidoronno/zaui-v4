package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
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
                for(PmsBookingRooms room : booking.rooms) {
                    if(room.isActiveOnDay(cal.getTime())) {
                        Double price = room.getDailyPrice(booking.priceType, cal);
                        if(pricesExTax) {
                            price /= 1 - (room.taxes/100);
                        }
                        
                        entry.totalPrice += price;
                        entry.roomsRentedOut++;
                    }
                }
            }
            
            entry.finalize();
            System.out.println("------------");
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.endDate)) {
                break;
            }
        }
        
        statics.buildTotal();
        
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

            for(PmsBooking booking : bookings) {
                for(String orderId : booking.orderIds) {
                    Order order = orderManager.getOrderSecure(orderId);
                    if(cal == null || cal.getTime() == null || order == null) {
                        continue;
                    }
                    if(order.createdOnDay(cal.getTime())) {
                        Double total = orderManager.getTotalAmount(order);
                        entry.totalPrice += total;
                        entry.numberOfOrders++;
                        entry.date = cal.getTime();
                        entry.addPayment(order.payment.paymentType, total);
                    }
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
