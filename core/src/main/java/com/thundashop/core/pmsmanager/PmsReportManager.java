package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ManagerBase;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsReportManager extends ManagerBase implements IPmsReportManager {

    @Autowired
    PmsManager pmsManager;

    @Autowired
    BookingEngine bookingEngine;
    
    @Override
    public List<PmsMobileReport> getReport(Date start, Date end) {
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.startDate = start;
        filter.endDate = end;

        List<PmsMobileReport> result = new ArrayList();
        
        PmsStatistics stats = pmsManager.getStatistics(filter);
        result.add(buildTotal(stats, "All"));
        
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : types) {
            filter.typeFilter.clear();
            filter.typeFilter.add(type.id);
            stats = pmsManager.getStatistics(filter);
            result.add(buildTotal(stats, type.name));
        }
        
        
        
        return result;
    }

    @Override
    public List<PmsMobileRoomCoverage> getRoomCoverage(Date start, Date end) {
        return new ArrayList();
    }

    @Override
    public List<PmsMobileUsage> getUsage(Date start, Date end) {
        return new ArrayList();
    }

    private PmsMobileReport buildTotal(PmsStatistics stats, String totalText) {
        PmsMobileReport total = new PmsMobileReport();
        StatisticsEntry totalEntry = stats.getTotal();
        
        total.totalNumberOfRooms = totalEntry.totalRooms;
        total.numberOfRooms = totalEntry.roomsRentedOut;
        total.avgCoverage = new Double(totalEntry.coverage);
        total.numberOfDays = stats.entries.size()-1;
        total.typeName = totalText;
        
        total.totalRented = totalEntry.totalPrice;
        total.totalRentedDaily = (double)Math.round(total.totalRented / total.numberOfDays);
        
        total.avgPrice = (double)Math.round(total.totalRented / total.numberOfRooms);

        total.revParDaily = (double)Math.round(total.avgPrice * (total.avgCoverage / 100));
        total.revPar = (double)Math.round(total.revParDaily * total.numberOfDays);
        
        total.squareMeters = totalEntry.squareMetres;
        
        if(total.squareMeters > 10) {
            total.squareMetresPrice =  (double)Math.round(total.totalRented / total.squareMeters);
            total.squareMetresPriceDaily =  (double)Math.round(total.squareMetresPrice / total.numberOfDays);
        }
        
        return total;
    }
    
}
