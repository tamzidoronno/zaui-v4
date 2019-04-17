/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;

/**
 *
 * @author boggi
 */

@GetShopApi
public interface IPmsConferenceManager {
    @Administrator
    public List<PmsConferenceItem> getAllItem(String toItem);
    @Administrator
    public void deleteItem(String itemId);
    @Administrator
    public PmsConferenceItem saveItem(PmsConferenceItem item);
    @Administrator
    public PmsConferenceItem getItem(String id);
    
    
    @Administrator
    public PmsConference saveConference(PmsConference conference);
    @Administrator
    public PmsConference getConference(String conferenceId);
    @Administrator
    public List<PmsConference> getAllConferences(PmsConferenceFilter filter);
    @Administrator
    public void deleteConference(String conferenceId);
    
    @Administrator
    public void saveConferenceEvent(PmsConferenceEvent event);
    @Administrator
    public void deleteConferenceEvent(String id);
    @Administrator
    public List<PmsConferenceEvent> getConferenceEvents(String confernceId);
    @Administrator
    public List<PmsConferenceEvent> getConferenceEventsBetweenTime(Date start, Date end);
    @Administrator
    public PmsConferenceEvent getConferenceEvent(String eventId);
    @Administrator
    public List<PmsConferenceEvent> getConferenceEventsByFilter(PmsConferenceEventFilter filter);
    
    @Administrator
    public List<PmsConferenceEventEntry> getEventEntries(String eventId);
    
    @Administrator
    public List<PmsConferenceEventEntry> getEventEntriesByFilter(PmsConferenceEventFilter filter);
    
    @Administrator
    public void deleteEventEntry(String eventEntryId);
    @Administrator
    public PmsConferenceEventEntry getEventEntry(String eventEntryId);
    @Administrator
    public void saveEventEntry(PmsConferenceEventEntry entry);
    
    @Administrator
    public List<PmsConferenceGuests> getAllGuestsForEvent(String eventId);
    
    @Administrator
    public void removeGuestFromEvent(String guestId, String eventId);
    
    @Administrator
    public void addGuestToEvent(String guestId, String eventId);
}
