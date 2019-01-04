/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.PermenantlyDeleteData;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
@PermenantlyDeleteData
public class Route extends DataCommon {

    public String name = "Routename";
    public int seq = 0;
    
    public String shortDescription = "";
    
    public String instruction = "";
    
    public boolean instructionAccepted = false;

    public StartInfo startInfo = new StartInfo();
    
    public StartInfo completedInfo = new StartInfo();
    
    public List<String> destinationIds = new ArrayList();
    
    public List<DriverRouteLog> driverLogs = new ArrayList();
    
    public List<String> userIds = new ArrayList();
    
    public String deliveryTime = "";
    
    @Transient
    private List<Destination> destinations = new ArrayList();
    
    public Date deliveryServiceDate;
    
    public String originalId;

    public String depotId = "";
    
    public boolean dirty = false;
    
    /**
     * This flag is used for exporting data that is not connected to any route.
     */
    public boolean isVritual = false;
    
    public Route() {
        shortDescription = "A route description";
    }

    public boolean hasDestinationId(String destinationId) {
        return destinationIds.contains(destinationId);
    }
    
    public void addDestination(Destination dest) {
        if (dest == null) {
            return;
        }
        
        if (!destinationIds.contains(dest.id)) {
            throw new ErrorException(1000016);
        }
        
        destinations.removeIf(o -> o.id.equals(dest.id));
        destinations.add(dest);
    }


    public List<Destination> getDestinations() {
        return destinations;
    }

    public void makeSureUserIdsNotDuplicated() {
        
        // add elements to al, including duplicates
        Set<String> hs = new HashSet<>();
        hs.addAll(userIds);
        userIds.clear();
        userIds.addAll(hs);
    }

    public void setPodBarcodeStringToTasks() {
        destinations.stream().forEach(dest -> dest.setPodBarcodeStringToTasks());
    }

    public boolean removeDestination(String destinationId) {
        return destinationIds.removeIf(key -> key.equals(destinationId));
    }

    public void clearDestinations() {
        destinations.clear();;
    }

    public boolean shouldBeDeletedDueToOverdue() {
        if (completedInfo.completedTimeStamp == null)
            return false;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(completedInfo.completedTimeStamp);
        cal.add(Calendar.HOUR, 180);
        Date dateToPass = cal.getTime();
        
        Date now = new Date();
        
        if (now.after(dateToPass))
            return true;
        
        return false;
    }

    void sortDestinations() {
        destinations = destinations.stream().sorted((o1, o2) -> {
            return o1.seq.compareTo(o2.seq);
        }).collect(Collectors.toList());
    }
    
    static Comparator<? super Route> getSortedById() {
        return (Route o1, Route o2) -> {
            try {
                Integer o1Id = Integer.parseInt(o1.originalId);
                Integer o2Id = Integer.parseInt(o2.originalId);
                return o2Id.compareTo(o1Id);
            } catch (Exception ex) {
            }
            
            try {
                Integer o1Id = Integer.parseInt(o1.id.split(" ")[0]);
                Integer o2Id = Integer.parseInt(o2.id.split(" ")[0]);
                return o2Id.compareTo(o1Id);
            } catch (Exception ex) {
            }
            
            
            return o1.id.compareTo(o2.id);
        };
    }
    
    static Comparator<? super Route> getSortedByDeliveryDate() {
        
        return (Route o1, Route o2) -> {
            if (o1.sameDeliveryDay(o2)) {
                return o2.id.compareTo(o1.id);
            }
            
            return Comparator.nullsLast(Date::compareTo).compare(o1.deliveryServiceDate, o2.deliveryServiceDate);
        };
    }

    public boolean serviceDateToDayOrInPast() {
        Date today = new Date();
        
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(today);
        cal2.setTime(deliveryServiceDate);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                          cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        if (sameDay)
            return true;
        
        if (today.after(deliveryServiceDate))
            return true;
        
        return false;
    }

    void addLogEntryForDriver(String userId, String addedByUserId, Date date, boolean added) {
        DriverRouteLog log = new DriverRouteLog();
        log.added = added;
        log.addedByUserId = addedByUserId;
        log.userId = userId;
        log.date = date;
        driverLogs.add(log);
    }

    private boolean sameDeliveryDay(Route o2) {
        if (o2.deliveryServiceDate == null || deliveryServiceDate == null) {
            return false;
        }
        
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(deliveryServiceDate);
        cal2.setTime(o2.deliveryServiceDate);
        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                          cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        
        return sameDay;
    }
}
