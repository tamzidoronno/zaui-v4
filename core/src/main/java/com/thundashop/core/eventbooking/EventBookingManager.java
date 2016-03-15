package com.thundashop.core.eventbooking;


import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.mongodb.BasicDBObject;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.CheckSendQuestBackScheduler;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ktonder
 */

@Component
@GetShopSession
public class EventBookingManager extends GetShopSessionBeanNamed implements IEventBookingManager {
    public HashMap<String, Event> events = new HashMap();
    public HashMap<String, Location> locations = new HashMap();
    public HashMap<String, ReminderTemplate> reminderTemplates = new HashMap();
    public HashMap<String, Reminder> reminders = new HashMap();
    public HashMap<String, Certificate> certificates = new HashMap();
    public HashMap<String, BookingItemTypeMetadata> bookingTypeMetaDatas = new HashMap();
    public HashMap<String, ExternalCertificate> externalCertificates = new HashMap();
    
    @Autowired
    public EventLoggerHandler eventLoggerHandler;
    
    @Autowired
    public BookingEngine bookingEngine;
    
    @Autowired
    public StoreApplicationPool applicationPool;
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public StorePool storePool;
            
    @Autowired
    public Database database;

    @Override
    public void dataFromDatabase(DataRetreived datas) {
        for (DataCommon data : datas.data) {
            if (data instanceof Event) {
                Event event = (Event)data;
                events.put(event.id, event);
            }
            
            if (data instanceof Location) {
                Location loc = (Location)data;
                locations.put(loc.id, loc);
            }
            
            if (data instanceof Reminder) {
                Reminder reminder = (Reminder)data;
                reminders.put(reminder.id, reminder);
            }
            
            if (data instanceof ReminderTemplate) {
                ReminderTemplate reminder = (ReminderTemplate)data;
                reminderTemplates.put(reminder.id, reminder);
            }
            
            if (data instanceof Certificate) {
                Certificate certificate = (Certificate)data;
                certificates.put(certificate.id, certificate);
            }
            
            if (data instanceof BookingItemTypeMetadata) {
                BookingItemTypeMetadata metaData = (BookingItemTypeMetadata)data;
                bookingTypeMetaDatas.put(metaData.id, metaData);
            }
            
            if (data instanceof ExternalCertificate) {
                ExternalCertificate externalCertificate = (ExternalCertificate)data;
                externalCertificates.put(externalCertificate.id, externalCertificate);
            }
        }
        
        createScheduler("event_questback_checked", "0 * * * *", CheckSendQuestBackScheduler.class);
    }
    
    @Override
    public void createEvent(Event event) {
        BookingItem item = bookingEngine.saveBookingItem(event.bookingItem);
        event.bookingItemId = item.id;
        saveObject(event);
        events.put(event.id, event);
        log("EVENT_CREATED", event, null);
    }

    @Override
    public List<Event> getEvents() {
        List<Event> retEvents = new ArrayList(events.values());
        
        retEvents = retEvents.stream()
                .filter(o -> isVisibleForGroup(o))
                .collect(Collectors.toList());
        
        sortEventsByDate(retEvents);
        retEvents = cloneAndFinalize(retEvents);
        
        return filterByTimeFilter(retEvents);
    }

    private void sortEventsByDate(List<Event> retEvents) {
        Collections.sort(retEvents, (Event o1, Event o2) -> o1.days.get(0).startDate.compareTo(o2.days.get(0).startDate));
    }

    private List<Event> cloneAndFinalize(List<Event> events) {
        List<Event> retEvents = new ArrayList();
        
        List<String> locationFilters = getLocationFilters();
        if (!locationFilters.isEmpty()) {
            events = filterList(events, locationFilters);
        }
        
        for (Event event : events) {
            retEvents.add(finalize(event));
        }
        
        return retEvents;
    }

    private Event finalize(Event event) {
        event.bookingItem = bookingEngine.getBookingItem(event.bookingItemId);
        
        if (event.bookingItem != null) {
            event.bookingItemType = bookingEngine.getBookingItemType(event.bookingItem.bookingItemTypeId);
        }
        
        event.location = getLocationBySubLocationId(event.subLocationId);
        event.subLocation = getSubLocation(event.subLocationId);
        
        event.setMainDates(); 
        if (event.bookingItem != null) {
            event.eventPage = "?page="+event.bookingItem.pageId+"&eventId="+event.id;
        }
        
        event.isInFuture = isInFuture(event);
        
        if (event.markedAsReady || !isInFuture(event) || event.bookingItem == null || event.bookingItem.isFull || event.bookingItem.freeSpots < 1 || event.isCanceled) {
            event.canBook = false;
        } else {
            event.canBook = true;
        }
        
        event.price = getPrice(event);
        
        return event;
    }
    
