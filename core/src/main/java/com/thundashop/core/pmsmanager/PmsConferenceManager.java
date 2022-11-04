package com.thundashop.core.pmsmanager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.mongodb.BasicDBObject;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.cartmanager.data.CartItem;
import com.thundashop.core.common.ConferenceDiffLog;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.NullSafeConcurrentHashMap;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.dbbackupmanager.DBBackupManager;
import com.thundashop.core.pos.PosConference;
import com.thundashop.core.pos.PosManager;
import com.thundashop.core.pos.PosTab;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.TaxGroup;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;

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
   
    @Autowired
    private DBBackupManager backupManager;
    
    @Autowired
    private PosManager posManager;
    
    @Autowired
    private ProductManager productManager;

    @Autowired
    private PmsManager pmsManager;
    
    Map<String, PmsConferenceItem> items = new NullSafeConcurrentHashMap<>();
    Map<String, PmsConference> conferences = new NullSafeConcurrentHashMap<>();
    Map<String, PmsConferenceEvent> conferenceEvents = new NullSafeConcurrentHashMap<>();
    Map<String, PmsConferenceEventEntry> conferenceEventEntries = new NullSafeConcurrentHashMap<>();

    
    public List<PmsConferenceEventEntry> getAllEventEntries() {
        return new ArrayList<>(conferenceEventEntries.values());
    }
    
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof PmsConferenceItem) { items.put(dataCommon.id, (PmsConferenceItem) dataCommon); }
            if(dataCommon instanceof PmsConference) { conferences.put(dataCommon.id, (PmsConference) dataCommon); }
            if(dataCommon instanceof PmsConferenceEvent) { conferenceEvents.put(dataCommon.id, (PmsConferenceEvent) dataCommon); }
            if(dataCommon instanceof PmsConferenceEventEntry) { conferenceEventEntries.put(dataCommon.id, (PmsConferenceEventEntry) dataCommon); }
        }
        
        deleteEventsWithNoConference();
    }
    
    @Override
    public List<PmsConferenceItem> getAllItem(String toItem) {
        List<PmsConferenceItem> result = new ArrayList<>();
        if(toItem != null && toItem.equals("-1")) {
            result = new ArrayList<>(items.values());
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
        
        HashMap<String, PmsConferenceItem> checkitems = new HashMap<>(items);
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
        logDiff(conference.id, conference);
        saveObject(conference);
        conferences.put(conference.id, conference);
        conferenceUpdated(conference);
        return conference;
    }

    @Override
    public void deleteConference(String conferenceId) {
        PmsConference conference = conferences.get(conferenceId);
        conferences.remove(conferenceId);

        conferenceUpdated(conference);

        deleteObject(conference);
        
        HashMap<String, PmsConferenceEvent> confevents = new HashMap<>(conferenceEvents);        
        for(PmsConferenceEvent evnt : confevents.values()) {
            if(evnt.pmsConferenceId.equals(conferenceId)) {
                deleteConferenceEvent(evnt.id);
            }
        }       

    }

    @Override
    public String createConferenceEvent(PmsConferenceEvent event) {
        if (event.pmsConferenceId == null || event.pmsConferenceId.isEmpty()) {
            return null;
        }
        
        if(!canAddEvent(event)) {
            return null;
        }
        
        logDiff(event.pmsConferenceId, event);
        saveObject(event);
        conferenceEvents.put(event.id, event);
        conferenceUpdated(getConference(event.pmsConferenceId));
        return event.id;
    }
    
    @Override
    public boolean saveConferenceEvent(PmsConferenceEvent event) {
        String eventId = createConferenceEvent(event);
        if(eventId != null) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteConferenceEvent(String id) {
        PmsConferenceEvent conferenceevent = conferenceEvents.get(id);
        deleteObject(conferenceevent);
        conferenceEvents.remove(id);
        HashMap<String, PmsConferenceEventEntry> eventlog = new HashMap<>(conferenceEventEntries);
        
        for(PmsConferenceEventEntry evntlog : eventlog.values()) {
            if(!evntlog.pmsEventId.equals(id)) {
                continue;
            }
            deleteEventEntry(evntlog.id);
        }
        
        conferenceUpdated(getConference(conferenceevent.pmsConferenceId));
    }

    @Override
    public PmsConferenceItem getItem(String id) {
        return items.get(id);
    }

    @Override
    public PmsConference getConference(String conferenceId) {
        checkIfDateIsCorrectOnConferences();
        PmsConference conference = conferences.get(conferenceId);
        return conference;
    }

    @Override
    public List<PmsConferenceEvent> getConferenceEvents(String confernceId) {
        List<PmsConferenceEvent> result = new ArrayList<>();
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
        List<PmsConferenceEventEntry> result = new ArrayList<>();
        PmsConferenceEvent event = getConferenceEvent(eventId);
        for(PmsConferenceEventEntry entry : conferenceEventEntries.values()) {
            if(entry.pmsEventId.equals(eventId)) {
                if(entry.from == null) {
                    entry.from = new Date();
                }
                if(!PmsBookingRooms.isSameDayStatic(event.from, entry.from)) {
                    Calendar cal1 = Calendar.getInstance();
                    Calendar cal2 = Calendar.getInstance();
                    cal1.setTime(event.from);
                    cal2.setTime(entry.from);
                    cal2.set(Calendar.DAY_OF_YEAR, cal1.get(Calendar.DAY_OF_YEAR));
                    cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                    entry.from = cal2.getTime();
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
    public void deleteObject(DataCommon data) throws ErrorException {
        if (data instanceof PmsConferenceEvent) {
            conferenceUpdated(getConference(((PmsConferenceEvent)data).pmsConferenceId));
        }
        
        super.deleteObject(data); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PmsConferenceEventEntry getEventEntry(String eventEntryId) {
        return conferenceEventEntries.get(eventEntryId);
    }

    @Override
    public void saveEventEntry(PmsConferenceEventEntry entry) {
        logDiff(entry.conferenceId, entry);
        saveObject(entry);
        conferenceEventEntries.put(entry.id, entry);
    }

    @Override
    public List<PmsConferenceEvent> getConferenceEventsBetweenTime(Date start, Date end) {
        List<PmsConferenceEvent> result = new ArrayList<>();
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
        ArrayList<PmsConference> result = getFilterResult(filter);
        result.sort(Comparator.comparing(a -> a.meetingTitle));
        addConferenceMetaData(result);
        return result;
    }

    private void addConferenceMetaData(ArrayList<PmsConference> result) {
        result.stream().forEach(res -> {
            if (res.forUser != null && !res.forUser.isEmpty()) {
                User user = userManager.getUserById(res.forUser);
                if (user != null) {
                    res.forUserFullName = user.fullName;
                }
            }
        });
    }

    @Override
    public List<PmsConferenceEvent> getConferenceEventsByFilter(PmsConferenceEventFilter filter) {
        List<PmsConferenceEvent> result = new ArrayList<>();
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
            List<PmsConferenceEvent> allEvents = new ArrayList<>();
            for(PmsConferenceEvent event : result) {
                if(filter.itemIds.contains(event.pmsConferenceItemId)) {
                    allEvents.add(event);
                }
            }
            result = allEvents;
        }
        
        
        for(PmsConferenceEvent event : result) {
            PmsConference conf = getConference(event.pmsConferenceId);
            if(conf != null) {
                event.meetingTitle = conf.meetingTitle;
            }
        }
        
        return result;
    }

    @Override
    public List<PmsConferenceGuests> getAllGuestsForEvent(String eventId) {
        List<String> multiLevelNames = database.getMultilevelNames("PmsManager", storeId);
        List<PmsConferenceGuests> result = new ArrayList<>();
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
        List<PmsConferenceEventEntry> result = new ArrayList<>();
        for(PmsConferenceEventEntry entry : conferenceEventEntries.values()) {
            if(entry.inTime(filter)) {
                PmsConferenceEvent event = getConferenceEvent(entry.pmsEventId);
                
                if(filter.pmsConferenceId != null && !filter.pmsConferenceId.isEmpty()) {
                    if(!filter.pmsConferenceId.equals(event.pmsConferenceId)) {
                        continue;
                    }
                }
                
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
            if(evt != null && evt.betweenTime(event.from, event.to) && event.pmsConferenceItemId.equals(evt.pmsConferenceItemId)) {
                return false;
            }
        }
        return true;
    }

    private void deleteEventsWithNoConference() {
        List<PmsConferenceEvent> toDelete = conferenceEvents.values()
                .stream()
                .filter(o -> o.pmsConferenceId == null || o.pmsConferenceId.isEmpty())
                .collect(Collectors.toList());
        
        for (PmsConferenceEvent event : toDelete) {
            conferenceEvents.remove(event.id);
            deleteObject(event);
            logPrint("Deleted event with no conference");
        }
    }

    private ArrayList<PmsConference> getFilterResult(PmsConferenceFilter filter) {
        ArrayList<PmsConference> retList = new ArrayList<>(conferences.values());
        
        if(filter != null) {
            if(filter.title != null && !filter.title.isEmpty()) {
                retList.removeIf(o -> !o.meetingTitle.toLowerCase().contains(filter.title));
            }
            
            if (filter.onlyNoneExpiredEvents) {
                retList.removeIf(o -> eventHasExpired(o));
            }

            if (!filter.userIds.isEmpty()) {
                retList.removeIf(o -> !filter.userIds.contains(o.forUser));
            }
            
            if(filter.start != null && filter.end != null) {
                retList.removeIf(o -> isNotWithin(o, filter.start, filter.end));
            }
        }
        
        return retList;
    }

    private boolean eventHasExpired(PmsConference o) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -3);
        Date expireDate = cal.getTime();
                
        return getConferenceEvents(o.id)
                .stream()
                .filter(event -> event.to != null)
                .filter(event -> event.to.after(expireDate))
                .count() == 0;
    }

    public boolean anyConferences() {
        return !conferences.isEmpty();
    }

    public List<String> getConferencesIds() {
        return new ArrayList<>(conferences.keySet());
    }

    public Date getExpiryDate(String confId) {
        Date date = new Date(0);
        for (PmsConferenceEvent event : getConferenceEvents(confId)) {
            if (event.to.after(date)) {
                date = event.to;
            }
        }
        
        return date;
    }

    private void conferenceUpdated(PmsConference conference) {
        if (conference != null) {
            posManager.updatePosConference(conference.id);
        }
    }

    @Override
    public List<PmsConference> searchConferences(String searchWord) {
        if (searchWord == null || searchWord.isEmpty())
            return new ArrayList<>();
        
        return conferences.values()
                .stream()
                .filter(o -> o.meetingTitle.toLowerCase().contains(searchWord.trim().toLowerCase()))
                .filter(o -> {
                    // filter out conferences that does not have anything to pay
                    PosConference conf = posManager.getPosConference(o.id);
                    return posManager.getTab(conf.tabId).cartItems.isEmpty();
                })
                .collect(Collectors.toList());
    }   

    public void logDiff(String conferenceId, DataCommon data) throws ErrorException {
        DataCommon oldObject = database.getObject(credentials, data.id);
        String diff = backupManager.diffObjects(oldObject, data);

        if (!diff.trim().isEmpty()) {
            ConferenceDiffLog diffLog = new ConferenceDiffLog();
            diffLog.diff = diff;
            diffLog.doneByUser = getSession() != null && getSession().currentUser != null ? getSession().currentUser.id : "";
            diffLog.forClassName = data.getClass().getCanonicalName();
            diffLog.conferenceId = conferenceId;
            saveObject(diffLog);
        }
    }

    @Override
    public List<ConferenceDiffLog> getDiffLog(String conferenceId) {
        BasicDBObject query = new BasicDBObject();
        query.put("conferenceId", conferenceId);
        query.put("className", ConferenceDiffLog.class.getCanonicalName());
        
        return database.query(getClass().getSimpleName(), storeId, query)
                .stream()
                .map(o -> (ConferenceDiffLog)o)
                .sorted((ConferenceDiffLog a, ConferenceDiffLog b) -> {
                    return b.rowCreatedDate.compareTo(a.rowCreatedDate);
                })
                .collect(Collectors.toList());
    }

    private void checkIfDateIsCorrectOnConferences() {
        try {
            List<PmsConferenceEventEntry> events = getAllEventEntries();
            Calendar cal1 = Calendar.getInstance();
            Calendar cal2 = Calendar.getInstance();
            for(PmsConferenceEventEntry entry : events) {
                PmsConferenceEvent event = getConferenceEvent(entry.pmsEventId);
                if(event != null && entry.from != null && event.from != null) {
                    if(!PmsBookingRooms.isSameDayStatic(event.from, entry.from)) {
                        cal1.setTime(event.from);
                        cal2.setTime(entry.from);
                        cal2.set(Calendar.DAY_OF_YEAR, cal1.get(Calendar.DAY_OF_YEAR));
                        cal2.set(Calendar.YEAR, cal1.get(Calendar.YEAR));
                        entry.from = cal2.getTime();
                   }
                }
            }
        }catch(Exception e) {
        }
    }

    @Override
    public void addCartItemsToConference(String confernceId, String eventId, List<CartItem> cartItems) {
        PmsConference conference = getConference(confernceId);
        
        if (conference == null) {
            return;   
        }
        
        PosConference posConference = posManager.getPosConference(confernceId);

        if (posConference == null) {
            return;   
        }
        
        posManager.updatePosConference(posConference.id);
        posConference = posManager.getPosConference(posConference.id);
        
        final String tabId = posConference.tabId;
        
        cartItems.stream().forEach(item -> {
            TaxGroup taxGroup = productManager.getTaxGroup(item.getProduct().taxgroup);
            
            if (taxGroup == null) {
                throw new NullPointerException("Not able to find the gived taxgroupobject for the taxid");
            }
            
            item.getProduct().taxGroupObject = taxGroup;
            
            if (productManager.getProduct(item.getProductId()) == null) {
                productManager.saveProduct(item.getProduct());
            }
            
            item.conferenceId = confernceId;
            item.conferenceEventId = eventId;
            posManager.addToTab(tabId, item);
        });
    }
        

    @Override
    public String createConference(String engine, Date date, String name) {
        PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(engine, PmsManager.class);
        pmsManager.startBooking();
        
        PmsBooking booking = pmsManager.getCurrentBooking();
        
        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemTypeId = "gspmsconference";
        room.date.start = date;
        room.date.end = date;
        room.deleted = true;
        room.deletedDate = new Date();
        booking.rooms.add(room);
        
        try {
            pmsManager.setBooking(booking);
        } catch (Exception ex) {
            logPrintException(ex);
        }
        
        pmsManager.completeConferenceBooking();
        
        PmsConference conference = new PmsConference();
        conference.conferenceDate = date;
        conference.meetingTitle = name;
        conference.pmsBookingId = booking.id;

        saveConference(conference);

        booking.conferenceId = conference.id;
        pmsManager.saveBooking(booking);

        return room.pmsBookingRoomId;
    }

    @Override
    public PmsConference createConferenceForV5(String engine, Date date, String name, int noOfGuest) throws Exception {
        PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(engine, PmsManager.class);
        pmsManager.startBooking();
        
        PmsBooking booking = pmsManager.getCurrentBooking();
        
        PmsBookingRooms room = new PmsBookingRooms();
        room.bookingItemTypeId = "gspmsconference";
        room.date.start = date;
        room.date.end = date;
        room.deleted = true;
        room.deletedDate = new Date();
        booking.rooms.add(room);
        
        pmsManager.setBooking(booking);
        
        PmsConference conference = new PmsConference();
        conference.conferenceDate = date;
        conference.meetingTitle = name;
        conference.pmsBookingId = booking.id;
        conference.attendeeCount = noOfGuest;

        saveConference(conference);

        booking.conferenceId = conference.id;
        pmsManager.saveBooking(booking);

        return conference;
    }

    @Override
    public PmsBooking completeConferenceForV5(String engine) {
        PmsManager pmsManager = getShopSpringScope.getNamedSessionBean(engine, PmsManager.class);
        return pmsManager.completeConferenceBooking();
    }

    private PosTab getTabForConference(String conferenceId) {
        PmsConference conference = getConference(conferenceId);
        if (conference == null) {
            return null;
        }
        
        PosConference posConference = posManager.getPosConference(conference.id);
        
        if (posConference == null) {
            return null;
        }
        
        return posManager.getTab(posConference.tabId);
    }
    
    @Override
    public List<CartItem> getCartItems(String conferenceId, String eventId) {
        PosTab tab = getTabForConference(conferenceId);
        
        if (tab == null) {
            return new ArrayList<>();
        }
        
        List<CartItem> retList = tab.cartItems.stream()
                .filter(o -> o.conferenceEventId != null && o.conferenceEventId.equals(eventId))
                .collect(Collectors.toList());
        
        retList.sort((CartItem item1, CartItem item2) -> {
            return item1.getProductId().compareTo(item2.getProductId());
        });
        
        return retList;
    }

    @Override
    public void removeCartItemFromConference(String conferenceId, String cartItemId) {
        PosTab tab = getTabForConference(conferenceId);
        
        if (tab != null) {
            tab.cartItems.removeIf(o -> o.getCartItemId().equals(cartItemId));
            posManager.saveObject(tab);
        }        
    }

    @Override
    public void updateCartItem(String conferenceId, CartItem cartItem) {
        PosTab tab = getTabForConference(conferenceId);
        
        if (tab != null && cartItem != null) {
            removeCartItemFromConference(conferenceId, cartItem.getCartItemId());
            
            List<CartItem> cartItems = new ArrayList<>();
            cartItems.add(cartItem);
            
            addCartItemsToConference(conferenceId, cartItem.conferenceEventId, cartItems);
        }
    }

    @Override
    public CartItem getCartItem(String conferenceId, String cartItemId) {
        PosTab tab = getTabForConference(conferenceId);
        
        if (tab != null && cartItemId != null) {
            return tab.getCartItem(cartItemId);
        }
        
        return null;
    }

    @Override
    public Double getTotalPriceForCartItems(String conferenceId, String eventId) {
        List<CartItem> items = getCartItems(conferenceId, eventId);
        
        return items.stream()
                .mapToDouble(o -> o.getTotalAmount())
                .sum();
    }

    public PmsConference getConferenceDirectFromDB(String pmsConferenceId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", pmsConferenceId);
        
        DataCommon retObject = database.query(getClass().getSimpleName(), storeId, query)
                .stream()
                .findAny()
                .orElse(null);
        
        if (retObject != null) {
            return (PmsConference)retObject;
        }
        
        return null;
    }

    public PmsConferenceEvent getConferenceEventDirectFromDB(String eventId) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", eventId);
        
        DataCommon retObject = database.query(getClass().getSimpleName(), storeId, query)
                .stream()
                .findAny()
                .orElse(null);
        
        if (retObject != null) {
            return (PmsConferenceEvent)retObject;
        }
        
        return null;
    }

    private boolean isNotWithin(PmsConference o, Date start, Date end) {
        if(o.conferenceDate == null) {
            return false;
        }
        
        return o.conferenceDate.before(start) || o.conferenceDate.after(end);
    }

    @Override
    public List<PmsBookingWithConferenceDto> getBookingConferences(PmsConferenceFilter filter) {
        List<PmsBookingWithConferenceDto> bookings = new ArrayList<>();

        getAllConferences(filter).stream().forEach(conference -> {  
            PmsBooking booking = pmsManager.getconferenceBooking(conference.id);
            if(booking != null){
                bookings.add(new PmsBookingWithConferenceDto(booking, conference, getConferenceEvents(conference.id)));
            }
        });

        return new ArrayList<>(bookings);
    }
}