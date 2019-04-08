/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopApi;
import java.util.Date;
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
    public List<PmsConference> getAllConferences(PmsConferenceFilter filter);
    public void deleteConference(String conferenceId);
    
    public void saveConferenceEvent(PmsConferenceEvent event);
    public void deleteConferenceEvent(String id);
    public List<PmsConferenceEvent> getConferenceEvents(String confernceId);
    public List<PmsConferenceEvent> getConferenceEventsBetweenTime(Date start, Date end);
    public PmsConferenceEvent getConferenceEvent(String eventId);
    
    public List<PmsConferenceEventEntry> getEventEntries(String eventId);
    public void deleteEventEntry(String eventEntryId);
    public PmsConferenceEventEntry getEventEntry(String eventEntryId);
    public void saveEventEntry(PmsConferenceEventEntry entry);
}
