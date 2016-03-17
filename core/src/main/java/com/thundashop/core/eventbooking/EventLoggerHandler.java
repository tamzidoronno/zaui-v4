/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class EventLoggerHandler {
    @Autowired
    public UserManager userManager;
    
    @Autowired
    public EventBookingManager eventBookingManager;
    
    public String compare(Event event1, Event event2) {
        String changes = "";
        if (!event1.getBookingItemId().equals(event2.getBookingItemId())) {
            changes += "Changed event type from : " + event1.getBookingItemId() + " to " + event2.getBookingItemId() + "\n";
        }
        
        if (!event1.getEventHelderUserId().equals(event2.getEventHelderUserId())) {
            changes += "Eventhelder changed from : " + userManager.getUserById(event1.getEventHelderUserId()).fullName + " to " + userManager.getUserById(event2.getEventHelderUserId()).fullName + "\n";
        }
        
        if (!event1.getSubLocationId().equals(event2.getSubLocationId())) {
            Location loc1 = eventBookingManager.getLocationBySubLocationId(event1.getSubLocationId());
            Location loc2 = eventBookingManager.getLocationBySubLocationId(event2.getSubLocationId());
            SubLocation subLoc1 = loc1.locations.stream().filter(sub -> sub.id.equals(event1.subLocationId)).findFirst().orElse(null);
            SubLocation subLoc2 = loc2.locations.stream().filter(sub -> sub.id.equals(event2.subLocationId)).findFirst().orElse(null);
            
            if (loc1 != null && loc2 != null) {
                changes += "Location changed from: " + loc1.name + " - " + subLoc1.name + " to " + loc2.name + " - " + subLoc2.name + "\n";    
            }
        }
        
        if (event1.bookingItem != null && event1.bookingItem != null && event1.bookingItem.bookingSize != event2.bookingItem.bookingSize) {
            changes += "Changed available spots from : " + event1.bookingItem.bookingSize + " to " + event2.bookingItem.bookingSize + "\n";
        }
        
        if (!event1.sameDates(event2)) {
            changes += "Days changed from: \n";
            int i = 0;
            for (Day day : event1.days) {
                changes += "&nbsp;&nbsp;&nbsp;" + day.startDate +  " - " + day.endDate + "\n";
            }
            
            changes += "To: \n";
            i = 0;
            for (Day day : event2.days) {
                changes += "&nbsp;&nbsp;&nbsp;" + day.startDate +  " - " + day.endDate + "\n";
            }
        }
        
        return changes;
    }

    public String compare(Object event, User user, boolean added) {
        String toReturn = "User removed from event";
        if (added) {
            toReturn = "User added to event";
        }
        
        toReturn += "\n &nbsp;&nbsp;&nbsp; Name: " + user.fullName + "\n &nbsp;&nbsp;&nbsp; Email: " + user.emailAddress;
        
        if (user.companyObject != null) {
            toReturn += "\n &nbsp;&nbsp;&nbsp; Company: " + user.companyObject.name + ", " + user.companyObject.vatNumber;
        }
        
        return toReturn;
    }
    
    public String compareWaiting(Object event, User user, boolean added) {
        String toReturn = "User removed from event waitinglist";
        if (added) {
            toReturn = "User added to event waitinglist";
        }
        
        toReturn += "\n &nbsp;&nbsp;&nbsp; Name: " + user.fullName + "\n &nbsp;&nbsp;&nbsp; Email: " + user.emailAddress;
        
        if (user.companyObject != null) {
            toReturn += "\n &nbsp;&nbsp;&nbsp; Company: " + user.companyObject.name + ", " + user.companyObject.vatNumber;
        }
        
        return toReturn;
    }
}
