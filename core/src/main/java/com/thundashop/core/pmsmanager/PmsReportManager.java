package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ManagerBase;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.joda.time.DateTime;
import org.joda.time.Days;
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
    public List<PmsMobileReport> getReport(Date start, Date end, String compareTo) {
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        start = cal.getTime();
        
        cal.setTime(end);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        end = cal.getTime();
        
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.startDate = start;
        filter.endDate = end;
        
        PmsBookingFilter savedFilter = pmsManager.getPmsBookingFilter("coverage");
        if(savedFilter != null) {
            savedFilter.startDate = filter.startDate;
            savedFilter.endDate = filter.endDate;
            filter = savedFilter;
        }
        
        PmsBookingFilter filterCompare = new PmsBookingFilter();
        filterCompare.startDate = new Date(start.getTime());
        filterCompare.endDate = new Date(end.getTime());
        
        Calendar calStart = Calendar.getInstance();
        Calendar calEnd = Calendar.getInstance();
        calStart.setTime(filterCompare.startDate);
        calEnd.setTime(filterCompare.endDate);

        if(compareTo.equals("-1week")) {
            int days = Days.daysBetween(new DateTime(filterCompare.startDate), new DateTime(filterCompare.endDate)).getDays();

            
            calStart.add(Calendar.DAY_OF_MONTH, days*-1);
            calEnd.add(Calendar.DAY_OF_MONTH, days*-1);
        }
        if(compareTo.equals("-1month")) {
            calStart.add(Calendar.MONTH, -1);
            calEnd.add(Calendar.MONTH, -1);
        }
        if(compareTo.equals("-1year")) {
            calStart.add(Calendar.YEAR, -1);
            calEnd.add(Calendar.YEAR, -1);
        }
        filterCompare.startDate = calStart.getTime();
        filterCompare.endDate = calEnd.getTime();

        List<PmsMobileReport> result = new ArrayList();
        
        PmsStatistics stats = pmsManager.getStatistics(filter);
        PmsStatistics compare = pmsManager.getStatistics(filterCompare);
        result.add(buildTotal(stats, "All", compare.getTotal()));
        
        List<BookingItemType> types = bookingEngine.getBookingItemTypes();
        for(BookingItemType type : types) {
            filter.typeFilter.clear();
            filter.typeFilter.add(type.id);
            stats = pmsManager.getStatistics(filter);
            
            filterCompare.typeFilter.clear();
            filterCompare.typeFilter.add(type.id);
            compare = pmsManager.getStatistics(filterCompare);
            
            result.add(buildTotal(stats, type.name, compare.getTotal()));
        }
        
        return result;
    }

    @Override
    public List<PmsMobileRoomCoverage> getRoomCoverage(Date start, Date end) {
        List<BookingItem> items = bookingEngine.getBookingItems();
        
        PmsBookingFilter filter = new PmsBookingFilter();
        filter.startDate = start;
        filter.endDate = end;
        filter.itemFilter = new ArrayList();
        
        List<PmsMobileRoomCoverage> result = new ArrayList();
        for(BookingItem item : items) {
            filter.itemFilter.clear();
            filter.itemFilter.add(item.id);
            
            PmsStatistics stats = pmsManager.getStatistics(filter);
            StatisticsEntry total = stats.getTotal();
            
            PmsMobileRoomCoverage coverage = new PmsMobileRoomCoverage();
            coverage.roomName = item.bookingItemName;
            coverage.coverage = total.coverage;
            coverage.avgPrice = (double) Math.round(total.avgPrice);
            
            coverage.revpar = (double) Math.round(coverage.avgPrice * (double)((double)coverage.coverage / 100));
            
            int squareMeters = 0;
            PmsAdditionalItemInformation additional = pmsManager.getAdditionalInfo(item.id);
            if(additional != null) {
                squareMeters = additional.squareMetres;
            }
            if(squareMeters < 10) {
                squareMeters = 0;
            }
            if(squareMeters > 0) {
                coverage.sqaurePrice = (double) Math.round(coverage.avgPrice / squareMeters);
                coverage.squarePricePeriode = (double) Math.round((double)coverage.sqaurePrice * (double)stats.entries.size()-1);
            }
            
            result.add(coverage);
            
        }
        return result;
    }

    @Override
    public List<PmsMobileUsage> getUsage(Date start, Date end) {
        return new ArrayList();
    }

    private PmsMobileReport buildTotal(PmsStatistics stats, String totalText, StatisticsEntry compare) {
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

        total.revParDaily = (double)Math.round(total.totalRented / total.totalNumberOfRooms);
        total.revParLastPeriode = (double)Math.round(compare.totalPrice / total.totalNumberOfRooms);
        total.revPar = (double)Math.round(total.revParDaily * total.numberOfDays);
        
        total.squareMeters = totalEntry.squareMetres;
        
        if(total.squareMeters > 10) {
            total.squareMetresPrice =  (double)Math.round(total.totalRented / total.squareMeters);
            total.squareMetresPriceDaily =  (double)Math.round(total.squareMetresPrice / total.numberOfDays);
        }
        
        total.totalPastPeriode = compare.totalPrice;
        total.totalPastPeriodeDaily = (double)Math.round(compare.totalPrice / total.numberOfDays);
        
        total.increaseFromLastPeriode = total.totalRented - total.totalPastPeriode;
        total.increaseFromLastPeriodeDaily = (double)Math.round(total.increaseFromLastPeriode / total.numberOfDays);
        
        return total;
    }
    
}
