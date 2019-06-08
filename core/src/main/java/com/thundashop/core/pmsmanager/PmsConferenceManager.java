package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsConferenceManager extends ManagerBase implements IPmsConferenceManager {
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    BookingEngine engine;
    
   @Autowired
    private GetShopSessionScope getShopSpringScope; 
    
    HashMap<String, PmsConferenceItem> items = new HashMap();
    HashMap<String, PmsConference> conferences = new HashMap();
    HashMap<String, PmsConferenceEvent> conferenceEvents = new HashMap();
    HashMap<String, PmsConferenceEventEntry> conferenceEventEntries = new HashMap();

    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof PmsConferenceItem) { items.put(dataCommon.id, (PmsConferenceItem) dataCommon); }
            if(dataCommon instanceof PmsConference) { conferences.put(dataCommon.id, (PmsConference) dataCommon); }
            if(dataCommon instanceof PmsConferenceEvent) { conferenceEvents.put(dataCommon.id, (PmsConferenceEvent) dataCommon); }
            if(dataCommon instanceof PmsConferenceEventEntry) { conferenceEventEntries.put(dataCommon.id, (PmsConferenceEventEntry) dataCommon); }
        }
    }
    
    @Override
    public List<PmsConferenceItem> getAllItem(String toItem) {
        List<PmsConferenceItem> result = new ArrayList();
        if(toItem != null && toItem.equals("-1")) {
            result = new ArrayList(items.values());
        } else {
            for(PmsConferenceItem item : items.values()) {
                if(item.toItemId.equals(toItem)) {
                    List<PmsConferenceItem> subitems = getAllItem(item.id);
                    item.hasSubItems =  subitems.size() > 0;
                    if(item.hasSubItems) {
                        for(PmsConferenceItem tmp : subitems) {
                            item.subItems.add(tmp.id);
                        }
                    }
                    result.add(item);
                }
            }
            }
        
        result.sort(Comparator.comparing(a -> a.name));

        
        return result;
    }

    @Override
    public void deleteItem(String itemId) {
        PmsConferenceItem item = getItem(itemId);
        items.remove(item.id);
        deleteObject(item);
        
        HashMap<String, PmsConferenceItem> checkitems = new HashMap(items);
        for(PmsConferenceItem itm : checkitems.values()) {
            if(itm.toItemId.equals(itemId)) {
                deleteItem(itm.id);
            }
        }
    }

    @Override
    public PmsConferenceItem saveItem(PmsConferenceItem item) {
        saveObject(item);
        items.put(item.id, item);
        return item;
    }

    @Override
    public PmsConference saveConference(PmsConference conference) {
        saveObject(conference);
        conferences.put(conference.id, conference);
        return conference;
    }

    @Override
    public void deleteConference(String conferenceId) {
        PmsConference conference = conferences.get(conferenceId);
        conferences.remove(conferenceId);
        deleteObject(conference);
        
        HashMap<String, PmsConferenceEvent> confevents = new HashMap(conferenceEvents);        
        for(PmsConferenceEvent evnt : confevents.values()) {
            if(evnt.pmsConferenceId.equals(conferenceId)) {
                deleteConferenceEvent(evnt.id);
            }
        }
    }

    @Override
    public boolean saveConferenceEvent(PmsConferenceEvent event) {
        if(!canAddEvent(event)) {
            return false;
        }
        saveObject(event);
        conferenceEvents.put(event.id, event);
        return true;
    }

    @Override
    public void deleteConferenceEvent(String id) {
        PmsConferenceEvent conferenceevent = conferenceEvents.get(id);
        deleteObject(conferenceevent);
        conferenceEvents.remove(id);
        HashMap<String, PmsConferenceEventEntry> eventlog = new HashMap(conferenceEventEntries);
        
        for(PmsConferenceEventEntry evntlog : eventlog.values()) {
            if(!evntlog.pmsEventId.equals(id)) {
                continue;
            }
            deleteEventEntry(evntlog.id);
        }
    }

    @Override
    public PmsConferenceItem getItem(String id) {
        return items.get(id);
    }

    @Override
    public PmsConference getConference(String conferenceId) {
        return conferences.get(conferenceId);
    }

    @Override
    public List<PmsConferenceEvent> getConferenceEvents(String confernceId) {
        List<PmsConferenceEvent> result = new ArrayList();
        for(PmsConferenceEvent evnt : conferenceEvents.values()) {
            if(evnt.pmsConferenceId.equals(confernceId)) {
                result.add(evnt);
            }
        }
        result.sort(Comparator.comparing(a -> a.from));
        return result;
    }

    @Override
    public PmsConferenceEvent getConferenceEvent(String eventId) {
        return conferenceEvents.get(eventId);
    }

    @Override
    public List<PmsConferenceEventEntry> getEventEntries(String eventId) {
        List<PmsConferenceEventEntry> result = new ArrayList();
        for(PmsConferenceEventEntry entry : conferenceEventEntries.values()) {
            if(entry.pmsEventId.equals(eventId)) {
                if(entry.from == null) {
                    entry.from = new Date();
                }
                result.add(entry);
            }
        }
        
        result.sort(Comparator.comparing(a -> a.from));
        return result;
    }

    @Override
    public void deleteEventEntry(String eventEntryId) {
        PmsConferenceEventEntry event = getEventEntry(eventEntryId);
        deleteObject(event);
        conferenceEventEntries.remove(event.id);
    }

    @Override
    public PmsConferenceEventEntry getEventEntry(String eventEntryId) {
        return conferenceEventEntries.get(eventEntryId);
    }

    @Override
    public void saveEventEntry(PmsConferenceEventEntry entry) {
        saveObject(entry);
        conferenceEventEntries.put(entry.id, entry);
    }

    @Override
    public List<PmsConferenceEvent> getConferenceEventsBetweenTime(Date start, Date end) {
        List<PmsConferenceEvent> result = new ArrayList();
        for(PmsConferenceEvent evnt : conferenceEvents.values()) {
            if(evnt.betweenTime(start, end)) {
                PmsConference conference = getConference(evnt.pmsConferenceId);
                if(conference != null) {
                    evnt.title = conference.meetingTitle;
                    result.add(evnt);
                }
            }
        }
        result.sort(Comparator.comparing(a -> a.from));
        return result;
    }

    @Override
    public List<PmsConference> getAllConferences(PmsConferenceFilter filter) {
        ArrayList<PmsConference> result = new ArrayList(conferences.values());
        result.sort(Comparator.comparing(a -> a.meetingTitle));
        return result;
    }

    @Override
    public List<PmsConferenceEvent> getConferenceEventsByFilter(PmsConferenceEventFilter filter) {
        List<PmsConferenceEvent> result = new ArrayList();
        if(filter.start != null && filter.end != null) {
            List<PmsConferenceEvent> eventsToAdd = getConferenceEventsBetweenTime(filter.start, filter.end);
            result.addAll(eventsToAdd);
        }
        
        if(filter.userId != null && !filter.userId.isEmpty()) {
            for(PmsConferenceEvent event : conferenceEvents.values()) {
                if(event.userId.equals(filter.userId) && result.contains(event)) {
                    result.add(event);
                }
            }
        }
        
        if(filter.keyword != null && !filter.keyword.isEmpty()) {
            for(PmsConferenceEvent event : conferenceEvents.values()) {
                PmsConference conference = getConference(event.pmsConferenceId);
                if(conference.meetingTitle != null && conference.meetingTitle.toLowerCase().contains(filter.keyword.toLowerCase())) {
                    result.add(event);
                }
            }
        }
        
       
        if(!filter.itemIds.isEmpty()) {
            List<PmsConferenceEvent> allEvents = new ArrayList();
            for(PmsConferenceEvent event : result) {
                if(filter.itemIds.contains(event.pmsConferenceItemId)) {
                    allEvents.add(event);
                }
            }
            result = allEvents;
        }
        
        
        return result;
    }

    @Override
    public List<PmsConferenceGuests> getAllGuestsForEvent(String eventId) {
        List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        List<PmsConferenceGuests> result = new ArrayList();
        for (String multilevelName : multiLevelNames) {
            PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
            BookingEngine engine = getShopSpringScope.getNamedSessionBean(multilevelName, BookingEngine.class);
            List<PmsBooking> allbookings = pmsManager.getAllBookings(null);
            allbookings.stream().forEach(booking -> {
                booking.rooms.stream().forEach(room -> {
                    room.guests.stream().forEach(guest -> {
                        if(guest.pmsConferenceEventIds != null && guest.pmsConferenceEventIds.contains(eventId)) {
                            PmsConferenceGuests toAdd = new PmsConferenceGuests();
                            toAdd.guest = guest;
                            toAdd.bookerName = userManager.getUserByIdUnfinalized(booking.userId).fullName;
                            if(room.bookingItemTypeId != null) {
                                BookingItemType type = engine.getBookingItemType(room.bookingItemTypeId);
                                if(type != null) { toAdd.bookingItemTypeName = type.name; }
                            }
                            if(room.bookingItemId != null) {
                                BookingItem item = engine.getBookingItem(room.bookingItemId);
                                if(item != null) { toAdd.bookingItem = item.bookingItemName; }
                            }
                            toAdd.start = room.date.start;
                            toAdd.end = room.date.end;
                            
                            result.add(toAdd);
                        }
                    });
                });
            });
        }
        return result;
    }

    @Override
    public void removeGuestFromEvent(String guestId, String eventId) {
      List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        for (String multilevelName : multiLevelNames) {
            PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
            BookingEngine engine = getShopSpringScope.getNamedSessionBean(multilevelName, BookingEngine.class);
            List<PmsBooking> allbookings = pmsManager.getAllBookings(null);
            allbookings.stream().forEach(booking -> {
                booking.rooms.stream().forEach(room -> {
                    room.guests.stream().forEach(guest -> {
                        if(guest.guestId != null && guest.guestId.equals(guestId)) {
                            guest.pmsConferenceEventIds.remove(eventId);
                            pmsManager.saveBooking(booking);
                            return;
                        }
                    });
                });
            });
        }
    }

    @Override
    public void addGuestToEvent(String guestId, String eventId) {
    List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        for (String multilevelName : multiLevelNames) {
            PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(multilevelName, PmsManager.class);
            BookingEngine engine = getShopSpringScope.getNamedSessionBean(multilevelName, BookingEngine.class);
            List<PmsBooking> allbookings = pmsManager.getAllBookings(null);
            allbookings.stream().forEach(booking -> {
                booking.rooms.stream().forEach(room -> {
                    room.guests.stream().forEach(guest -> {
                        if(guest.guestId != null && guest.guestId.equals(guestId)) {
                            guest.pmsConferenceEventIds.add(eventId);
                            pmsManager.saveBooking(booking);
                            return;
                        }
                    });
                });
            });
        }
    }

    @Override
    public List<PmsConferenceEventEntry> getEventEntriesByFilter(PmsConferenceEventFilter filter) {
        List<PmsConferenceEventEntry> result = new ArrayList();
        for(PmsConferenceEventEntry entry : conferenceEventEntries.values()) {
            if(entry.inTime(filter)) {
                PmsConferenceEvent event = getConferenceEvent(entry.pmsEventId);
                
                if(!filter.itemIds.isEmpty() && !filter.itemIds.contains(event.pmsConferenceItemId)) {
                    continue;
                }
                
                if(event != null) {
                    PmsConferenceItem item = getItem(event.pmsConferenceItemId);
                    if(item != null) {
                        entry.conferenceItem = item.name;
                    }
                    PmsConference confernece = getConference(event.pmsConferenceId);
                    if(confernece != null) { entry.meetingTitle = confernece.meetingTitle; }
                    if(confernece != null) { entry.conferenceId = confernece.id; }
                }
                result.add(entry);
            }
        }
        result.sort(Comparator.comparing(a -> a.from));
        return result;
    }

    private boolean canAddEvent(PmsConferenceEvent event) {
        for(PmsConferenceEvent evt : conferenceEvents.values()) {
            if(evt != null && evt.id != null && event != null && event.id != null && evt.id.equals(event.id)) {
                continue;
            }
            if(evt.betweenTime(event.from, event.to) && event.pmsConferenceItemId.equals(evt.pmsConferenceItemId)) {
                return false;
            }
        }
        return true;
    }
    
}
