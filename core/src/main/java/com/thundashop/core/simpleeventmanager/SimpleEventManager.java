/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.simpleeventmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.PageManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class SimpleEventManager extends ManagerBase implements ISimpleEventManager {

    @Autowired
    private PageManager pageManger;
    
    public HashMap<String, SimpleEvent> events = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon common : data.data) {
            if (common instanceof SimpleEvent) {
                events.put(((SimpleEvent)common).id, ((SimpleEvent)common));
            }
        }
    }
    
    @Override
    public void saveEvent(SimpleEvent simpleEvent) {
        saveObject(simpleEvent);
        events.put(simpleEvent.id, simpleEvent);
    }

    @Override
    public List<SimpleEvent> getEventsInFuture(String origninalPageId) {
        
        Date now = new Date();
        
        List<SimpleEvent> retEvents = events.values().stream()
                .filter(e -> e.date.after(now))
                .collect(Collectors.toList());
                
        retEvents.stream()
                .forEach(e -> finalize(e));
        
        return new ArrayList(retEvents.stream()
                .filter(e -> e.originalPageId != null && e.originalPageId.equals(origninalPageId))
                .sorted(SimpleEvent.getDateSorter())
                .collect(Collectors.toList())
        );
    }

    @Override
    public List<SimpleEvent> getAllEvents(String origninalPageId) {
        events.values().stream()
                .filter(e -> e.originalPageId != null && e.originalPageId.equals(origninalPageId))
                .forEach(e -> finalize(e));
        
        return events.values().stream()
                .filter(e -> e.originalPageId != null && e.originalPageId.equals(origninalPageId))
                .sorted(SimpleEvent.getDateSorter())
                .collect(Collectors.toList());
    }
    
    private void finalize(SimpleEvent event) {
        if (event.eventPageId == null || event.eventPageId.isEmpty()) {
            event.eventPageId = pageManger.createPageFromTemplatePage("simple_event_page_template").id;
            saveObject(event);
        }
    }

    @Override
    public void deleteEvent(String eventId) {
        SimpleEvent event = events.remove(eventId);
        if (event != null) {
            deleteObject(event);
        }
    }

    @Override
    public SimpleEvent getEventByPageId(String pageId) {
        events.values().stream().forEach(event -> finalize(event));
        
        return events.values().stream()
                .filter(event -> event.eventPageId != null)
                .filter(event -> event.eventPageId.equals(pageId))
                .findAny()
                .orElse(null);
    }

    @Override
    public void addUserToEvent(String pageId, String userId) {
        SimpleEvent event = getEventByPageId(pageId);
        if (event != null) {
            event.userIds.add(userId);
            saveObject(event);
        }
        
    }
}
