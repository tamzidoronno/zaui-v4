/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.common.Writing;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IEventBookingManager {
    @Editor
    @Writing
    public void createEvent(Event event);
    
    @Editor
    @Writing
    public void deleteEvent(String eventId);
    
    @Editor
    @Writing
    public void saveLocation(Location location);
    
    public List<Event> getEvents();
}
