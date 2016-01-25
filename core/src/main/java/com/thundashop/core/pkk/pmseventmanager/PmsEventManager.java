package com.thundashop.core.pkk.pmseventmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.pmseventmanager.PmsBookingEventEntry;
import com.thundashop.core.pmseventmanager.PmsEventFilter;
import com.thundashop.core.pmsmanager.IPmsManager;
import com.thundashop.core.pmsmanager.PmsBooking;
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
    
    @Override
    public List<PmsBookingEventEntry> getEventEntries(PmsEventFilter filter) {
        List<PmsBookingEventEntry> result = new ArrayList();
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

    private PmsBookingEventEntry createEvent(String id) {
        PmsBooking result = pmsManager.getBooking(id);
        PmsBookingEventEntry entry = new PmsBookingEventEntry();
        entry.id = result.id;
        entries.put(entry.id, entry);
        saveEntry(entry);
        return entry;
    }

    @Override
    public PmsBookingEventEntry getEntry(String entryId) {
        if(entries.get(entryId) == null) {
            createEvent(entryId);
        }
        return entries.get(entryId);
    }
    
}
