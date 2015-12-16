package com.thundashop.core.pmsmanager;

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

    PmsStatistics buildStatistics(PmsBookingFilter filter) {
        PmsStatistics statics = new PmsStatistics();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(filter.startDate);
        
        while(true) {
            StatisticsEntry entry = buildStatsEntry(cal);
            statics.addEntry(entry);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if(cal.getTime().after(filter.endDate)) {
                break;
            }
        }
        
        return statics;
    }

    private StatisticsEntry buildStatsEntry(Calendar cal) {
        StatisticsEntry entry = new StatisticsEntry();
        
        return entry;
    }
    
}
