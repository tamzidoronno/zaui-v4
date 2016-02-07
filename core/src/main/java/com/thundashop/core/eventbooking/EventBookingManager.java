package com.thundashop.core.eventbooking;


import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ktonder
 */

@Component
@GetShopSession
public class EventBookingManager extends GetShopSessionBeanNamed implements IEventBookingManager {
    public HashMap<String, Event> events = new HashMap();
    public HashMap<String, Location> locations = new HashMap();
    
    @Autowired
    public BookingEngine bookingEngine;

    @Override
    public void dataFromDatabase(DataRetreived datas) {
        for (DataCommon data : datas.data) {
            if (data instanceof Event) {
                Event event = (Event)data;
                events.put(event.id, event);
            }
            
            if (data instanceof Location) {
                Location loc = (Location)data;
                locations.put(loc.id, loc);
            }
        }
    }
    
    @Override
    public void createEvent(Event event) {
        log("Event created", event);
        BookingItem item = bookingEngine.saveBookingItem(event.bookingItem);
        event.bookingItemId = item.id;
        saveObject(event);
        events.put(event.id, event);
    }

    @Override
    public List<Event> getEvents() {
        return cloneAndFinalize(new ArrayList(events.values()));
    }

    private List<Event> cloneAndFinalize(List<Event> events) {
        List<Event> retEvents = new ArrayList();
        
        for (Event event : events) {
            retEvents.add(finalize(event));
        }
        
        return retEvents;
    }

    private Event finalize(Event event) {
        event.bookingItem = bookingEngine.getBookingItem(event.bookingItemId);
        
        if (event.bookingItem != null) {
            event.bookingItemType = bookingEngine.getBookingItemType(event.bookingItem.bookingItemTypeId);
        }
        
        event.location = getLocationBySubLocationId(event.subLocationId);
        event.subLocation = getSubLocation(event.subLocationId);
        
        event.setMainDates(); 
        return event;
    }
    
    private void log(String description, Object event) {
        // TODO, add logging.
    }

    @Override
    public void deleteEvent(String eventId) {
        Event event = events.get(eventId);
        if (event != null && event.bookingItemId != null && !event.bookingItemId.isEmpty()) {
            bookingEngine.deleteBookingItem(event.bookingItemId);
        }
        
        events.remove(eventId);
        deleteObject(event);
        
    }

    @Override
    public void saveLocation(Location location) {
        if (location.id == null || location.id.isEmpty())
            log("location created", location);
        else 
            log("location changed", location);
        
        saveObject(location);
        locations.put(location.id, location);
    }

    @Override
    public List<Location> getAllLocations() {
        return new ArrayList(locations.values());
    }

    @Override
    public Location getLocation(String locationId) {
        return locations.get(locationId);
    }

    @Override
    public void deleteLocation(String locationId) {
        // TODO - check if location is in use somewhere.
        Location location = locations.remove(locationId);
        if (location != null)
            deleteObject(location);
    }

    private Location getLocationBySubLocationId(String subLocationId) {
        for (Location loc : locations.values()) {
            for (SubLocation subLoc : loc.locations) {
                if (subLoc.id.equals(subLocationId)) {
                    return loc;
                }
            }
        }
        
        return null;
    }

    @Override
    public List<Event> getBookingsByPageId(String pageId, boolean showOnlyNew) {
        BookingItemType bookingItemType = bookingEngine.getBookingItemTypes()
                .stream()
                .filter(o -> o.pageId.equals(pageId))
                .findFirst()
                .orElse(null);
        
        if (bookingItemType == null) {
            return new ArrayList();
        }
        
        List<BookingItem> items = bookingEngine.getBookingItemsByType(bookingItemType.id);
        
        List<Event> retEvents = new ArrayList();
        for (BookingItem item : items) {
            retEvents.addAll(events.values().stream()
                .filter(event -> event.bookingItemId.equals(item.id))
                .filter(event -> showOnlyNew && isInFuture(event))
                .collect(Collectors.toList()));
        }
        
        return cloneAndFinalize(retEvents);
    }

    private boolean isInFuture(Event event) {
        boolean isInfure = false;
        for (Day day : event.days) {
            if (day.isInFuture()) {
                isInfure = true;
            }
        }
        
        return isInfure
;    }

    private SubLocation getSubLocation(String subLocationId) {
        for (Location loc : locations.values()) {
            for (SubLocation subLoc : loc.locations) {
                if (subLoc.id.equals(subLocationId)) {
                    return subLoc;
                }
            }
        }
        
        return null;
    }
    
}
