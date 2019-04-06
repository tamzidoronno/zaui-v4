/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author boggi
 */

@GetShopApi
public interface IPmsConferenceManager {
    public List<PmsConferenceItem> getAllItem(String toItem);
    
    public void deleteItem(String itemId);
    public PmsConferenceItem saveItem(PmsConferenceItem item);
    public PmsConferenceItem getItem(String id);
    
    public PmsConference saveConference(PmsConference conference);
    public PmsConference getConference(String conferenceId);
    public void deleteConference(String conferenceId);
    
    public void saveConferenceEvent(PmsConferenceEvent event);
    public void deleteConferenceEvent(String id);
    public List<PmsConferenceEvent> getConferenceEvents(String confernceId);
    public PmsConferenceEvent getConferenceEvent(String eventId);
    
}
