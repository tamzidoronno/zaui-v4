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
import java.util.Map;
import javax.xml.ws.soap.Addressing;

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IEventBookingManager {
    @Editor
    @Writing
    public Event createEvent(Event event);
    
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
    
    @Editor
    public void setGroupInvoiceingStatus(String eventId, String userId, String groupId);
    
    public void addLocationFilter(String locationId);
    
    public void addTypeFilter(String bookingItemTypeId);
   
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
    public void sendDiplomas(Reminder reminder, String userid, String base64);
    
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
    
    @Editor
    public List<Event> getEventsForDay(int year, int month, int day);
    
    @Administrator
    public void moveUserToEvent(String userId, String fromEventId, String toEventId);
    
    @Editor
    public void toggleLocked(String eventId);
    
    @Editor
    public void toggleHide(String eventId);
    
    public void clearLocationFilters();
    
    @Administrator
    public List<EventStatistic> getStatisticGroupedByLocations(Date startDate, Date stopDate, List<String> groupIds, List<String> eventTypeIds);
        
    @Administrator
    public void deleteUserComment(String userId, String eventId, String commentId);

    @Administrator
    public void unCancelEvent(String eventId);
    
    @Administrator
    public void saveGroupInvoicing(InvoiceGroup invoiceGroup);
    
    @Administrator
    public List<InvoiceGroup> getInvoiceGroups(String eventId);
    
    @Administrator
    public InvoiceGroup getInvoiceGroup(String groupId);
    
    @Administrator
    public void deleteInvoiceGroup(String groupId);
    
    @Administrator
    public void addManuallyParticipatedEvent(ManuallyAddedEventParticipant man);
    
    @Customer
    public List<ManuallyAddedEventParticipant> getManuallyAddedEvents(String userId);
    
    public void deleteManullyParticipatedEvent(String id);
    
    @Administrator
    public ManuallyAddedEventParticipant getManuallyAddedEventParticipant(String id);
    
    @Customer
    public List<BookingItemType> getMandatoryCourses(String userId);
    
    @Customer
    public boolean hasCompletedMandatoryEvent(String eventTypeId, String userId);
    
    @Customer
    public boolean hasForcedMandatoryTest(String eventTypeId, String userId);
    
    @Administrator
    public void setForcedMandatoryAccess(String userId, List<String> bookingItemIds);
    
    @Customer
    public void registerEventIntrest(EventIntrest interest);
    
    @Administrator
    public List<EventIntrest> getInterests();
    
    @Administrator
    public void removeInterest(String bookingItemTypeId, String userId);
    
    @Administrator
    public List<String> getCompaniesWhereNoCanditasHasCompletedTests(List<String> testIds);
    
    public EventRequest getEventRequest(String id);
    
    public void handleEventRequest(String id, boolean accepted);
    
    @Administrator
    public boolean isWaitingForConfirmation(String eventId, String userId);
    
    public List<Event> getEventsForPdf();
    
    public void addPersonalIdToEvent(String eventId, String userId, String personalId);
    
    @Administrator
    public Map<String, String> decodePersonalIds(String eventId, String privateKey);
}
