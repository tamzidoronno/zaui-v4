package com.thundashop.core.pmseventmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import java.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingDateRange;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsEventManager extends GetShopSessionBeanNamed implements IPmsEventManager {
    HashMap<String, PmsBookingEventEntry> entries = new HashMap();
    HashMap<String, PmsEvent> eventEntries = new HashMap();

    @Autowired
    PmsManager pmsManager;
 
    @Autowired
    BookingEngine bookingEngine;
 
    @Autowired
    UserManager userManager;
    private List<PmsEventListEntry> cachedList;
    
     @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBookingEventEntry) {
                PmsBookingEventEntry entry = (PmsBookingEventEntry) dataCommon;
                entries.put(entry.id, entry);
            }
            if (dataCommon instanceof PmsEvent) {
                PmsEvent entry = (PmsEvent) dataCommon;
                eventEntries.put(entry.id, entry);
            }
        }
    }

    
    @Override
    public List<PmsBookingEventEntry> getEventEntries(PmsEventFilter filter) {
        return getEventEntriesInternal(filter, false);
    }
    
    public List<PmsBookingEventEntry> getEventEntriesWithDeleted(PmsEventFilter filter) {
        return getEventEntriesInternal(filter, true);
    }

    @Override
    public void saveEntry(PmsBookingEventEntry entry, String day) {
        cachedList = null;

        if(day != null && !day.isEmpty()) {
            PmsBookingEventEntry oldentry = getEntryClearOverrides(entry.id, "",false);
            oldentry.saveDay(entry, day);
            saveObject(oldentry);
        } else {
            entries.put(entry.id, entry);
            saveObject(entry);
        }
    }

    @Override
    public void deleteEntry(String entryId, String day) {
        cachedList = null;
        if(day == null) {
            for(PmsBookingEventEntry entry : entries.values()) {
                if(entry.bookingId.equals(entryId)) {
                    entry.isDeleted = true;
                    saveObject(entry);
                }
            }
        } else {
            PmsBookingEventEntry entry = getEntry(entryId, day);
            entry.isDeleted = true;
            saveObject(entry);
        }
    }

    @Override
    public PmsBookingEventEntry createEvent(String id) {
        cachedList = null;
        PmsBooking result = pmsManager.getBookingUnsecure(id);
        PmsBookingEventEntry entry = new PmsBookingEventEntry();
        entry.id = result.id;
        entry.title = result.registrationData.resultAdded.get("title");
        entry.shortdesc = result.registrationData.resultAdded.get("shortdesc");
        entry.bookingId = id;
        logPrint("Creating event:" + entry.title + " : " + entry.id);
        setRooms(entry, result, false);
        entries.put(entry.id, entry);
        saveEntry(entry, "");
        return entry;
    }

    @Override
    public PmsBookingEventEntry getEntry(String entryId, String day) {
        return getEntryClearOverrides(entryId, day, true);
    }

    @Override
    public PmsBookingEventEntry getEntryShort(String shortId, String day) {
        for(PmsBookingEventEntry entry : entries.values()) {
            if(entry.id.startsWith(shortId)) {
                entry = getEntry(entry.id, day);
                finalize(entry, false);
                return entry;
            }
        }
        return null;
    }

    private void setRooms(PmsBookingEventEntry entry, PmsBooking result, boolean includeDeteled) {
        entry.dateRanges.clear();
        entry.roomNames.clear();
        List<PmsBookingRooms> rooms = result.getActiveRooms();
        if(includeDeteled) {
            rooms = result.getAllRooms();
        }
        for(PmsBookingRooms room : rooms) {
            BookingItemType type = bookingEngine.getBookingItemType(room.bookingItemTypeId);
            if(type != null && type.addon > 0) {
                continue;
            }
            if(room.isDeleted() || entry.isDeleted) {
                room.date.isDeleted = true;
            } else {
                room.date.isDeleted = false;
            }
            entry.location = type.name;
            entry.dateRanges.add(room.date);
            BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
            if(item != null) {
                entry.roomNames.add(item.bookingItemName);
            } else {
                entry.roomNames.add(type.name);
            }
        }
    }

    private PmsBookingEventEntry finalize(PmsBookingEventEntry get, boolean includeDeleted) {
        PmsBooking booking = pmsManager.getBookingUnsecureUnfinalized(get.id);
        setRooms(get, booking, includeDeleted);
        get.arrangedBy = userManager.getUserById(booking.userId) != null ? userManager.getUserById(booking.userId).fullName : "";
        get.finalizeSubEntries();
        return get;
    }

    private void removeDeadEvents() {
        List<PmsBookingEventEntry> toremove = new ArrayList();
        for(PmsBookingEventEntry event : entries.values()) {
            if(event == null) {
                continue;
            }             
            if(event.bookingId == null || event.bookingId.trim().isEmpty()) {
                event.isDeleted = true;
                continue;
            }

            if(event.isDeleted) {
                continue;
            }
            PmsBooking booking = pmsManager.getBookingUnsecureUnfinalized(event.id);
            if(booking == null || booking.isDeleted) {
                event.isDeleted = true;
                saveObject(event);
            }
        }
    }

    @Override
    public List<PmsEventListEntry> getEventList() {
        if(cachedList != null) {
            return cachedList;
        }
        List<PmsEventListEntry> list = getEventListInternal(false);
        cachedList = list;
        return list;
    }

    @Override
    public List<PmsEventListEntry> getEventListWithDeleted() {
        return getEventListInternal(true);
    }

    private String getDayString(Calendar cal) {
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        month++;
        String timeString = year + "-";
        if(month < 10) {
            timeString += "0" + month;
        } else {
            timeString += month;
        }
        
        timeString += "-";
        
        if(day < 10) {
            timeString += "0" + day;
        } else {
            timeString += day;
        }
        
        return timeString;
    }

    private List<PmsBookingEventEntry> getEventEntriesInternal(PmsEventFilter filter, boolean includeDeleted) {
        removeDeadEvents();
        List<PmsBookingEventEntry> result = new ArrayList();
        if(filter == null) {
            for(PmsBookingEventEntry entry : entries.values()) {
                if(entry.isDeleted && !includeDeleted) {
                    continue;
                }
                finalize(entry, includeDeleted);
                result.add(entry);
            }
            return result;
        }
        for(String id : filter.bookingIds) {
            result.add(getEntry(id, ""));
        }
        
        return result;
    }

    private List<PmsEventListEntry> getEventListInternal(boolean includeDeleted) {
        List<PmsBookingEventEntry> eventlist = null;
        if(includeDeleted) {
            eventlist = getEventEntriesWithDeleted(null);
        } else {
            eventlist = getEventEntries(null);
        }
        List<PmsEventListEntry> result = new ArrayList();
        Calendar cal = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 6);
        Date todayDate = today.getTime();
        List<String> added = new ArrayList();
        for(PmsBookingEventEntry entry : eventlist) {
            int i = 0;
            int offset = 0;
            for(PmsBookingDateRange range : entry.dateRanges) {
                offset++;
                if(!range.start.after(todayDate)) {
                    continue;
                }
                
                cal.setTime(range.start);
                String day = getDayString(cal);
                
                PmsBookingEventEntry overrideEntry = entry.getDay(day);
                PmsEventListEntry newEntry = new PmsEventListEntry(overrideEntry, range.start, entry.roomNames.get(i));

                newEntry.eventId = entry.id;
                newEntry.eventDateId = entry.id + "_" + day;
                newEntry.isDeleted = range.isDeleted;
                if(!added.contains(newEntry.eventDateId)) {
                    added.add(newEntry.eventDateId);
                    result.add(newEntry);
                }
                i++;
            }
        }
        
        Collections.sort(result, new Comparator<PmsEventListEntry>(){
            public int compare(PmsEventListEntry o1, PmsEventListEntry o2){
                return o1.date.compareTo(o2.date);
            }
       });
        
        return result;  
    }

    public void deleteAllEvents() {
        for(PmsBookingEventEntry entry : entries.values()) {
            deleteObject(entry);
        }
        entries.clear();
    }

    @Override
    public boolean isChecked(String pmsBookingId) {
        List<PmsEventListEntry> list = getEventList();
        for(PmsEventListEntry entry : list) {
            if(entry.bookingId != null && entry.bookingId.equals(pmsBookingId)) {
                return true;
            }
        }
        return false;
    }

    private PmsBookingEventEntry getEntryClearOverrides(String entryId, String day, boolean clear) {
        if(entries.get(entryId) == null) {
            createEvent(entryId);
        }
        
        PmsBookingEventEntry entry = finalize(entries.get(entryId), false);
        if(day != null && !day.isEmpty()) {
            entry = entry.getDay(day);
            entry.id = entryId;
        }
        
        if(clear) {
            Gson gson = new Gson();
            String txt = gson.toJson(entry);
            entry = gson.fromJson(txt, PmsBookingEventEntry.class);
            entry.roomNames = new ArrayList();
            entry.overrideEntries = new HashMap();
        }

        return entry;
    }

    @Override
    public void saveEvent(PmsEvent event) {
        saveObject(event);
        eventEntries.put(event.id, event);
    }

    @Override
    public void deleteEvent(String id) {
        PmsEvent object = getEvent(id);
        deleteObject(object);
        eventEntries.remove(id);
    }

    @Override
    public List<PmsEvent> getEvents(PmsEvent filter) {
        return new ArrayList(eventEntries.values());
    }

    @Override
    public PmsEvent getEvent(String id) {
        return eventEntries.get(id);
    }
    
}
