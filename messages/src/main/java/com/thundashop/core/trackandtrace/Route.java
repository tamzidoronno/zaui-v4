/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.trackandtrace;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import java.util.ArrayList;
import java.util.Calendar;
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
public class Route extends DataCommon {
    public String name = "Routename";
    public int seq = 0;
    
    public String shortDescription = "";
    
    public String instruction = "";
    
    public boolean instructionAccepted = false;

    public StartInfo startInfo = new StartInfo();
    
    public StartInfo completedInfo = new StartInfo();
    
    public List<String> destinationIds = new ArrayList();
    
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
        if (!completedInfo.completed)
            return false;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(completedInfo.completedTimeStamp);
        cal.add(Calendar.DAY_OF_WEEK, 2);
        Date dateToPass = cal.getTime();
        
        Date now = new Date();
        System.out.println("Now: " + now);
        
        if (now.after(dateToPass))
            return true;
        
        return false;
    }

    void sortDestinations() {
        destinations = destinations.stream().sorted((o1, o2) -> {
            return o1.seq.compareTo(o2.seq);
        }).collect(Collectors.toList());
    }

}