    private void log(String action, Event event, Object additional) {
        EventLog logEntry = new EventLog();
        logEntry.action = action;
        
        if (event != null) {
            logEntry.eventId = event.id;
        }
        
        if (getSession().currentUser != null) {
            logEntry.doneBy = getSession().currentUser.id;
        }
        
        if (action.equals("EVENT_UPDATED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (Event)additional);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_REMOVED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (User)additional, false);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_ADDED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (User)additional, true);
            saveObject(logEntry);
        }
        
        if (action.equals("EVENT_CANCELED")) {
            logEntry.comment = "Event marked as canceled";
            saveObject(logEntry);
        }
        
        if (action.equals("REMINDER_SENT")) {
            logEntry.comment = "Reminder sent, type: " + additional;
            saveObject(logEntry);
        }
        
        if (action.equals("MARK_AS_READY")) {
            logEntry.comment = "Event is marked as ready";
            saveObject(logEntry);
        }
        
        if (action.equals("EVENT_CREATED")) {
            logEntry.comment = "Event created";
            saveObject(logEntry);
        }
    }

    @Override
    public void deleteEvent(String eventId) {
        Event event = events.get(eventId);
        if (event != null && event.bookingItemId != null && !event.bookingItemId.isEmpty()) {
            bookingEngine.deleteBookingItem(event.bookingItemId);
        }
        
        events.remove(eventId);
        deleteObject(event);
        
    }

    @Override
    public void saveLocation(Location location) {
        if (location.id == null || location.id.isEmpty())
            log("LOCATION_CREATED", null, location);
        else 
            log("LOCATION_CHANGED", null, location);
        
        saveObject(location);
        locations.put(location.id, location);
    }

    @Override
    public List<Location> getAllLocations() {
        for (Location loc : locations.values()) {
            finalize(loc);
        }
        
        return new ArrayList(locations.values());
    }

    @Override
    public Location getLocation(String locationId) {
        return locations.get(locationId);
    }

    @Override
    public void deleteLocation(String locationId) {
        // TODO - check if location is in use somewhere.
        Location location = locations.remove(locationId);
        if (location != null)
            deleteObject(location);
    }

    public Location getLocationBySubLocationId(String subLocationId) {
        for (Location loc : locations.values()) {
            for (SubLocation subLoc : loc.locations) {
                if (subLoc.id.equals(subLocationId)) {
                    return loc;
                }
            }
        }
        
        return null;
    }

    @Override
    public List<Event> getBookingsByPageId(String pageId, boolean showOnlyNew) {
        BookingItemType bookingItemType = bookingEngine.getBookingItemTypes()
                .stream()
                .filter(o -> o.pageId.equals(pageId))
                .findFirst()
                .orElse(null);
        
        if (bookingItemType == null) {
            return new ArrayList();
        }
        
        List<BookingItem> items = bookingEngine.getBookingItemsByType(bookingItemType.id);
        
        List<Event> retEvents = new ArrayList();
        for (BookingItem item : items) {
            retEvents.addAll(events.values().stream()
                .filter(event -> event.bookingItemId.equals(item.id))
                .filter(event -> showOnlyNew && isInFuture(event))
                .collect(Collectors.toList()));
        }
        
        return cloneAndFinalize(retEvents);
    }

    private boolean isInFuture(Event event) {
        boolean isInfure = false;
        for (Day day : event.days) {
            if (day.isInFuture()) {
                isInfure = true;
            }
        }
        
        return isInfure;    
    }

    private SubLocation getSubLocation(String subLocationId) {
        for (Location loc : locations.values()) {
            for (SubLocation subLoc : loc.locations) {
                if (subLoc.id.equals(subLocationId)) {
                    return subLoc;
                }
            }
        }
        
        return null;
    }

    @Override
    public Event getEvent(String eventId) {
        Event event = events.get(eventId);
        if (event != null) {
            return finalize(event);
        }
        
        return null;
    }

    @Override
    public void saveEvent(Event event) {
        Event inMemory = getEvent(event.id);
        event.bookingItem.id = inMemory.bookingItemId;
        BookingItem item = bookingEngine.saveBookingItem(event.bookingItem);

        event.bookingItem = item;
        event.bookingItemId = item.id;
        
        events.put(event.id, event);
        saveObject(event);
        
        log("EVENT_UPDATED", inMemory, event);
    }

    @Override
    public void bookCurrentUserToEvent(String eventId) {
        Event event = getEvent(eventId);
        
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        AddUserToEvent(event, getSession().currentUser);
    }   

    private void AddUserToEvent(Event event, User user) {
        List<Booking> alreadyBooked = bookingEngine.getAllBookingsByBookingItem(event.bookingItem.id);
        boolean userdBooked = alreadyBooked.stream()
                .filter( o -> o.userId != null && o.userId.equals(user.id))
                .count() > 0;
       
        if (userdBooked) {
            return;
        }
        
        Booking booking = createBooking(event, user);
        List<Booking> bookings = new ArrayList();
        bookings.add(booking);
        bookingEngine.addBookings(bookings);
        sendUserAddedToEventNotifications(user, event);
        log("USER_ADDED", event, user);
    }   

    private Booking createBooking(Event event, User user) {
        Booking booking = new Booking();
        booking.bookingItemId = event.bookingItemId;
        booking.bookingItemTypeId = event.bookingItemType.id;
        booking.needConfirmation = false;
        booking.userId = user.id;
        return booking;
    }

    @Override
    public List<User> getUsersForEvent(String eventId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        return bookingEngine.getAllBookingsByBookingItem(event.bookingItemId).stream()
                .map(o -> userManager.getUserById(o.userId))
                .collect(Collectors.toList()); 
    }

    @Override
    public List<User> getUsersForEventWaitinglist(String eventId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeUserFromEvent(String eventId, String userId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        Booking booking = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId).stream()
            .filter( o -> o.userId.equals(userId))
            .findAny()
            .orElse(null);
        
        User user = userManager.getUserById(userId);
        if (booking != null) {
            bookingEngine.deleteBooking(booking.id);
            log("USER_REMOVED", event, user);
        }
        
        sendRemovedUserFromEventNotification(user, event);
    }

    @Override
    public void addUserComment(String userId, String eventId, String comment) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        UserComment ucomment = new UserComment();
        ucomment.comment = comment;
        ucomment.userId = userId;
        ucomment.addedByUserId = getSession().currentUser.id;
        
        List<UserComment> comments = event.comments.get(userId);
        if (comments == null) {
            comments = new ArrayList();
            event.comments.put(userId, comments);
        }
        
        comments.add(ucomment);
        saveObject(event);
        log("Comment added", event, comment);
    }

