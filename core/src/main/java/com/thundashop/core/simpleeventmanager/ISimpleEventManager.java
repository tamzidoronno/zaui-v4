/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.simpleeventmanager;

import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISimpleEventManager {
    @Editor
    public void saveEvent(SimpleEvent simpleEvent);
    
    public List<SimpleEvent> getEventsInFuture(String listPageId);
    
    public List<SimpleEvent> getAllEvents(String listPageId);
    
    @Editor
    public void deleteEvent(String eventId);
    
    public SimpleEvent getEventByPageId(String pageId);
    
    public SimpleEvent getEventById(String eventId);
    
    public void addUserToEvent(String pageId, String userId);
   
}
