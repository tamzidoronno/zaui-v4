/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.common.Writing;
import com.thundashop.core.usermanager.data.User;
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

    @Editor
    @Writing
    public void deleteLocation(String locationId);
    
    public List<Location> getAllLocations();
    
    public List<Event> getEvents();
    
    public Location getLocation(String locationId);
    
    public List<Event> getBookingsByPageId(String pageId, boolean showOnlyNew);
    
    public Event getEvent(String eventId);
    
    @Administrator
    public void saveEvent(Event event);
    
    @Customer
    public void bookCurrentUserToEvent(String eventId);
    
    @Editor
    public List<User> getUsersForEvent(String eventId);
    
    @Editor
    public List<User> getUsersForEventWaitinglist(String eventId);
    
    @Editor
    public void removeUserFromEvent(String eventId, String userId);
    
    @Editor
    public void addUserComment(String userId, String eventId, String comment);

    @Editor
    public void setParticipationStatus(String eventId, String userId, String status);
    
    public void addLocationFilter(String locationId);
   
    @Administrator
    public List<EventLog> getEventLog(String eventId);

    @Administrator
    public void markAsReady(String eventId);
    
    @Editor
    public void addUserToEvent(String eventId, String userId);
    
    @Editor
    public List<ReminderTemplate> getReminderTemplates(); 
    
    @Editor
    public ReminderTemplate getReminderTemplate(String id); 
    
    @Editor
    public void saveReminderTemplate(ReminderTemplate template); 
    
    @Editor
    public void deleteReminderTemplate(String templateId);
    
    @Editor
    public void sendReminder(Reminder reminder);
    
    @Editor
    public List<Reminder>  getReminders(String eventId);
    
    @Editor
    public Reminder getReminder(String reminderId);
    
    @Administrator
    public void saveCertificate(Certificate certificate);
    
    @Administrator
    public void deleteCertificate(String certificateId);
    
    @Administrator
    public List<Certificate> getCertificates();
    
    @Administrator
    public Certificate getCertificate(String certificateId);
    
    @Administrator
    public BookingItemTypeMetadata getBookingTypeMetaData(String id);
    
    @Administrator
    public void saveBookingTypeMetaData(BookingItemTypeMetadata bookingItemTypeMetadata);
    
    public List<BookingItemType> getBookingItemTypes();
    
    @Customer
    public List<Event> getMyEvents();
}