    @Override
    public void addLocationFilter(String locationId) {
        List<String> sessionFilters = getLocationFilters();
        if (sessionFilters.contains(locationId)) {
            sessionFilters.remove(locationId);
        } else {
            sessionFilters.add(locationId);
        }
    }
    
    private List<String> getLocationFilters() {
        List<String> sessionFilters = (List<String>) getSession().get("sessionfilters");
        
        if (sessionFilters == null) {
            sessionFilters = new ArrayList();
            getSession().put("sessionfilters", sessionFilters);
        }
        
        return sessionFilters;
    }

    private List<Event> filterList(List<Event> events, List<String> locationFilters) {
        List<Event> retEvents = new ArrayList();
        
        for (Event event : events) {
            finalize(event);
            if (locationFilters.contains(event.location.id)) {
                retEvents.add(event);
            }
        }
        
        return retEvents;
    }

    private void finalize(Location loc) {
        loc.isFiltered = getLocationFilters().contains(loc.id);
    }

    @Override
    public void setParticipationStatus(String eventId, String userId, String status) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        event.participationStatus.put(userId, status);
        saveObject(event);
    }

    @Override
    public List<EventLog> getEventLog(String eventId) {
        BasicDBObject query = new BasicDBObject();
        query.put("className", EventLog.class.getCanonicalName());
        query.put("eventId", eventId);
        
        List<DataCommon> logEntries = database.query(EventBookingManager.class.getSimpleName()+"_"+getName(), storeId, query);
        List<EventLog> logToReturn = logEntries.stream().map(o -> (EventLog)o).collect(Collectors.toList());
        
        Collections.sort(logToReturn, (EventLog o1, EventLog o2) -> {
            return o2.rowCreatedDate.compareTo(o1.rowCreatedDate);
        });
        return logToReturn;
    }

    @Override
    public void markAsReady(String eventId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        event.markedAsReady = true;
        saveObject(event);
        log("MARK_AS_READY", event, null);
    }

    @Override
    public void addUserToEvent(String eventId, String userId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        if (user != null) {
            AddUserToEvent(event, user);
        }
    }

    @Override
    public List<ReminderTemplate> getReminderTemplates() {
        return new ArrayList(reminderTemplates.values());
    }

    @Override
    public void saveReminderTemplate(ReminderTemplate template) {
        saveObject(template);
        reminderTemplates.put(template.id, template);
    }
    
    @Override
    public void deleteReminderTemplate(String templateId) {
        ReminderTemplate template = reminderTemplates.remove(templateId);
        
        if (template != null) {
            deleteObject(template);
        }
    }

    @Override
    public ReminderTemplate getReminderTemplate(String id) {
        return reminderTemplates.get(id);
    }

    @Override
    public void sendReminder(Reminder reminder) {
        Event event = getEvent(reminder.eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        for (String userId : reminder.userIds) {
            User user = userManager.getUserById(userId);
            if (user != null) {
                if (reminder.type.equals("sms")) {
                    sendReminderSms(reminder.content, user, event, reminder.smsMessageId);
                } else {
                    sendReminderMail(reminder.content, reminder.subject, user, event, reminder.userIdMessageId, reminder.userIdInvoiceMessageId);
                }
            }
        }
        
        saveObject(reminder);
        reminders.put(reminder.id, reminder);
        log("REMINDER_SENT", event, reminder.type);
    }

    private void sendReminderMail(String conent, String subject, User user, Event event, HashMap<String, String> userIdMessageId, HashMap<String, String> userIdInvoiceMessageId) {
        String email = storePool.getStore(storeId).configuration.emailAdress;
        String content = formatText(conent, user, event);
        subject = formatText(subject, user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String messageId = messageManager.sendMail(user.emailAddress, user.fullName, subject, content, email, "");
            if (userIdInvoiceMessageId != null) {
                userIdMessageId.put(user.id, messageId);
            }
        }
        
        if (user.companyObject != null && user.companyObject.invoiceEmail != null && !user.companyObject.invoiceEmail.isEmpty()) {
            String messageId = messageManager.sendMail(user.companyObject.invoiceEmail, user.fullName, subject, content, email, "");
            if (userIdInvoiceMessageId != null) {
                userIdInvoiceMessageId.put(user.id, messageId);
            }
        }
    }


    @Override
    public List<Reminder> getReminders(String eventId) {
        return reminders.values().stream()
                .filter(o -> o.eventId.equals(eventId))
                .collect(Collectors.toList());
    }

    @Override
    public Reminder getReminder(String reminderId) {
        return reminders.get(reminderId);
    }

    private String sendReminderSms(String content, User user, Event event, HashMap<String, String> smsMessageId) {
        content = formatText(content, user, event);
        if (user.cellPhone != null && !user.cellPhone.isEmpty()) {
            String smsId = messageManager.sendSms("clickatell", user.cellPhone, content, user.prefix);
            if (smsMessageId != null) {
                smsMessageId.put(user.id, smsId);
            }
            return smsId;
        }
        
        return "";
    }

    @Override
    public void saveCertificate(Certificate certificate) {
        saveObject(certificate);
        certificates.put(certificate.id, certificate);
    }

    @Override
    public void deleteCertificate(String certificateId) {
        Certificate certificate = certificates.remove(certificateId);
        if (certificate != null) {
            deleteObject(certificate);
        }
    }

    @Override
    public List<Certificate> getCertificates() {
        return new ArrayList(certificates.values());
    }

    @Override
    public Certificate getCertificate(String certificateId) {
        return certificates.get(certificateId);
    }

    @Override
    public BookingItemTypeMetadata getBookingTypeMetaData(String id) {
        BookingItemTypeMetadata res = bookingTypeMetaDatas.values().stream()
                .filter(o -> o.bookingItemTypeId.equals(id))
                .findFirst()
                .orElse(null);
        return finalizeMetaData(res, id);
    }

    @Override
    public void saveBookingTypeMetaData(BookingItemTypeMetadata bookingItemTypeMetadata) {
        saveObject(bookingItemTypeMetadata);
        bookingTypeMetaDatas.put(bookingItemTypeMetadata.id, bookingItemTypeMetadata);
    }

    private BookingItemTypeMetadata finalizeMetaData(BookingItemTypeMetadata data, String id) {
        if (id == null || id.isEmpty()) {
            return null;
        }
        
        if (data == null) {
            data = new BookingItemTypeMetadata();
            data.bookingItemTypeId = id;
            saveBookingTypeMetaData(data);
        }
        
        List<Group> groups = userManager.getAllGroups();
        for (Group group : groups) {
            if (data.certificateIds.get(group.id) == null) {
                data.certificateIds.put(group.id, new ArrayList());
            }
            if (data.groupPrices.get(group.id) == null) {
                data.groupPrices.put(group.id, -1D);
            }
            if (data.visibleForGroup.get(group.id) == null) {
                data.visibleForGroup.put(group.id, true);
            }
        }
        
        return data;
    }

    private boolean isVisibleForGroup(Event o) {
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(o);
        
        if (getSession() != null || getSession() == null) {
            return metaData.publicVisible;
        }
        
        if (getSession().currentUser.groups.get(0) == null) {
            return true;
        }
        
        return metaData.visibleForGroup.get(getSession().currentUser.groups.get(0));
    }

    private BookingItemTypeMetadata getBookingTypeMetaData(Event event) throws NullPointerException {
        BookingItem item = bookingEngine.getBookingItem(event.bookingItemId);
        if (item == null) {
            throw new NullPointerException("There is an event that is connected to a null-bookingitem. That should not be possible.");
        }
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(item.bookingItemTypeId);
        return metaData;
    }

    @Override
    public List<BookingItemType> getBookingItemTypes() {
        return bookingEngine.getBookingItemTypes()
                .stream()
                .filter(o -> isTypeAvailble(o))
                .collect(Collectors.toList());
    }

    private boolean isTypeAvailble(BookingItemType o) {
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(o.id);
        
        if (getSession() != null && getSession() == null) {
            return metaData.publicVisible;
        }
        
        if (getSession().currentUser.groups.get(0) == null) {
            return true;
        }
        
        return metaData.visibleForGroup.get(getSession().currentUser.groups.get(0));
        
    }

    private Double getPrice(Event event) {
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(event);
        
        if (getSession() == null || getSession().currentUser == null || getSession().currentUser.groups == null) {
            return metaData.publicPrice;
        }
        
        if (getSession().currentUser.groups.isEmpty()) {
            return metaData.publicPrice;
        }
        
        return metaData.groupPrices.get(getSession().currentUser.groups.get(0));
    }

    @Override
    public List<Event> getMyEvents() {
        List<Event> rets = bookingEngine.getAllBookings().stream()
                .filter(booking -> booking.userId != null && booking.userId.equals(getSession().currentUser.id))
                .map( booking -> getEventByBooking(booking))
                .collect(Collectors.toList());
        
        
        sortEventsByDate(rets);
        return cloneAndFinalize(rets);
    }

    private Event getEventByBooking(Booking booking) {
        String itemId = booking.bookingItemId;
        return events.values().stream()
                .filter(event -> event.bookingItemId.equals(itemId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void addExternalCertificate(String userId, String fileId, String eventId) {
        ExternalCertificate certificate = new ExternalCertificate();
        certificate.userId = userId;
        certificate.fileId = fileId;
        certificate.eventId = eventId;
        saveObject(certificate);
        externalCertificates.put(certificate.id, certificate);
    }

    @Override
    public List<ExternalCertificate> getExternalCertificates(String userId, String eventId) {
        User user = userManager.getUserById(userId);
        if (user == null) {
            throw new ErrorException(26);
        }
        
        userManager.checkUserAccess(user);
        
        List<ExternalCertificate> ret = externalCertificates.values()
                .stream()
                .filter(o -> o.userId.equals(userId))
                .filter(o -> o.eventId.equals(eventId))
                .collect(Collectors.toList());
        
        return ret;
    }

    @Override
    public void deleteExternalCertificates(String userId, String fileId, String eventId) {
        ExternalCertificate cert = getExternalCertificates(userId, eventId).stream()
                .filter(o -> o.fileId.equals(fileId))
                .findFirst()
                .orElse(null);
        
        if (cert  != null) {
            externalCertificates.remove(cert.id);
            deleteObject(cert);
            
        }
    }

    @Override
    public boolean canDownloadCertificate(String eventId) {
        Event event = getEvent(eventId);
        if (event == null)
            return false;
        
        String status = event.participationStatus.get(getSession().currentUser.id);
        
        if (status == null)
            return false;
        
        if (status.equals("participated") || status.equals("participated_free"))
            return true;
        
        return false;
    }

    @Override
    public boolean isUserSignedUpForEvent(String eventId, String userId) {
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            return false;
        }
        
        userManager.checkUserAccess(user);
        
        Event event = getEvent(eventId);
        if (event == null) {
            return false;
        }
        
        Booking res = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId)
                .stream()
                .filter(o -> o.userId.equals(userId))
                .findAny()
                .orElse(null);
        
        return res != null;
    }

    @Override
    public void cancelEvent(String eventId) {
        Event event = getEvent(eventId);
        
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        List<Booking> bookings = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId);
        
        for (Booking booking : bookings) {
            User user = userManager.getUserById(booking.userId);
            sendCanceledNotification(user, event);
        }
        
        log("EVENT_CANCELED", event, null);
        event.isCanceled = true;
        saveObject(event);
    }

    @Override
    public void setTimeFilter(Date from, Date to) {
        getSession().put("from", from);
        getSession().put("to", to);
    }

    private List<Event> filterByTimeFilter(List<Event> retEvents) {
        Date from = (Date)getSession().get("from");
        Date to = (Date)getSession().get("to");
        Date today = new Date();
        
        if (from == null || to == null) {
            return retEvents.stream()
                    .filter(o -> o.mainEndDate != null && o.mainEndDate.after(today))
                    .collect(Collectors.toList());
        }
        
        if (from != null && to != null) {
            return retEvents.stream()
                    .filter(o -> o.mainStartDate != null && o.mainStartDate.after(from) && o.mainStartDate.before(to))
                    .collect(Collectors.toList());
        }
        
        return retEvents;
    }

    @Override
    public Date getFromDateTimeFilter() {
        return (Date)getSession().get("from");
    }

    @Override
    public Date getToDateTimeFilter() {
        return (Date)getSession().get("to");
    }

    @Override
    public void clearFilters() {    
        getSession().put("sessionfilters", new ArrayList());
        getSession().put("from", null);
        getSession().put("to", null);
    }

    @Override
    public List<Location> getFilteredLocations() {
        Set<Location> retLocs = new HashSet();
        List<Event> events = getEvents();
        for (Event event : events) {
            finalize(event.location);
            retLocs.add(event.location);
        }
        
        return new ArrayList(retLocs);
    }

    private void sendUserAddedToEventNotifications(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("signupemail").equals("true")) {
            sendSignupEmail(settingsApp, user, event);
        }
        
        if (settingsApp.getSetting("signupsms").equals("true")) {
            sendSms(settingsApp, user, event);
        }
    }

    private Application getSettingsApplication() {
        return applicationPool.getApplication("bd751f7e-5062-4d0d-a212-b1fc6ead654f");
    }

    private void sendSignupEmail(Application settingsApp, User user, Event event) {
        String subject = formatText(settingsApp.getSetting("signup_subject"), user, event);
        String content = formatText(settingsApp.getSetting("signup_mailcontent"), user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            logEventEntry(event, "SIGNUP_MAIL_SENT", "Signupmail sent to user " + user.fullName + ", email: " + user.emailAddress, mailId);
        } else {
            logEventEntry(event, "SIGNUP_MAIL_SENT_FAILED", "Couldnot send mail to user " + user.fullName + ", no email registered.", "");
        }
    }

    private String formatText(String text, User user, Event event) {
        text = text.replace("{User.Name}", checkNull(user.fullName));
        text = text.replace("{User.Email}", checkNull(user.emailAddress));
        text = text.replace("{Event.Name}", checkNull(event.bookingItemType.name));
        return text;
    }

    private String checkNull(String text) {
        if (text == null) {
            return "";
        }
        
        return text;
    }

    private void sendSms(Application settingsApp, User user, Event event) {
        String content = formatText(settingsApp.getSetting("signup_sms_content"), user, event);
        String prefix = user.prefix;
        String phoneNumber = user.cellPhone;
        String storeName = getStoreName();
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String res = messageManager.sendSms("clickatell", phoneNumber, content, prefix, storeName);
            logEventEntry(event, "SMS_SIGNUP_SENT", "Signup sms sent to " + user.fullName, res);
        } else {
            logEventEntry(event, "SMS_SIGNUP_SENT_FAILED", "Failed to send signup sms to use " + user.fullName, "");
        }
    }

    private void sendRemovedUserFromEventNotification(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("removedemail").equals("true")) {
            sendRemovedEmail(settingsApp, user, event);
        }
        
        if (settingsApp.getSetting("removedsms").equals("true")) {
            sendRemovedSms(settingsApp, user, event);
        }
    }

    private void sendRemovedEmail(Application settingsApp, User user, Event event) {
        String subject = formatText(settingsApp.getSetting("removed_mail_subject"), user, event);
        String content = formatText(settingsApp.getSetting("removed_mailcontent"), user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            logEventEntry(event, "REMOVED_MAIL_SENT", "Removed mail sent to user " + user.fullName + ", email: " + user.emailAddress, mailId);
        } else {
            logEventEntry(event, "SIGNUP_MAIL_SENT_FAILED", "Couldnot send remove mail to user " + user.fullName + ", no email registered.", "");
        }
    }

    private void sendRemovedSms(Application settingsApp, User user, Event event) {
        String content = formatText(settingsApp.getSetting("removed_sms_content"), user, event);
        String prefix = user.prefix;
        String phoneNumber = user.cellPhone;
        String storeName = getStoreName();
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String res = messageManager.sendSms("clickatell", phoneNumber, content, prefix, storeName);
            logEventEntry(event, "SMS_REMOVED_SENT", "Sms removed notification sent to user " + user.fullName + " cellphone, " + phoneNumber + ", prefix: " + prefix, res);
        } else {
            logEventEntry(event, "SMS_REMOVED_SENT_FAILED", "Failed to send removed sms to user " + user.fullName, "");
        }
    }

    private void sendCanceledNotification(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("canceleventmail").equals("true")) {
            sendCanceledEmail(settingsApp, user, event);
        }
        
        if (settingsApp.getSetting("canceleventsms").equals("true")) {
            sendCanceledSms(settingsApp, user, event);
        }
    }

    private void sendCanceledSms(Application settingsApp, User user, Event event) {
        String subject = formatText(settingsApp.getSetting("event_canceled_mail_subject"), user, event);
        String content = formatText(settingsApp.getSetting("event_canceled_mail_content"), user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            logEventEntry(event, "CANCELED_MAIL_SENT", "Canceled mail sent to user " + user.fullName + ", email: " + user.emailAddress, mailId);
        } else {
            logEventEntry(event, "CANCELED_MAIL_SENT_FAILED", "Couldnot send canceled mail to user " + user.fullName + ", no email registered.", "");
        }
    }

    private void sendCanceledEmail(Application settingsApp, User user, Event event) {
        String content = formatText(settingsApp.getSetting("event_canceled_sms_conent"), user, event);
        String prefix = user.prefix;
        String phoneNumber = user.cellPhone;
        String storeName = getStoreName();
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String res = messageManager.sendSms("clickatell", phoneNumber, content, prefix, storeName);
            logEventEntry(event, "SMS_CANCELED_SENT", "Sms canceled notification sent to user " + user.fullName + " cellphone, " + phoneNumber + ", prefix: " + prefix, res);
        } else {
            logEventEntry(event, "SMS_CANCELED_SENT_FAILED", "Failed to send canceled sms to user " + user.fullName + ", phonenumber: " + phoneNumber + ", prefix: " + prefix, "");
        }
    }

    @Override
    public void startScheduler(String scheduler) {
        stopScheduler("event_booking_scheduler");
        createScheduler("event_booking_scheduler", scheduler, EventBookingScheduler.class);
    }

    @Override
    public void checkToSendReminders() {
        events.values().stream()
                .forEach(o -> finalize(o));
        
        events.values().stream()
                .filter(o -> o.mainStartDate != null)
                .filter(o -> nowIsBetween(o.mainStartDate))
                .forEach(o -> sendReminder(o));
    }

    private boolean nowIsBetween(Date mainStartDate) {
        Application settings = getSettingsApplication();
        String inAdvance = settings.getSetting("automatic_reminder_template_frequence");
        
        if (inAdvance == null || inAdvance.isEmpty()) {
            return false;
        }
        
        int days = Integer.parseInt(inAdvance);
        
        Date now = new Date();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(mainStartDate);
        cal.add(Calendar.DAY_OF_YEAR, -days);
        Date afterThisDate = cal.getTime();
        
        return now.getTime() >= afterThisDate.getTime() && now.getTime() <= mainStartDate.getTime();
    }

    private void sendReminder(Event o) {
        if (o.isCanceled) {
            return;
        }
        
        if (o.smsReminderSent && o.mailReminderSent) {
            return;
        }
        
        Application settings = getSettingsApplication();
        
        if (!o.smsReminderSent && settings.getSetting("reminder_on_sms_activated").equals("true")) {
            sendAutomaticSmsReminder(o, settings);
            o.smsReminderSent = true;
        }
        
        if (!o.mailReminderSent && settings.getSetting("reminder_on_email_activated").equals("true")) {
            sendAutomaticMailReminder(o, settings);
            o.mailReminderSent = true;
        }
        
        saveObject(o);
    }

    private void sendAutomaticSmsReminder(Event event, Application settings) {
        String templateId = settings.getSetting("automatic_reminder_template_sms");
        ReminderTemplate template = getReminderTemplate(templateId);
        if (template == null) {
            logEventEntry(event, "AUOTMATIC_REMINDER_SMS_FAILED", "Could not send automatic sms reminder, the template does not exists", "");
            return;
        }
        
        List<Booking> bookings = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId);
        for (Booking booking : bookings) {
            User user = userManager.getUserById(booking.userId);
            String smsId = sendReminderSms(template.content, user, event, null);
            logEventEntry(event, "AUTOMATIC_REMIDER_SMS_SENT", "Sent reminder to user " + user.fullName, smsId);
        }
    }
    
    private void sendAutomaticMailReminder(Event event, Application settings) {
        String templateId = settings.getSetting("automatic_reminder_template_email");
        ReminderTemplate template = getReminderTemplate(templateId);
        if (template == null) {
            logEventEntry(event, "AUOTMATIC_REMINDER_MAIL_FAILED", "Could not send automatic mail reminder, the template does not exists", "");
            return;
        }
        
        List<Booking> bookings = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId);
        for (Booking booking : bookings) {
            User user = userManager.getUserById(booking.userId);
            sendMailReminder(user, event, template, null);
            
            if (user.companyObject != null && user.companyObject.invoiceAddress != null && !user.companyObject.invoiceEmail.equals(user.emailAddress)) {
                sendMailReminder(user, event, template, user.companyObject.invoiceEmail);
            }
        }
    }

    private void sendMailReminder(User user, Event event, ReminderTemplate template, String emailAddress) throws ErrorException {
        String subject = event.bookingItemType.name;
        String content = formatText(template.content, user, event);
        
        String emailToUser = user.emailAddress;
        if (emailAddress != null) {
            emailToUser = emailAddress;
        }
        
        if (emailToUser != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(emailToUser, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            String logMsg = "Reminder has been sent to " + user.fullName + ", email: " + emailToUser;
            logEventEntry(event, "AUOTMATIC_REMINDER_MAIL_SENT", logMsg, mailId);
        } else {
            logEventEntry(event, "AUOTMATIC_REMINDER_MAIL_FAILED", "Could not send reminder to " + user.fullName + ", missing emailaddress", "");
        }
    }

    private void logEventEntry(Event event, String action, String comment, String additional) throws ErrorException {
        EventLog log = new EventLog();
        log.action = action;
        log.doneBy = getSession().currentUser.id;
        log.eventId = event.id;
        log.comment = comment;
        log.additional = additional;
        saveObject(log);
    }

    @Override
    public List<Event> getEventsWhereEndDateBetween(Date from, Date to) {
        events.values().stream().forEach(o -> o.setMainDates());
        
        List<Event> ret = events.values().stream()
                .filter(o -> o.mainEndDate != null && o.mainEndDate.after(from) && o.mainEndDate.before(to))
                .collect(Collectors.toList());
        
        ret.stream().forEach(o -> finalize(o));
        return ret;
    }

    @Override
    public void markQuestBackSent(String eventId) {
        Event event = events.get(eventId);
        if (event != null) {
            event.questBackSent = true;
            saveObject(event);
            logEventEntry(event, "EVENT_QUESTBACK_SENT", "QuestBack has been sent for event", "");
        }
    }

}