/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.storemanager.StoreManager;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class PmsCoverageReportManager extends ManagerBase {

    HashMap<String, PmsCoverageReportEntry> entries = new HashMap();

    private Calendar cal = Calendar.getInstance();

    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    BookingEngine engine;
    
    @Autowired
    StoreManager storeManager;

    PmsCoverageReport getCoverageReport(PmsBookingFilter filter) {
        entries.clear();

        LinkedList<PmsCoverageReportEntry> result = generateReportEntries(filter);

        Collections.sort(result, new Comparator<PmsCoverageReportEntry>() {
            public int compare(PmsCoverageReportEntry o1, PmsCoverageReportEntry o2) {
                return o1.date.compareTo(o2.date);
            }
        });

        Integer totalRooms = getTotalRooms(filter);
        calculateAccumulatedResult(result, totalRooms);
        
        PmsCoverageReport report = new PmsCoverageReport();
        
        for(PmsCoverageReportEntry check : result) {
            if((check.date.after(filter.startDate) || check.date.equals(filter.startDate)) && check.date.before(filter.endDate)) {
                report.entries.add(check);
            }
        }
        
        return report;
    }

    private LinkedList<PmsCoverageReportEntry> generateReportEntries(PmsBookingFilter filter) {
        List<PmsBooking> bookings = pmsManager.getAllBookingsFlat();
        for (PmsBooking booking : bookings) {
            PmsCoverageReportEntry report = getCoverageReportEntry(booking.rowCreatedDate);
            
            for(PmsBookingRooms r : booking.getActiveRooms()) {
                if(avoidRoomByType(r.bookingItemTypeId, filter)) {
                    continue;
                }
                report.roomsSold++;
            }
            

            if(booking.getActiveRooms().size() > 2) {
                for(PmsBookingRooms r : booking.getActiveRooms()) {
                    if(avoidRoomByType(r.bookingItemTypeId, filter)) {
                        continue;
                    }
                    report.groupRoomsSold++;
                }
            }
            
            for (PmsBookingRooms r : booking.getActiveRooms()) {
                if(avoidRoomByType(r.bookingItemTypeId, filter)) {
                    continue;
                }
                report = getCoverageReportEntry(r.date.start);
                report.roomsArriving++;
                report.guestsArriving += r.numberOfGuests;
                if(booking.getActiveRooms().size() > 2) {
                    report.groupRoomsArriving++;
                }
                
                
                report = getCoverageReportEntry(r.date.end);
                report.guestsDeparting += r.numberOfGuests;
                report.roomsDeparting++;
                if(booking.getActiveRooms().size() > 2) {
                    report.groupRoomsDeparting++;
                }
                
                for (String k : r.priceMatrix.keySet()) {
                    report = getCoverageReportEntry(k);
                    Double price = r.priceMatrix.get(k);
                    for(PmsBookingAddonItem addon : r.addons) {
                        if(addon.isIncludedInRoomPrice && PmsBookingRooms.isSameDayStatic(report.date, addon.date)) {
                            price -= (addon.count * addon.price);
                        }
                    }
                    price = price / (1+(r.taxes/100));
                    report.totalPrice += price;
                }
            }
        }

        fillInBlankSpots(filter);

        return new LinkedList(entries.values());
    }

    private PmsCoverageReportEntry getCoverageReportEntry(Date date) {
        String key = createCalKey(date);
        if (entries.containsKey(key)) {
            return entries.get(key);
        }

        
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        PmsCoverageReportEntry toAdd = new PmsCoverageReportEntry();
        toAdd.date = cal.getTime();
        entries.put(key, toAdd);
        return toAdd;
    }

    private String createCalKey(Date date) {
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month++;

        String offset = "";
        if (day < 10) {
            offset += "0" + day + "-";
        } else {
            offset += day + "-";
        }

        if (month < 10) {
            offset += "0" + month + "-";
        } else {
            offset += month + "-";
        }

        return offset + cal.get(Calendar.YEAR);
    }

    private PmsCoverageReportEntry getCoverageReportEntry(String key) {
        if (entries.containsKey(key)) {
            return entries.get(key);
        }

        PmsCoverageReportEntry toAdd = new PmsCoverageReportEntry();
        
        cal.setTime(PmsBookingRooms.convertOffsetToDate(key));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        toAdd.date = cal.getTime();
        entries.put(key, toAdd);
        return toAdd;
    }

    private void fillInBlankSpots(PmsBookingFilter filter) {
        Date start = new Date();
        Date end = new Date();
        for (PmsCoverageReportEntry entry : entries.values()) {
            if (start.after(entry.date)) {
                start = entry.date;
            }
            if (end.before(entry.date)) {
                end = entry.date;
            }
        }
        
        if(filter.endDate.after(end)) {
            end = filter.endDate;
        }
        
        if(filter.startDate.before(start)) {
            start = filter.startDate;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        while (true) {
            Date toCheck = cal.getTime();
            getCoverageReportEntry(toCheck);
            cal.add(Calendar.DAY_OF_YEAR, 1);
            if (cal.getTime().after(end)) {
                break;
            }
        }

    }

    private void calculateAccumulatedResult(List<PmsCoverageReportEntry> result, int totalRooms) {
        int roomsTaken = 0;
        int groupRoomsTaken = 0;
        int guestsStaying = 0;
        for(PmsCoverageReportEntry entry : result) {
            roomsTaken += entry.roomsArriving - entry.roomsDeparting;
            entry.roomsTaken = roomsTaken;
            
            groupRoomsTaken += entry.groupRoomsArriving - entry.groupRoomsDeparting;
            entry.groupRoomsTaken = groupRoomsTaken;
            
            guestsStaying += entry.guestsArriving - entry.guestsDeparting;
            entry.guestsStaying = guestsStaying;
            
            entry.roomsAvailable = totalRooms - entry.roomsTaken;
            
            if(entry.roomsTaken > 0) {
                entry.avgPrice = entry.totalPrice / entry.roomsTaken;
            }
            
            entry.occupancy = ((double)entry.roomsTaken / (double)totalRooms)*100;
        }
    }

    private Integer getTotalRooms(PmsBookingFilter filter) {
        List<BookingItemType> types = engine.getBookingItemTypes();
        int size = 0;
        for(BookingItemType type : types) {
            if(!type.visibleForBooking && !filter.includeNonBookable && avoidRoomByType(type.id, filter)) {
                continue;
            }
            if(avoidRoomByType(type.id, filter)) {
                continue;
            }
            
            size += engine.getBookingItemsByType(type.id).size();
        }
        
        return size;
    }

    private boolean avoidRoomByType(String typeId, PmsBookingFilter filter) {
        if(filter.typeFilter != null && !filter.typeFilter.isEmpty() && !filter.typeFilter.contains(typeId)) {
            return true;
        }
        return false;
    }
}