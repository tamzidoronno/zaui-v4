package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsConferenceManager extends ManagerBase implements IPmsConferenceManager {
    HashMap<String, PmsConferenceItem> items = new HashMap();
    HashMap<String, PmsConference> conferences = new HashMap();
    HashMap<String, PmsConferenceEvent> conferenceEvents = new HashMap();

    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if(dataCommon instanceof PmsConferenceItem) { items.put(dataCommon.id, (PmsConferenceItem) dataCommon); }
            if(dataCommon instanceof PmsConference) { conferences.put(dataCommon.id, (PmsConference) dataCommon); }
            if(dataCommon instanceof PmsConferenceEvent) { conferenceEvents.put(dataCommon.id, (PmsConferenceEvent) dataCommon); }
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
                    item.hasSubItems = getAllItem(item.id).size() > 0;
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
    public void saveConferenceEvent(PmsConferenceEvent event) {
        saveObject(event);
        conferenceEvents.put(event.id, event);
    }

    @Override
    public void deleteConferenceEvent(String id) {
        PmsConferenceEvent conferenceevent = conferenceEvents.get(id);
        deleteObject(conferenceevent);
        conferenceEvents.remove(id);
        
        throw new RuntimeException("Please don't forget to delete all event entries as well");
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
        return result;
    }

    @Override
    public PmsConferenceEvent getConferenceEvent(String eventId) {
        return conferenceEvents.get(eventId);
    }
    
}
