package com.thundashop.core.pkk.pmseventmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pmseventmanager.PmsBookingEventEntry;
import com.thundashop.core.pmseventmanager.PmsEventFilter;
import com.thundashop.core.pmsmanager.IPmsManager;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsEventManager extends GetShopSessionBeanNamed implements IPmsEventManager {
    HashMap<String, PmsBookingEventEntry> entries = new HashMap();

    @Autowired
    PmsManager pmsManager;
 
    @Autowired
    BookingEngine bookingEngine;
    
     @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PmsBookingEventEntry) {
                PmsBookingEventEntry entry = (PmsBookingEventEntry) dataCommon;
                entries.put(entry.id, entry);
            }
        }
    }

    
    @Override
    public List<PmsBookingEventEntry> getEventEntries(PmsEventFilter filter) {
        List<PmsBookingEventEntry> result = new ArrayList();
        if(filter == null) {
            return new ArrayList(entries.values());
        }
        for(String id : filter.bookingIds) {
            result.add(getEntry(id));
        }
        
        return result;
    }

    @Override
    public void saveEntry(PmsBookingEventEntry entry) {
        entries.put(entry.id, entry);
        saveObject(entry);
    }

    @Override
    public void deleteEntry(String entryId) {
        PmsBookingEventEntry entry = getEntry(entryId);
        entries.remove(entryId);
        deleteObject(entry);
    }

    @Override
    public PmsBookingEventEntry createEvent(String id) {
        PmsBooking result = pmsManager.getBooking(id);
        PmsBookingEventEntry entry = new PmsBookingEventEntry();
        entry.id = result.id;
        entry.title = result.registrationData.resultAdded.get("title");
        entry.shortdesc = result.registrationData.resultAdded.get("shortdesc");
        setRooms(entry, result);
        entries.put(entry.id, entry);
        saveEntry(entry);
        return entry;
    }

    @Override
    public PmsBookingEventEntry getEntry(String entryId) {
        if(entries.get(entryId) == null) {
            createEvent(entryId);
        }
        return finalize(entries.get(entryId));
    }

    @Override
    public PmsBookingEventEntry getEntryShort(String shortId) {
        for(PmsBookingEventEntry entry : entries.values()) {
            if(entry.id.startsWith(shortId)) {
                return entry;
            }
        }
        return null;
    }

    private void setRooms(PmsBookingEventEntry entry, PmsBooking result) {
        for(PmsBookingRooms room : result.rooms) {
            if(bookingEngine.getBookingItemType(room.bookingItemTypeId).addon > 0) {
                continue;
            }
            entry.dateRanges.add(room.date);
            entry.roomNames.add(bookingEngine.getBookingItem(room.bookingItemId).bookingItemName);
        }
    }

    private PmsBookingEventEntry finalize(PmsBookingEventEntry get) {
        PmsBooking booking = pmsManager.getBooking(get.id);
        setRooms(get, booking);
        return get;
    }
    
}
