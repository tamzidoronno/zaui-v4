package com.thundashop.core.eventbooking;


import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.mongodb.BasicDBObject;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
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
    public EventLoggerHandler eventLoggerHandler;
    
    @Autowired
    public BookingEngine bookingEngine;
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    public Database database;

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
        BookingItem item = bookingEngine.saveBookingItem(event.bookingItem);
        event.bookingItemId = item.id;
        saveObject(event);
        events.put(event.id, event);
        log("EVENT_CREATED", event, null);
    }

    @Override
    public List<Event> getEvents() {
        List<Event> retEvents = new ArrayList(events.values());
        Collections.sort(retEvents, (Event o1, Event o2) -> o1.days.get(0).startDate.compareTo(o2.days.get(0).startDate));
        return cloneAndFinalize(retEvents);
    }

    private List<Event> cloneAndFinalize(List<Event> events) {
        List<Event> retEvents = new ArrayList();
        
        List<String> locationFilters = getLocationFilters();
        if (!locationFilters.isEmpty()) {
            events = filterList(events, locationFilters);
        }
        
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
        if (event.bookingItem != null) {
            event.eventPage = "?page="+event.bookingItem.pageId+"&eventId="+event.id;
        }
        
        if (event.markedAsReady || !isInFuture(event) || event.bookingItem == null || event.bookingItem.isFull || event.bookingItem.freeSpots < 1) {
            event.canBook = false;
        } else {
            event.canBook = true;
        }
        
        return event;
    }
    
    private void log(String action, Event event, Object additional) {
        EventLog logEntry = new EventLog();
        logEntry.action = action;
        
        if (event != null) {
            logEntry.eventId = event.id;
        }
        
        if (getSession().currentUser != null) {
            logEntry.doneBy = getSession().currentUser.id;
        }
        
        if (action.equals("EVENT_UPDATED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (Event)additional);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_REMOVED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (User)additional, false);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_ADDED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (User)additional, true);
            saveObject(logEntry);
        }
        
        if (action.equals("MARK_AS_READY")) {
            logEntry.comment = "Event is marked as ready";
            saveObject(logEntry);
        }
        
        if (action.equals("EVENT_CREATED")) {
            logEntry.comment = "Event created";
            saveObject(logEntry);
        }   
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
            log("LOCATION_CREATED", null, location);
        else 
            log("LOCATION_CHANGED", null, location);
        
        saveObject(location);
        locations.put(location.id, location);
    }

    @Override
    public List<Location> getAllLocations() {
        for (Location loc : locations.values()) {
            finalize(loc);
        }
        
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

    public Location getLocationBySubLocationId(String subLocationId) {
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
        
        return isInfure;    
    }

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

    @Override
    public Event getEvent(String eventId) {
        Event event = events.get(eventId);
        if (event != null) {
            return finalize(event);
        }
        
        return null;
    }

    @Override
    public void saveEvent(Event event) {
        Event inMemory = getEvent(event.id);
        event.bookingItem.id = inMemory.bookingItemId;
        BookingItem item = bookingEngine.saveBookingItem(event.bookingItem);

        event.bookingItem = item;
        event.bookingItemId = item.id;
        
        events.put(event.id, event);
        saveObject(event);
        
        log("EVENT_UPDATED", inMemory, event);
    }

    @Override
    public void bookCurrentUserToEvent(String eventId) {
        Event event = getEvent(eventId);
        
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        AddUserToEvent(event, getSession().currentUser);
    }   

    private void AddUserToEvent(Event event, User user) {
        List<Booking> alreadyBooked = bookingEngine.getAllBookingsByBookingItem(event.bookingItem.id);
        boolean userdBooked = alreadyBooked.stream()
                .filter( o -> o.userId != null && o.userId.equals(user.id))
                .count() > 0;
       
        if (userdBooked) {
            return;
        }
        
        Booking booking = createBooking(event, user);
        List<Booking> bookings = new ArrayList();
        bookings.add(booking);
        bookingEngine.addBookings(bookings);
        log("USER_ADDED", event, user);
    }   

    private Booking createBooking(Event event, User user) {
        Booking booking = new Booking();
        booking.bookingItemId = event.bookingItemId;
        booking.bookingItemTypeId = event.bookingItemType.id;
        booking.needConfirmation = false;
        booking.userId = user.id;
        return booking;
    }

    @Override
    public List<User> getUsersForEvent(String eventId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        return bookingEngine.getAllBookingsByBookingItem(event.bookingItemId).stream()
                .map(o -> userManager.getUserById(o.userId))
                .collect(Collectors.toList()); 
    }

    @Override
    public List<User> getUsersForEventWaitinglist(String eventId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeUserFromEvent(String eventId, String userId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        Booking booking = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId).stream()
            .filter( o -> o.userId.equals(userId))
            .findAny()
            .orElse(null);
        
        if (booking != null) {
            bookingEngine.deleteBooking(booking.id);
            log("USER_REMOVED", event, userManager.getUserById(userId));
        }
    }

    @Override
    public void addUserComment(String userId, String eventId, String comment) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        UserComment ucomment = new UserComment();
        ucomment.comment = comment;
        ucomment.userId = userId;
        ucomment.addedByUserId = getSession().currentUser.id;
        
        List<UserComment> comments = event.comments.get(userId);
        if (comments == null) {
            comments = new ArrayList();
            event.comments.put(userId, comments);
        }
        
        comments.add(ucomment);
        saveObject(event);
        log("Comment added", event, comment);
    }

    @Override
    public void addLocationFilter(String locationId) {
        List<String> sessionFilters = getLocationFilters();
        if (sessionFilters.contains(locationId)) {
            sessionFilters.remove(locationId);
        } else {
            sessionFilters.add(locationId);
        }
    }
    
    private List<String> getLocationFilters() {
        List<String> sessionFilters = (List<String>) getSession().get("sessionfilters");
        
        if (sessionFilters == null) {
            sessionFilters = new ArrayList();
            getSession().put("sessionfilters", sessionFilters);
        }
        
        return sessionFilters;
    }

    private List<Event> filterList(List<Event> events, List<String> locationFilters) {
        List<Event> retEvents = new ArrayList();
        
        for (Event event : events) {
            finalize(event);
            if (locationFilters.contains(event.location.id)) {
                retEvents.add(event);
            }
        }
        
        return retEvents;
    }

    private void finalize(Location loc) {
        loc.isFiltered = getLocationFilters().contains(loc.id);
    }

    @Override
    public void setParticipationStatus(String eventId, String userId, String status) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        event.participationStatus.put(userId, status);
        saveObject(event);
    }

    @Override
    public List<EventLog> getEventLog(String eventId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", EventLog.class.getCanonicalName());
        query.put("eventId", eventId);
        
        List<DataCommon> logEntries = database.query(EventBookingManager.class.getSimpleName()+"_"+getName(), storeId, query);
        List<EventLog> logToReturn = logEntries.stream().map(o -> (EventLog)o).collect(Collectors.toList());
        
        Collections.sort(logToReturn, (EventLog o1, EventLog o2) -> {
            return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
        });
        return logToReturn;
    }

    @Override
    public void markAsReady(String eventId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        event.markedAsReady = true;
        saveObject(event);
        log("MARK_AS_READY", event, null);
    }
}