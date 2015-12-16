package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.BookingEngine;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author boggi2
 */
class PmsStatisticsBuilder {
    private final List<PmsBooking> bookings;

    PmsStatisticsBuilder(List<PmsBooking> allBookings) {
        this.bookings = allBookings;
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
            cal.add(Calendar.DAY_OF_YEAR, 1);
            
            for(PmsBooking booking : bookings) {
                if(!booking.confirmed) {
                    continue;
                }
                for(PmsBookingRooms room : booking.rooms) {
                    if(room.isActiveOnDay(cal.getTime())) {
                        entry.totalPrice += room.getDailyPrice(booking.priceType, cal);
                        entry.roomsRentedOut++;
                    }
                }
            }
            
            entry.finalize();
            
            if(cal.getTime().after(filter.endDate)) {
                break;
            }
        }
        
        statics.buildTotal();
        
        return statics;
    }

    private StatisticsEntry buildStatsEntry(Calendar cal) {
        StatisticsEntry entry = new StatisticsEntry();
        
        return entry;
    }
    
}
