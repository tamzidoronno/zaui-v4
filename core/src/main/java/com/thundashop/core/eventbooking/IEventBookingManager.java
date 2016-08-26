/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.EventStatistic;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.common.Writing;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
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
    
    public List<Location> getFilteredLocations();
    
    public List<Location> getActiveLocations();
    
    public List<Event> getEventsWhereEndDateBetween(Date from, Date to);
    
    public List<Event> getEvents();
    
    public List<Event> getAllEvents();
    
    public Location getLocation(String locationId);
    
    public List<Event> getBookingsByPageId(String pageId, boolean showOnlyNew);
    
    public Event getEvent(String eventId);
    
    @Administrator
    public void saveEvent(Event event);
    
    @Customer
    public void bookCurrentUserToEvent(String eventId, String source);
    
    @Editor
    public List<User> getUsersForEvent(String eventId);
    
    @Editor
    public List<User> getUsersForEventWaitinglist(String eventId);
    
    @Editor
    public void removeUserFromEvent(String eventId, String userId, boolean silent);
    
    @Editor
    public void addUserComment(String userId, String eventId, String comment);

    @Editor
    public void setParticipationStatus(String eventId, String userId, String status);
    
    public void addLocationFilter(String locationId);
   
    @Administrator
    public List<EventLog> getEventLog(String eventId);

    @Administrator
    public void markAsReady(String eventId);
    
    public void addUserToEvent(String eventId, String userId, boolean silent, String source);
    
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
    
    public Certificate getCertificateForEvent(String eventId, String userId);
    
    @Administrator
    public Certificate getCertificate(String certificateId);
    
    @Customer
    public BookingItemTypeMetadata getBookingTypeMetaData(String id);
    
    @Administrator
    public void saveBookingTypeMetaData(BookingItemTypeMetadata bookingItemTypeMetadata);
    
    public List<BookingItemType> getBookingItemTypes();
    
    @Customer
    public List<Event> getMyEvents();
    
    @Editor
    public void addExternalCertificate(String userId, String fileId, String eventId);
    
    public List<ExternalCertificate> getExternalCertificates(String userId, String eventId);
    
    @Editor
    public void deleteExternalCertificates(String userId, String fileId, String eventId);
    
    @Administrator
    public void cancelEvent(String eventId);
    
    @Customer
    public boolean canDownloadCertificate(String eventId);
    
    @Customer
    public boolean isUserSignedUpForEvent(String eventId, String userId);
    
    public void setTimeFilter(Date from, Date to);
    
    public Date getFromDateTimeFilter();
    public Date getToDateTimeFilter();
    
    public void clearFilters();
    
    @Administrator
    public void checkToSendReminders();
    
    @Administrator
    public void startScheduler(String scheduler);
    
    @Administrator
    public void markQuestBackSent(String eventId);
    
    @Editor
    public void transferUserFromWaitingToEvent(String userId, String eventId);
    
    @Editor
    public String getSource(String eventId, String userId);
    
    @Customer
    public List<Event> getEventsForUser(String userId);
    
    @Editor
    public List<Event> getEventsByType(String eventTypeId);
    
    @Editor
    public List<Event> getEventsByLocation(String locationId);

    public Event getEventByPageId(String eventId);
    
    @Administrator
    public void deleteSubLocation(String subLocationId);
    
    public BookingItemType getBookingItemTypeByPageId(String pageId);
    
    public Double getPriceForEventType(String bookingItemTypeId);
    
    @Administrator
    public void clearEventBookingManagerForAllData();
    
    @Editor
    public Double getPriceForEventTypeAndUserId(String eventId, String userId);
    
    @Administrator
    public List<EventStatistic> getStatistic(Date startDate, Date stopDate, List<String> groupIds, List<String> eventTypeIds);
}
