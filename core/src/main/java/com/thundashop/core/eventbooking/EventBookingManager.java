package com.thundashop.core.eventbooking;


import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.mongodb.BasicDBObject;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.Booking;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.bookingengine.data.EventStatistic;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.encryption.EncryptionManager;
import com.thundashop.core.scormmanager.Scorm;
import com.thundashop.core.scormmanager.ScormManager;
import com.thundashop.core.scormmanager.ScormPackage;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Company;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.utils.UtilManager;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    public HashMap<String, WaitingListBooking> waitingListBookings = new HashMap();
    public HashMap<String, InvoiceGroup> groupInvoicing = new HashMap();
    public HashMap<String, ManuallyAddedEventParticipant> manualEvents = new HashMap();
    public HashMap<String, ForcedParcipated> forcedParticipated = new HashMap();
    public HashMap<String, EventIntrest> eventInterests = new HashMap();
    public HashMap<String, EventRequest> requests = new HashMap();
    
    @Autowired
    public EventLoggerHandler eventLoggerHandler;
    
    @Autowired
    public BookingEngine bookingEngine;
    
    @Autowired
    public StoreApplicationPool applicationPool;
    
    @Autowired
    public UserManager userManager;
    
    @Autowired
    public ProMeisterMessageManager messageManager;
    
    @Autowired
    public StorePool storePool;
            
    @Autowired
    public Database database;

    @Autowired
    public UtilManager utilManager;
    
    @Autowired
    public ScormManager scormManager;
    
    @Autowired
    private EncryptionManager encryptionManager;
    
    @Override
    public void dataFromDatabase(DataRetreived datas) {
        for (DataCommon data : datas.data) {
            if (data instanceof ManuallyAddedEventParticipant) {
                ManuallyAddedEventParticipant event = (ManuallyAddedEventParticipant)data;
                manualEvents.put(event.id, event);
            }
            
            if (data instanceof Event) {
                Event event = (Event)data;
                events.put(event.id, event);
            }
            
            if (data instanceof ForcedParcipated) {
                ForcedParcipated forced = (ForcedParcipated)data;
                forcedParticipated.put(forced.userId, forced);
            }
            
            if (data instanceof EventRequest) {
                EventRequest request = (EventRequest)data;
                requests.put(request.id, request);
            }
            
            if (data instanceof EventIntrest) {
                EventIntrest eventInterest = (EventIntrest)data;
                eventInterests.put(eventInterest.id, eventInterest);
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
            
            if (data instanceof InvoiceGroup) {
                InvoiceGroup invoiceGroup = (InvoiceGroup)data;
                groupInvoicing.put(invoiceGroup.id, invoiceGroup);
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
            
            if (data instanceof WaitingListBooking) {
                WaitingListBooking waitingListBooking = (WaitingListBooking)data;
                waitingListBookings.put(waitingListBooking.id, waitingListBooking);
            }
        }
        
        if (events.isEmpty()) {
            return;
        }
        
        cleanBookingItemsThatDoesNotExsist();
//        createScheduler("event_questback_checked", "0 * * * *", CheckSendQuestBackScheduler.class);
        stopScheduler("event_booking_scheduler");
        stopScheduler("event_questback_checked");
    }
    
    @Override
    public Event createEvent(Event event) {
        BookingItem item = bookingEngine.saveBookingItem(event.bookingItem);
        event.bookingItemId = item.id;
        saveObject(event);
        events.put(event.id, event);
        log("EVENT_CREATED", event, null);
        event = finalize(event);
        return event;
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
    
    @Override
    public List<Event> getEventsForPdf() {
        if (getSession() == null || getSession().currentUser == null || getSession().currentUser.type < 100) {
            return getEvents();
        }
        
        
        List<Event> retEvents = new ArrayList(events.values());
        
        retEvents = retEvents.stream()
                .filter(o -> !o.isHidden)
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
        
        List<String> eventTypesFilter = getEventTypesFilter();
        if (!eventTypesFilter.isEmpty()) {
            events = filterListEventTypes(events, eventTypesFilter);
        }
        
        for (Event event : events) {
            retEvents.add(finalize(event));
        }
        
        return retEvents;
    }

    private Event shortFinalize(Event event) {
        setBookingItem(event, true);
        
        if (event.bookingItem != null) {
            event.bookingItemType = bookingEngine.getBookingItemType(event.bookingItem.bookingItemTypeId);
        }
        
        return event;
    }
    
    private Event finalize(Event event) {
        bookingEngine.removeBookingsWhereUserHasBeenDeleted(event.bookingItemId);
        setBookingItem(event, true);
        
        if (event.bookingItem != null) {
            event.bookingItemType = bookingEngine.getBookingItemType(event.bookingItem.bookingItemTypeId);
        }
        
        event.location = getLocationBySubLocationId(event.subLocationId);
        event.subLocation = getSubLocation(event.subLocationId);
        
        if (event.bookingItem != null) {
            event.bookingItemType = bookingEngine.getBookingItemType(event.bookingItem.bookingItemTypeId);
        }
        
        event.location = getLocationBySubLocationId(event.subLocationId);
        event.subLocation = getSubLocation(event.subLocationId);
        
        event.setMainDates(); 
        if (event.bookingItem != null) {
            event.eventPage = "?page="+event.bookingItem.pageId+"&eventId="+event.id;
        }
        
        event.isInFuture = event.isInFuture();
        
        if (event.isLocked || event.markedAsReady || !event.isInFuture() || event.bookingItem == null || event.bookingItem.isFull || event.bookingItem.freeSpots < 1 || event.isCanceled) {
            event.canBook = false;
        } else {
            event.canBook = true;
        }
        
        if ((event.bookingItem.isFull || event.bookingItem.freeSpots < 1) && !event.isCanceled && event.isInFuture && !event.isLocked) {
            event.canBookWaitingList = true;
        } else {
            event.canBookWaitingList = false;            
        }
         
        event.price = getPrice(event);
        
        return event;
    }

    private void setBookingItem(Event event, boolean force) {
        if (event.bookingItem == null || force)
            event.bookingItem = bookingEngine.getBookingItem(event.bookingItemId);
    }
    
    private void log(String action, Event event, Object additional) {
        EventLog logEntry = new EventLog();
        logEntry.action = action;
        
        if (event != null) {
            logEntry.eventId = event.id;
        }
        
        if (getSession().currentUser != null) {
            logEntry.doneBy = getSession().currentUser.id;
            logEntry.impersonatorUserId = userManager.getImpersonatedOriginalUserId();
        }
        
        if (action.equals("EVENT_UPDATED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (Event)additional);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_REMOVED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (User)additional, false);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_REMOVED_WAITING")) {
            logEntry.comment = eventLoggerHandler.compareWaiting(event, (User)additional, false);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_ADDED")) {
            logEntry.comment = eventLoggerHandler.compare(event, (User)additional, true);
            saveObject(logEntry);
        }
        
        if (action.equals("USER_ADDED_WAITING")) {
            logEntry.comment = eventLoggerHandler.compareWaiting(event, (User)additional, true);
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
        
        List<Location> locationsToRet = new ArrayList(locations.values());
        
        Collections.sort(locationsToRet, (Location a, Location b) -> {
            return a.name.compareTo(b.name);
        });
        
        return locationsToRet;
    }

    @Override
    public Location getLocation(String locationId) {
        return locations.get(locationId);
    }

    @Override
    public void deleteLocation(String locationId) {
        List<Event> eventsConnectedToLocation = getEventsByLocation(locationId);
        
        if (!eventsConnectedToLocation.isEmpty()) {
            throw new ErrorException(1036);
        }
        
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
                .filter(event -> showOnlyNew && event.isInFuture())
                .collect(Collectors.toList()));
        }
        
        retEvents = cloneAndFinalize(retEvents);

        sortByEventDate(retEvents);
        
        return retEvents;
    }

    private void sortByEventDate(List<Event> retEvents) {
        Collections.sort(retEvents, (o1, o2) -> {
            if (o1 == null || o2 == null)
                return 0;
            
            if (o1.mainStartDate == null || o2.mainStartDate == null)
                return 0;
            
            return o1.mainStartDate.compareTo(o2.mainStartDate);
        });
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
    public void bookCurrentUserToEvent(String eventId, String source) {
        Event event = getEvent(eventId);
        
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        AddUserToEvent(event, getSession().currentUser, false, source);
    }   

    private void AddUserToEvent(Event event, User user, boolean silent, String source) {
        List<Booking> alreadyBooked = bookingEngine.getAllBookingsByBookingItem(event.bookingItem.id);
        boolean userdBooked = alreadyBooked.stream()
                .filter( o -> o.userId != null && o.userId.equals(user.id))
                .count() > 0;
       
        if (userdBooked) {
            return;
        }
        
        BookingItem item = bookingEngine.getBookingItem(event.bookingItemId);
        if (item.isFull) {
            addToWaitingList(user, event, silent, source);
        } else {
            signupUserToEvent(event, user, silent, source);
        }
    }   

    private void signupUserToEvent(Event event, User user, boolean silent, String source) {
        boolean needConfirmation = false;
        
        if (user != null && user.companyObject != null && user.companyObject.needConfirmation && !getSession().currentUser.isCompanyOwner) {
            List<User> companyOwners = getCompanyOwners(user.companyObject);
            if (!companyOwners.isEmpty()) {
                needConfirmation = true;
            }
        }
        
        String bookingId = signupUserToEventInternal(event, user, silent, source, needConfirmation);
        
        if (user != null && user.companyObject != null && user.companyObject.needConfirmation && needConfirmation) {
            List<User> companyOwners = getCompanyOwners(user.companyObject);
            if (!companyOwners.isEmpty()) {
                addAndSendConfirmationRequest(event, user, source, companyOwners, bookingId);        
            }
        }
    }
    
    private String signupUserToEventInternal(Event event, User user, boolean silent, String source, boolean needConfirmation) {
        Booking booking = createBooking(event, user);
        booking.source = source;
        
        if (getSession() != null && getSession().currentUser != null) {
            booking.doneByUserId = getSession().currentUser.id;
        }
        
        booking.doneByImpersonator = userManager.getImpersonatedOriginalUserId();
        booking.needConfirmation = needConfirmation;
        
        List<Booking> bookings = new ArrayList();
        bookings.add(booking);
        bookingEngine.addBookings(bookings);
        
        if (!silent && !needConfirmation) {
            sendUserAddedToEventNotifications(user, event);
        }
        
        log("USER_ADDED", event, user);
        
        return booking.id;
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
        return waitingListBookings.values().stream()
                .filter(o -> o.eventId.equals(eventId))
                .map(o -> userManager.getUserById(o.userId))
                .collect(Collectors.toList());
    }

    @Override
    public void removeUserFromEvent(String eventId, String userId, boolean silent) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        Booking booking = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId).stream()
            .filter( o -> o.userId.equals(userId))
            .findAny()
            .orElse(null);
        
        User user = userManager.getUserById(userId);
        if (booking != null && user != null) {
            boolean removed = bookingEngine.deleteBooking(booking.id);
            if (removed) {
                log("USER_REMOVED", event, user);
                if (!silent) {
                    sendRemovedUserFromEventNotification(user, event);
                }
            }
        }
        
        WaitingListBooking waitingListBooking = getWaitingListBooking(eventId, userId);
        if (waitingListBooking != null) {
            waitingListBookings.remove(waitingListBooking.id);
            deleteObject(waitingListBooking);
            log("USER_REMOVED_WAITING", event, user);
            if (!silent) {
                sendRemovedUserFromWaitinglistEventNotification(user, event);
            }
        }
    }
    
    private WaitingListBooking getWaitingListBooking(String eventId, String userId) {
        return waitingListBookings.values().stream()
                .filter(o -> o.eventId.equals(eventId) && o.userId.equals(userId))
                .findFirst()
                .orElse(null);
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
        getEventTypesFilter().clear();
        
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
    
    private List<String> getEventTypesFilter() {
        List<String> sessionFilters = (List<String>) getSession().get("eventtypefilters");
        
        if (sessionFilters == null) {
            sessionFilters = new ArrayList();
            getSession().put("eventtypefilters", sessionFilters);
        }
        
        return sessionFilters;
    }

    private List<Event> filterList(List<Event> events, List<String> locationFilters) {
        List<Event> retEvents = new ArrayList();
        
        for (Event event : events) {
            finalize(event);
            if (event.location != null && locationFilters.contains(event.location.id)) {
                retEvents.add(event);
            }
        }
        
        return retEvents;
    }
    
    private List<Event> filterListEventTypes(List<Event> events, List<String> evenTypesFilter) {
        List<Event> retEvents = new ArrayList();
        
        for (Event event : events) {
            finalize(event);
            if (event.bookingItemType != null && evenTypesFilter.contains(event.bookingItemType.id)) {
                retEvents.add(event);
            }
        }
        
        return retEvents;
    }

    private void finalize(Location loc) {
        if (loc != null) {
            loc.isFiltered = getLocationFilters().contains(loc.id);
        }
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
    public void setGroupInvoiceingStatus(String eventId, String userId, String groupId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        event.groupInvoiceStatus.put(userId, groupId);
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
        event.encryptedPersonalIds.clear();
        saveObject(event);
        log("MARK_AS_READY", event, null);
    }

    @Override
    public void addUserToEvent(String eventId, String userId, boolean silent, String source) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        if (user != null) {
            AddUserToEvent(event, user, silent, source);
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
                    sendReminderMail(reminder.content, reminder.subject, user, event, reminder.userIdMessageId, reminder.userIdInvoiceMessageId, false);
                }
            }
        }
        
        sendToEventHelder(event, reminder);
        
        saveObject(reminder);
        reminders.put(reminder.id, reminder);
        log("REMINDER_SENT", event, reminder.type);
    }

    private void sendToEventHelder(Event event, Reminder reminder) throws ErrorException {
        User eventUserHelder = userManager.getUserById(event.eventHelderUserId);
        if (eventUserHelder != null) {
            if (reminder.type.equals("sms")) {
                sendReminderSms(reminder.content, eventUserHelder, event, reminder.smsMessageId);
            } else {
                sendReminderMail(reminder.content, reminder.subject, eventUserHelder, event, reminder.userIdMessageId, reminder.userIdInvoiceMessageId, true);
            }
        }
    }

    private void sendReminderMail(String conent, String subject, User user, Event event, HashMap<String, String> userIdMessageId, HashMap<String, String> userIdInvoiceMessageId, boolean dontSendToCompany) {
        String email = storePool.getStore(storeId).configuration.emailAdress;
        String content = formatText(conent, user, event);
        subject = formatText(subject, user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String messageId = messageManager.sendMail(event, user.emailAddress, user.fullName, subject, content, email, "");
            if (userIdInvoiceMessageId != null) {
                userIdMessageId.put(user.id, messageId);
            }
        }
        
        if (!dontSendToCompany && user.companyObject != null && user.companyObject.invoiceEmail != null && !user.companyObject.invoiceEmail.isEmpty() && !user.companyObject.invoiceEmail.equals(user.emailAddress)) {
            String messageId = messageManager.sendMail(event, user.companyObject.invoiceEmail, user.fullName, subject, content, email, "");
            if (userIdInvoiceMessageId != null) {
                userIdInvoiceMessageId.put(user.id, messageId);
            }
        }
    }

    private void sendReminderWithEmailAttachement(String conent, String subject, User user, Event event, HashMap<String, String> userIdMessageId, HashMap<String, String> userIdInvoiceMessageId, boolean dontSendToCompany, String base64) {
        String email = storePool.getStore(storeId).configuration.emailAdress;
        String content = formatText(conent, user, event);
        subject = formatText(subject, user, event);
        
        String name = "Diplom.pdf";
        HashMap<String, String> map = new HashMap();
        map.put(name, base64);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String messageId = messageManager.sendMailWithAttachmentIgnoreFutureCheck(event, user.emailAddress, user.fullName, subject, content, email, "", map);
            if (userIdInvoiceMessageId != null) {
                userIdMessageId.put(user.id, messageId);
            }
        }
        
        if (!dontSendToCompany && user.companyObject != null && user.companyObject.invoiceEmail != null && !user.companyObject.invoiceEmail.isEmpty() && !user.companyObject.invoiceEmail.equals(user.emailAddress)) {
            String messageId = messageManager.sendMailWithAttachmentIgnoreFutureCheck(event, user.companyObject.invoiceEmail, user.fullName, subject, content, email, "", map);
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
            String smsId = messageManager.sendSms(event, user.cellPhone, content, user.prefix);
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

    public BookingItemTypeMetadata getBookingTypeMetaDataInternal(String id, boolean fromFinalize) {
         BookingItemTypeMetadata res = bookingTypeMetaDatas.values().stream()
                .filter(o -> o.bookingItemTypeId.equals(id))
                .findFirst()
                .orElse(null);
        if (fromFinalize)
            return res;
        
        return finalizeMetaData(res, id);
    }
    
    @Override
    public BookingItemTypeMetadata getBookingTypeMetaData(String id) {
       return getBookingTypeMetaDataInternal(id, false);
    }

    @Override
    public void saveBookingTypeMetaData(BookingItemTypeMetadata bookingItemTypeMetadata) {
        saveBookingTypeMetaDataInternal(bookingItemTypeMetadata, false);
    }
    
    public void saveBookingTypeMetaDataInternal(BookingItemTypeMetadata bookingItemTypeMetadata, boolean fromFinalize) {
        BookingItemTypeMetadata inMemory = getBookingTypeMetaDataInternal(bookingItemTypeMetadata.bookingItemTypeId, fromFinalize);
        
        if (inMemory != null) {
            deleteAllOtherMetaTypes(inMemory);
            bookingItemTypeMetadata.id = inMemory.id;
        }
        
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
            saveBookingTypeMetaDataInternal(data, true);
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
        
        User loggedOnUser = userManager.getLoggedOnUser();
        if (loggedOnUser != null && loggedOnUser.isAdministrator())
            return true;
        
        if (o.isHidden && (loggedOnUser == null || loggedOnUser.type < 100)) {
            return false;
        }
        
        if (loggedOnUser == null || metaData.publicVisible) {
            return metaData.publicVisible;
        }
        
        if (!loggedOnUser.useGroupId.isEmpty())
            return metaData.visibleForGroup.get(loggedOnUser.useGroupId);
        
        if (getSession().currentUser.groups == null) {
            return metaData.publicVisible;
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
        
        if (getSession() == null || getSession().currentUser == null) {
            return metaData.publicVisible;
        }

        // Admins
        if (getSession().currentUser.type > 50) {
            return true;
        }
        
        if (getSession().currentUser.companyObject == null) {
            return metaData.publicVisible;
        }
        
        String groupId = getSession().currentUser.companyObject.groupId;
        
        return metaData.visibleForGroup.get(groupId);
        
    }

    private Double getPrice(Event event) {
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(event);
        return getPriceForCurrentUser(metaData);
    }

    private Double getPriceForCurrentUser(BookingItemTypeMetadata metaData) {
        Double price = metaData.publicPrice;
        
        if (getSession().currentUser == null)
            return price;
        
        return getPriceForUser(metaData, getSession().currentUser);
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
            return null;
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
        
        if (!event.markedAsReady)
            return false;
        
        return hasUserParticipated(event, getSession().currentUser.id);
    }

    /**
     * Return true if user has participated, if the event is in the future we assume that 
     * he/she will participate.
     * 
     * @param event
     * @param userId
     * @return 
     */
    private boolean hasUserParticipated(Event event, String userId) {
        String status = event.participationStatus.get(userId);
        
        if (event.isInFuture)
            return true;
        
        if (status == null)
            return true;
        
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
                .filter(o -> o.userId != null && o.userId.equals(userId))
                .findAny()
                .orElse(null);
        
        if (res == null && getWaitingListBooking(event.id, user.id) != null) {
            return true;
        }
        
        return res != null;
    }

    @Override
    public void unCancelEvent(String eventId) {
        Event event = getEvent(eventId);
        
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        log("EVENT_UNCANCELED", event, null);
        event.isCanceled = false;
        saveObject(event);
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
        
        if ((from == null || to == null) && getSession().currentUser != null && getSession().currentUser.type == 100) {
            return retEvents.stream()
                    .filter(o -> o.getLastDate() != null && o.getLastDate().after(today))
                    .collect(Collectors.toList());
        }
        
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
    public void clearLocationFilters() {
        getSession().put("sessionfilters", new ArrayList());
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

    private void sendUserTransferredEventNotification(User user, Event fromEvent, Event toEvent) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("transfermail").equals("true")) {
            transferEmail(settingsApp, user, fromEvent, toEvent, "transferemail_subject", "transferemail_content");
        }
        
        if (settingsApp.getSetting("transfersms").equals("true")) {
            transferSms(settingsApp, user, fromEvent, toEvent, "transfersms_content");
        }
    }
    
    private void sendUserAddedToEventNotifications(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("signupemail").equals("true")) {
            sendSignupEmail(settingsApp, user, event, "signup_subject", "signup_mailcontent");
        }
        
        if (settingsApp.getSetting("signupsms").equals("true")) {
            sendSms(settingsApp, user, event, "signup_sms_content");
        }
    }
    
    private void sendUserAddedToEventNotificationsWaitinglist(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("signupemail_waitinglist").equals("true")) {
            sendSignupEmail(settingsApp, user, event, "signup_subject_waitinglist", "signup_mailcontent_waitinglist");
        }
        
        if (settingsApp.getSetting("signupsms_waitinglist").equals("true")) {
            sendSms(settingsApp, user, event, "signup_mailcontent_waitinglist");
        }
    }

    private Application getSettingsApplication() {
        return applicationPool.getApplication("bd751f7e-5062-4d0d-a212-b1fc6ead654f");
    }

    private void sendSignupEmail(Application settingsApp, User user, Event event, String subjectkey, String contentKey) {
        String subject = formatText(settingsApp.getSetting(subjectkey), user, event);
        String content = formatText(settingsApp.getSetting(contentKey), user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(event, user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            if (!mailId.isEmpty()) {
                logEventEntry(event, "SIGNUP_MAIL_SENT", "Signupmail sent to user " + user.fullName + ", email: " + user.emailAddress, mailId);
            }
        } else {
            logEventEntry(event, "SIGNUP_MAIL_SENT_FAILED", "Couldnot send mail to user " + user.fullName + ", no email registered.", "");
        }
    }
    
    private void transferEmail(Application settingsApp, User user, Event fromEvent, Event event, String subjectkey, String contentKey) {
        String subject = formatText(settingsApp.getSetting(subjectkey), user, event);
        String content = formatText(settingsApp.getSetting(contentKey), user, event);
        
        subject = formatTextTransfer(subject, user, fromEvent);
        content = formatTextTransfer(content, user, fromEvent);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(event, user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            logEventEntry(event, "TRANSFER_MAIL_SENT", "Signupmail sent to user " + user.fullName + ", email: " + user.emailAddress, mailId);
        } else {
            logEventEntry(event, "TRANSFER_MAIL_SENT_FAILED", "Couldnot send mail to user " + user.fullName + ", no email registered.", "");
        }
    }

    private String formatTextTransfer(String text, User user, Event event) {
        text = text.replace("{FromEvent.Name}", checkNull(event.bookingItemType.name));
        text = text.replace("{FromEvent.Dates}", getDate(event));
        text = text.replace("{FromEvent.Location}", checkNull(event.location == null ? "" : event.location.name));
        text = text.replace("{FromEvent.SubLocation}", checkNull(event.subLocation == null ? "" : event.subLocation.name));
        text = text.replace("\n", "<br/>");
        return text;
    }
    
    private String formatText(String text, User user, Event event) {
        text = text.replace("{User.Name}", checkNull(user.fullName));
        text = text.replace("{User.Email}", checkNull(user.emailAddress));
        text = text.replace("{Event.Name}", checkNull(event.bookingItemType.name));
        
        text = text.replace("{Event.Dates}", getDate(event));
        text = text.replace("{Event.Location}", checkNull(event.location == null ? "" : event.location.name));
        text = text.replace("{Event.SubLocation}", checkNull(event.subLocation == null ? "" : event.subLocation.name));
        text = text.replace("{Group.Logo}", checkNull(getGroupLogo(user)));
        
        text = text.replace("{Company.Name}", checkNull(user.companyObject == null ? "" : user.companyObject.name));
        text = text.replace("{Company.Vatnumber}", checkNull(user.companyObject == null ? "" : user.companyObject.vatNumber));
        text = text.replace("{Company.Postnumber}", checkNull(user.companyObject == null || user.companyObject.address == null ? "" : user.companyObject.address.postCode));
        text = text.replace("{Company.Country}", checkNull(user.companyObject == null || user.companyObject.address == null ? "" : user.companyObject.address.countryname));
        text = text.replace("{Company.City}", checkNull(user.companyObject == null || user.companyObject.address == null ? "" : user.companyObject.address.city));
        
        text = text.replace("{Event.SubLocation.Description}", checkNull(event.subLocation == null ? "" : event.subLocation.description));
        
        text = text.replace("\n", "<br/>");
        
        return text;
    }

    private String checkNull(String text) {
        if (text == null) {
            return "";
        }
        
        return text;
    }

    private void sendSms(Application settingsApp, User user, Event event, String contentKey) {
        String content = formatText(settingsApp.getSetting(contentKey), user, event);
        String prefix = user.prefix;
        String phoneNumber = user.cellPhone;
        String storeName = getStoreName();
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String res = messageManager.sendSms(event, phoneNumber, content, prefix, storeName);
            if (!res.isEmpty()) {
                logEventEntry(event, "SMS_SIGNUP_SENT", "Signup sms sent to " + user.fullName, res);
            }
        } else {
            logEventEntry(event, "SMS_SIGNUP_SENT_FAILED", "Failed to send signup sms to use " + user.fullName, "");
        }
    }
    
    private void transferSms(Application settingsApp, User user, Event fromEvet, Event event, String contentKey) {
        String content = formatText(settingsApp.getSetting(contentKey), user, event);
        content = formatTextTransfer(content, user, fromEvet);
        
        String prefix = user.prefix;
        String phoneNumber = user.cellPhone;
        String storeName = getStoreName();
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String res = messageManager.sendSms(event, phoneNumber, content, prefix, storeName);
            logEventEntry(event, "SMS_TRANSFER_SENT", "Signup sms sent to " + user.fullName, res);
        } else {
            logEventEntry(event, "SMS_TRANSFER_SENT_FAILED", "Failed to send signup sms to use " + user.fullName, "");
        }
    }

    private void sendRemovedUserFromEventNotification(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("removedemail").equals("true")) {
            sendRemovedEmail(settingsApp, user, event, "removed_mail_subject", "removed_mailcontent");
        }
        
        if (settingsApp.getSetting("removedsms").equals("true")) {
            sendRemovedSms(settingsApp, user, event, "removed_sms_content");
        }
    }
    
    private void sendRemovedUserFromWaitinglistEventNotification(User user, Event event) {
        Application settingsApp = getSettingsApplication();
        
        if (settingsApp.getSetting("removedemail_waitinglist").equals("true")) {
            sendRemovedEmail(settingsApp, user, event, "removed_mail_subject_waitinglist", "removed_mailcontent_waitinglist");
        }
        
        if (settingsApp.getSetting("removedsms_waitinglist").equals("true")) {
            sendRemovedSms(settingsApp, user, event, "removed_sms_content_waitinglist");
        }
    }

    private void sendRemovedEmail(Application settingsApp, User user, Event event, String subjectKey, String contentKey) {
        String subject = formatText(settingsApp.getSetting(subjectKey), user, event);
        String content = formatText(settingsApp.getSetting(contentKey), user, event);
        
        if (user.emailAddress != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(event, user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
            logEventEntry(event, "REMOVED_MAIL_SENT", "Removed mail sent to user " + user.fullName + ", email: " + user.emailAddress, mailId);
        } else {
            logEventEntry(event, "SIGNUP_MAIL_SENT_FAILED", "Couldnot send remove mail to user " + user.fullName + ", no email registered.", "");
        }
    }

    private void sendRemovedSms(Application settingsApp, User user, Event event, String contentKey) {
        String content = formatText(settingsApp.getSetting(contentKey), user, event);
        String prefix = user.prefix;
        String phoneNumber = user.cellPhone;
        String storeName = getStoreName();
        
        if (phoneNumber != null && !phoneNumber.isEmpty()) {
            String res = messageManager.sendSms(event, phoneNumber, content, prefix, storeName);
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
            String mailId = messageManager.sendMail(event, user.emailAddress, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
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
            String res = messageManager.sendSms(event, phoneNumber, content, prefix, storeName);
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
            
            if (user.companyObject != null && user.companyObject.invoiceAddress != null && !user.companyObject.invoiceEmail.equals(user.emailAddress) && !user.companyObject.invoiceAddress.equals(user.emailAddress)) {
                sendMailReminder(user, event, template, user.companyObject.invoiceEmail);
            }
        }
    }

    private void sendMailReminder(User user, Event event, ReminderTemplate template, String emailAddress) throws ErrorException {
        String subject = template.subject;
        String content = formatText(template.content, user, event);
        
        String emailToUser = user.emailAddress;
        if (emailAddress != null) {
            emailToUser = emailAddress;
        }
        
        if (emailToUser != null && !user.emailAddress.isEmpty()) {
            String mailId = messageManager.sendMail(event, emailToUser, user.fullName, subject, content, getStoreEmailAddress(), getStoreName());
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
        log.impersonatorUserId = userManager.getImpersonatedOriginalUserId();
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

    private void addToWaitingList(User user, Event event, boolean silent, String source) {
        if (getWaitingListBooking(event.id, user.id) != null) {
            return;
        }
        
        WaitingListBooking booking = new WaitingListBooking();
        booking.eventId = event.id;
        booking.userId = user.id;
        booking.source = source;
        
        saveObject(booking);
        waitingListBookings.put(booking.id, booking);
        log("USER_ADDED_WAITING", event, user);
        if (!silent) {
            sendUserAddedToEventNotificationsWaitinglist(user, event);
        }
    }

    @Override
    public void transferUserFromWaitingToEvent(String userId, String eventId) {
        Event event = events.get(eventId);
        if (event == null) {
            return;
        }
        
        User user = userManager.getUserById(userId);
        if (user == null) {
            return;
        }
        
        WaitingListBooking booking = getWaitingListBooking(eventId, userId);
        if (booking != null) {
            deleteObject(booking);
            waitingListBookings.remove(booking.id);
            signupUserToEvent(event, user, false, booking.source);
        }
    }

    private String getDate(Event event) {
        String dates = "";
        
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM-yyyy HH:mm");
        SimpleDateFormat dateFormatter2 = new SimpleDateFormat("HH:mm");
        
        for (Day day : event.days) {
            dates += dateFormatter.format(day.startDate) + " - " + dateFormatter2.format(day.endDate) + "\n";
        }
        
        return dates;
            
    }

    private String getGroupLogo(User user) {
        if (user.companyObject == null) {
            return "";
        }
        
        Group group = userManager.getGroup(user.companyObject.groupId);
        if (group != null && group.imageId != null && !group.imageId.isEmpty()) {
            String addr = getStoreDefaultAddress();
//            return "<img src='http://"+addr+"/displayImage.php?id="+group.id+"'/>";
            return "<img src='http://"+addr+"/displayImage.php?id="+group.imageId+"'/>";
        }
        
        return "";
    }

    @Override
    public List<Event> getAllEvents() {
        events.values().forEach(o -> finalize(o));
        return new ArrayList(events.values());
    }

    @Override
    public String getSource(String eventId, String userId) {
        Booking booking = bookingEngine.getAllBookings()
                .stream()
                .filter(o -> 
                        o.userId != null && 
                        o.userId.equals(userId) && 
                        o.bookingItemId != null && 
                        o.bookingItemId.equals(eventId)
                )
                .findFirst()
                .orElse(null);
        
        if (booking != null) {
            return booking.source;
        }
        
        return "";
    }

    @Override
    public List<Event> getEventsForUser(String userId) {
        
        User user = userManager.getUserById(userId);
        
        if (user == null) {
            return new ArrayList();
        }
        
        userManager.checkUserAccess(user);
        
        List<Event> rets = bookingEngine.getAllBookings().stream()
                .filter(booking -> booking.userId != null && booking.userId.equals(userId))
                .filter(booking -> isStatusParticipated(booking))
                .map( booking -> finalize(getEventByBooking(booking)))
                .filter(event -> !event.isCanceled)
                .collect(Collectors.toList());
        
        return rets;
    }

    private boolean isStatusParticipated(Booking booking) {
        Event event = getEventByBooking(booking);
        
        String status = event.participationStatus.get(booking.userId);
        if (status != null && (status.equals("participated_50") || status.equals("not_participated"))) {
            return false;
        }
        
        return true;
    }

    @Override
    public Certificate getCertificateForEvent(String eventId, String userId) {
        Event event = getEvent(eventId);
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(event);
        Company company = user.companyObject;
        Group group = userManager.getGroup(company.groupId);
        
        if (event == null || user == null || metaData == null || company == null || group == null) {
            return null;
        }
        
        List<String> certificateIds = metaData.certificateIds.get(group.id);
        
        Certificate cert = certificates.values().stream()
                .filter(o -> o.validFrom != null && o.validTo != null)
                .filter(o -> o.validFrom.before(event.mainStartDate) && o.validTo.after(event.mainStartDate))
                .filter(o -> certificateIds.contains(o.id))
                .findFirst()
                .orElse(null);
        
        return cert;
    }

    @Override
    public List<Event> getEventsByType(String eventTypeId) {
        List<Event> retEvents = events.values().stream()
                .filter(event -> bookingEngine.getBookingItem(event.bookingItemId).bookingItemTypeId.equals(eventTypeId))
                .collect(Collectors.toList());
        
        retEvents.stream().forEach(event -> finalize(event));
        
        String test = "";
        
        Collections.sort(retEvents, (Event event1, Event event2) -> { return event2.mainStartDate.compareTo(event1.mainStartDate); } );
        
        return retEvents;
    }

    @Override
    public Event getEventByPageId(String pageId) {
        events.values().forEach(o -> setBookingItem(o, false));
        
        return events.values().stream()
                .filter(o -> o.bookingItem.pageId.equals(pageId))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<Location> getActiveLocations() {
        List<Event> activeEvents = events.values().stream()
                .filter(event -> event.isInFuture)
                .collect(Collectors.toList());
        
        if (getSession().get("from") != null && getSession().get("to") != null) {
            activeEvents = getEvents();
        }
        
        
        activeEvents.forEach(o -> finalize(o));
                
        List<Location> activeLocations = activeEvents.stream()
                .map(event -> event.location)
                .distinct()
                .filter(o -> o != null && o.name != null)
                .sorted( (o1, o2) -> { return o1.name.compareTo(o2.name); })
                .collect(Collectors.toList());
        
        activeLocations.stream()
                .forEach(location -> finalize(location));
        
        return activeLocations;
    }

    @Override
    public Double getPriceForEventType(String bookingItemTypeId) {
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(bookingItemTypeId);
        
        if (metaData == null) {
            return -1D;
        }
        
        return getPriceForCurrentUser(metaData);
        
    }

    @Override
    public BookingItemType getBookingItemTypeByPageId(String pageId) {
        return bookingEngine.getBookingItemTypes().stream()
                .filter(type -> type.pageId != null && type.pageId.equals(pageId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void deleteSubLocation(String subLocationId) {
        long count = events.values().stream()
                .filter(event -> event.subLocationId != null && event.subLocationId.equals(subLocationId))
                .count();
        
        if (count > 0) {
            throw new ErrorException(1036);
        }
        
        Location location = getLocationBySubLocationId(subLocationId);
        location.deleteSubLocation(subLocationId);
        saveObject(location);
    }

    @Override
    public List<Event> getEventsByLocation(String locationId) {
        Location loc = getLocation(locationId);
        
        if (loc == null) {
            return new ArrayList();
        }
        
        List<Event> eventsToReturn = new ArrayList();
        for (SubLocation sub : loc.locations) {
            eventsToReturn.addAll(events.values().stream()
                    .filter(event -> event.subLocationId != null && event.subLocationId.equals(sub.id))
                    .collect(Collectors.toList()));
        }
        
        eventsToReturn.stream()
                .forEach(o -> finalize(o));
        
        return eventsToReturn;
    }

    @Override
    public void clearEventBookingManagerForAllData() {
        
        events.values().stream().forEach(o -> clearEventForBookings(o));
        new ArrayList<DataCommon>(events.values()).stream().forEach(o -> deleteEvent(o.id));
        new ArrayList<DataCommon>(locations.values()).stream().forEach(loc -> deleteLocation(loc.id));
        new ArrayList<DataCommon>(reminderTemplates.values()).stream().forEach(rem -> deleteReminderTemplate(rem.id));
//        new ArrayList<DataCommon>(certificates.values()).stream().forEach(cert -> deleteCertificate(cert.id));
        
//        bookingTypeMetaDatas.values().stream().forEach(meta -> deleteObject(meta));
        locations.values().stream().forEach(meta -> deleteObject(meta));
        externalCertificates.values().stream().forEach(meta -> deleteObject(meta));
        waitingListBookings.values().stream().forEach(meta -> deleteObject(meta));
        
//        bookingTypeMetaDatas.clear();
        locations.values().clear();
        externalCertificates.clear();
        waitingListBookings.clear();
        
        new ArrayList<BookingItem>(bookingEngine.getBookingItems()).stream().forEach(item -> bookingEngine.deleteBookingItem(item.id));
//        new ArrayList<BookingItemType>(bookingEngine.getBookingItemTypes()).stream().forEach(type -> bookingEngine.deleteBookingItemType(type.id));
    }

    private void clearEventForBookings(Event o) {
        bookingEngine.getAllBookingsByBookingItem(o.bookingItemId).stream().forEach(booking -> bookingEngine.deleteBooking(booking.id));
    }

    private void deleteAllOtherMetaTypes(BookingItemTypeMetadata inMemory) {
        bookingTypeMetaDatas.values().stream()
                .filter(type -> type.bookingItemTypeId.equals(inMemory.bookingItemTypeId) && !type.id.equals(inMemory.id))
                .forEach(type -> removeAndDeleteType(type));
    }

    private void removeAndDeleteType(BookingItemTypeMetadata type) {
        bookingTypeMetaDatas.remove(type.id);
        deleteObject(type);
    }

    @Override
    public Double getPriceForEventTypeAndUserId(String eventId, String userId) {
        Event event = getEvent(eventId);
        BookingItemTypeMetadata metaData = getBookingTypeMetaData(event);
        
        User user = userManager.getUserById(userId);
        return getPriceForUser(metaData, user);

    }

    private Double getPriceForUser(BookingItemTypeMetadata metaData, User currentUser) {
        Double price = metaData.publicPrice;
        
        if (currentUser.groups != null && !currentUser.groups.isEmpty()) {
            price = metaData.groupPrices.get(currentUser.groups.get(0));
        }

        if (currentUser.companyObject != null && currentUser.companyObject.groupId != null && !currentUser.companyObject.groupId.isEmpty()) {
            price = metaData.groupPrices.get(currentUser.companyObject.groupId);
        }
        
        return price;
    }

    @Override
    public List<EventStatistic> getStatisticGroupedByLocations (Date startDate, Date stopDate, List<String> groupIds, List<String> eventTypeIds) {
        List<Event> events = getAllEvents().stream()
                .filter(event -> isWithinDates(event, startDate, stopDate))
                .collect(Collectors.toList());
        
        
        List<EventStatistic> stats = new ArrayList();
        
        
        for (Location loc : getAllLocations()) {
            
            List<Event> eventsForLocation = events.stream()
                    .filter(event -> event.location.id.equals(loc.id))
                    .collect(Collectors.toList());
            
            if (eventsForLocation.isEmpty()) {
                continue;
            }
            
            EventStatistic stat = new EventStatistic();
            stat.count = getActiveParticipators(eventsForLocation, stat, groupIds);
            stat.locationId = loc.id;
            stats.add(stat);
        }
        
        return stats;
    }
    
    @Override
    public List<EventStatistic> getStatistic(Date startDate, Date oldStopDate, List<String> groupIds, List<String> eventTypeIds) {
        if (oldStopDate == null || oldStopDate.before(startDate) || oldStopDate.equals(startDate))
            return new ArrayList();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(oldStopDate);
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        
        Date stopDate = cal.getTime();
        
        List<Event> events = getAllEvents().stream()
                .filter(event -> isWithinDates(event, startDate, stopDate))
                .collect(Collectors.toList());
        
        if (eventTypeIds != null && !eventTypeIds.isEmpty()) {
            events = events.stream()
                    .filter(event -> eventTypeIds.contains(event.bookingItemType.id))
                    .collect(Collectors.toList());
        }
        
        List<EventStatistic> stats = new ArrayList();
        
        cal.setTime(startDate);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        
        
        while(true) {
            Date start = cal.getTime();
            if (start.after(stopDate)) {
                break;
            }
            
            Date end = getLastDateOfMonth(start);
            
            
            List<Event> eventsForMonth = events.stream()
                    .filter(event -> isWithinDates(event, start, end))
                    .filter(event -> isWithinDates(event, startDate, stopDate))
                    .collect(Collectors.toList());
                    
            
            cal.add(Calendar.MONTH, 1);
            
            EventStatistic stat = new EventStatistic();
            stat.month = cal.get(Calendar.MONTH);
            stat.year = cal.get(Calendar.YEAR);
            stat.count = getActiveParticipators(eventsForMonth, stat, groupIds);
            
            stats.add(stat);
        }
        
        return stats;
    }
    
    public boolean isWithinDates(Event event, Date startDate, Date stopDate) {
        return (event.mainStartDate.before(stopDate) || event.mainStartDate.equals(stopDate))
            &&  
            (event.mainStartDate.after(startDate) || event.mainStartDate.equals(startDate));
    }

    private Date getLastDateOfMonth(Date start) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(start);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    private int getActiveParticipators(List<Event> eventsForMonth, EventStatistic stat, List<String> groupIds) {
        
        int i = 0;
        for (Event event : eventsForMonth) {
            List<Booking> bookings = bookingEngine.getAllBookingsByBookingItem(event.bookingItemId);
            for (Booking booking : bookings) {
                User user = userManager.getUserById(booking.userId);
                if (user != null && hasUserParticipated(event, user.id) && isInGroup(groupIds, user)) {
                    stat.addUserId(event.id, user.id);
                    i += event.days.size();
                }
            }
        }
        
        return i;
    }

    private void cleanBookingItemsThatDoesNotExsist() {
        List<BookingItem> items = bookingEngine.getBookingItems();
        for (BookingItem item : items) {
            boolean exists = events.values().stream().filter(event -> event.bookingItemId.equals(item.id)).count() > 0;
            if (!exists) {
                bookingEngine.deleteBookingItem(item.id);
            }
        }
    }

    private boolean isInGroup(List<String> groupIds, User user) {
        if (groupIds.isEmpty())
            return true;
        
        if (user.companyObject != null && user.companyObject.groupId != null && groupIds.contains(user.companyObject.groupId)) {
            return true;
        }
        
        return false;
    }

    @Override
    public List<Event> getEventsForDay(int year, int month, int day) {
        month--;
        
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date dayDate = cal.getTime();
        
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        events.values().stream().forEach(event -> event.setMainDates());
        
        List<Event> restEvents = events.values().stream()
                .filter(o -> fmt.format(o.mainStartDate).equals(fmt.format(dayDate)))
                .collect(Collectors.toList());
        
        restEvents.stream().forEach(event -> finalize(event));
        
        return restEvents;
                        
    }

    @Override
    public void addTypeFilter(String bookingItemTypeId) {
        List<String> sessionFilters = getLocationFilters();
        sessionFilters.clear();
        

        List<String> eventTypesFilter = getEventTypesFilter();
        if (eventTypesFilter.contains(bookingItemTypeId)) {
            eventTypesFilter.remove(bookingItemTypeId);
        } else {
            eventTypesFilter.add(bookingItemTypeId);
        }
    }

    @Override
    public void moveUserToEvent(String userId, String fromEventId, String toEventId) {
        User user = userManager.getUserById(userId);
        
        
        Event fromEvent = getEvent(fromEventId);
        Event event = getEvent(toEventId);
        
        if (fromEvent.encryptedPersonalIds != null) {
            byte[] personalId = fromEvent.encryptedPersonalIds.get(userId);
            event.encryptedPersonalIds.put(userId, personalId);
            fromEvent.encryptedPersonalIds.remove(userId);
            saveObject(fromEvent);
            saveObject(event);
        }
        
        removeUserFromEvent(fromEventId, userId, true);
        addUserToEvent(toEventId, userId, true, "transfer");
        
        logEventEntry(event, "TRANSFERRED", "User transferred, " + user.fullName + ", from event: " + fromEvent.bookingItemType.name + " to this", user.id);
        logEventEntry(fromEvent, "TRANSFERRED", "User transferred, " + user.fullName + ", from this event to:" + event.bookingItemType.name, user.id);
        
        sendUserTransferredEventNotification(user, fromEvent, event);
    }

    @Override
    public void toggleLocked(String eventId) {
        Event event = getEvent(eventId);
        event.isLocked = !event.isLocked;
        saveObject(event);
        finalize(event);
    }

    @Override
    public void toggleHide(String eventId) {
        Event event = getEvent(eventId);
        event.isHidden = !event.isHidden;
        saveObject(event);
        finalize(event);
    }

    @Override
    public void deleteUserComment(String userId, String eventId, String commentId) {
        Event event = getEvent(eventId);
        if (event == null) {
            throw new ErrorException(1035);
        }
        
        List<UserComment> comments = event.comments.get(userId);
        comments.removeIf(comment -> comment.commentId.equals(commentId));
        saveObject(event);
        log("Comment removed", event, null);
    }

    @Override
    public void sendDiplomas(Reminder reminder, String userId, String base64) {
        User user = userManager.getUserById(userId);
        Event event = getEvent(reminder.eventId);
        sendReminderWithEmailAttachement(reminder.content, reminder.subject, user, event, reminder.userIdMessageId, reminder.userIdInvoiceMessageId, false, base64);
        
        saveObject(reminder);
        reminders.put(reminder.id, reminder);
        log("REMINDER_SENT", event, reminder.type);
   }

    @Override
    public void saveGroupInvoicing(InvoiceGroup invoiceGroup) {
        List<Company> companies = userManager.getCompaniesByVatNumber(invoiceGroup.vatnumber);
        if (companies == null || companies.isEmpty()) {
            Company company = utilManager.getCompanyFromBrReg(invoiceGroup.vatnumber);
            userManager.saveCompany(company);
            invoiceGroup.companyId = company.id;
        } else {
            invoiceGroup.companyId = companies.get(0).id;
        }
        
        saveObject(invoiceGroup);
        groupInvoicing.put(invoiceGroup.id, invoiceGroup);
    }

    @Override
    public List<InvoiceGroup> getInvoiceGroups(String eventId) {
        return groupInvoicing.values().stream()
            .filter(o -> o.eventId.equals(eventId))
            .collect(Collectors.toList());
    }

    @Override
    public InvoiceGroup getInvoiceGroup(String groupId) {
        return groupInvoicing.get(groupId);
    }

    @Override
    public void deleteInvoiceGroup(String groupId) {
        InvoiceGroup group = groupInvoicing.remove(groupId);
        if (group != null) {
            Event event = getEvent(group.eventId);
            if (event.participationStatus != null) {
                for (String userId : event.participationStatus.keySet()) {
                    if (event.participationStatus.get(userId) == groupId) {
                        event.participationStatus.put(userId, "");
                    }
                }
            }
            
            deleteObject(group);
        }
        
        
    }

    @Override
    public void addManuallyParticipatedEvent(ManuallyAddedEventParticipant man) {
        saveObject(man);
        manualEvents.put(man.id, man);
    }

    @Override
    public List<ManuallyAddedEventParticipant> getManuallyAddedEvents(String userId) {
        User user = userManager.getUserByCellphone(userId);
        userManager.checkUserAccess(user);
        return manualEvents.values().stream()
                .filter(man -> man.userId != null &&  man.userId.equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteManullyParticipatedEvent(String id) {
        ManuallyAddedEventParticipant res = manualEvents.remove(id);
        if (res != null) {
            deleteObject(res);
        }
    }

    @Override
    public ManuallyAddedEventParticipant getManuallyAddedEventParticipant(String id) {
        return manualEvents.get(id);
    }

    @Override
    public List<BookingItemType> getMandatoryCourses(String userId) {
        if (userId == null || userId.isEmpty()) {
            userId = getSession().currentUser.id;
        }
        
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        if (user.companyObject == null || user.companyObject.groupId == null || user.companyObject.groupId == null) {
            return new ArrayList();
        }
        
        String groupId = user.companyObject.groupId;
        
        return getBookingItemTypes().stream()
                .filter(type -> getBookingTypeMetaData(type.id) != null 
                        && getBookingTypeMetaData(type.id).mandatoryForGroup != null 
                        && getBookingTypeMetaData(type.id).mandatoryForGroup.get(groupId) != null 
                        && getBookingTypeMetaData(type.id).mandatoryForGroup.get(groupId) == true)
                .collect(Collectors.toList());
    }

    @Override
    public boolean hasCompletedMandatoryEvent(String eventTypeId, String userId) {
        if (userId == null || userId.isEmpty()) {
            userId = getSession().currentUser.id;
        }
        
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        if (hasForcedMandatoryTest(eventTypeId, user.id)) {
            return true;
        }

        List<Event> eventsOfType = events.values().stream()
                .filter(b -> b.bookingItem != null && b.bookingItem.bookingItemTypeId.equals(eventTypeId))
                .collect(Collectors.toList());
        
        for (Event event : eventsOfType) {
            if (isUserSignedUpForEvent(event.id, user.id) && hasUserParticipated(event, user.id) ) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean hasForcedMandatoryTest(String eventTypeId, String userId) {
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        if (user != null) {
            ForcedParcipated forced = forcedParticipated.get(userId);
            if (forced == null)
                return false;
            
            return forced.eventTypeIds.contains(eventTypeId);
        }
        
        return false;
    }

    @Override
    public void setForcedMandatoryAccess(String userId, List<String> bookingItemIds) {
        User user = userManager.getUserById(userId);
        if (user != null) {
            ForcedParcipated forced = forcedParticipated.get(userId);
            if (forced == null) {
                forced = new ForcedParcipated();
            }
            
            forced.userId = userId;
            forced.eventTypeIds = bookingItemIds;
            
            saveObject(forced);
            forcedParticipated.put(forced.userId, forced);
        }
    }

    @Override
    public void registerEventIntrest(EventIntrest interest) {
        interest.userId = getSession().currentUser.id;
        saveObject(interest);
        eventInterests.put(interest.id, interest);
    }

    @Override
    public List<EventIntrest> getInterests() {
        return new ArrayList(eventInterests.values());
    }

    @Override
    public void removeInterest(String bookingItemTypeId, String userId) {
        List<EventIntrest> res = eventInterests.values().stream()
                .filter(o -> o.eventTypeId.equals(bookingItemTypeId) && o.userId.equals(userId))
                .collect(Collectors.toList());
        
        res.stream().forEach(o -> {
            eventInterests.remove(o.id);
            deleteObject(o);
        });
    }

    @Override
    public List<String> getCompaniesWhereNoCanditasHasCompletedTests(List<String> testIds) {
        List<BookingItemType> bookingItemTypes = bookingEngine.getBookingItemTypes().stream()
                .filter(b -> testIds.contains(b.id))
                .collect(Collectors.toList());
        
        List<ScormPackage> scormPackages = scormManager.getAllPackages().stream()
                .filter(b -> testIds.contains(b.id))
                .collect(Collectors.toList());
        
        List<Company> companies = userManager.getAllCompanies();
        
        List<String> retCompanies = new ArrayList();
        
        for (Company company : companies) {
            List<User> users = userManager.getUsersByCompanyId(company.id);
            
            boolean hasCompletedTests = hasAnyUserCompletedAllTests(bookingItemTypes, users);
            boolean hasCompletedScorms = hasAnyUserCompletedAllScorms(scormPackages, users);
            
            if (!hasCompletedScorms || !hasCompletedTests) {
                retCompanies.add(company.id);
            }
        }
        
        return retCompanies;
    }

    private boolean hasAnyUserCompletedAllTests(List<BookingItemType> bookingItemTypes, List<User> users) {
        List<BookingItemType> typesToTest = new ArrayList(bookingItemTypes);
        
        for (BookingItemType type : bookingItemTypes) {
            boolean found = false;
            for (User user: users) {
                
                boolean hasCompleted = hasCompletedMandatoryEvent(type.id, user.id);
                if (hasCompleted) {
                    found = true;
                    break;
                }
            }
            
            if (found) {
                typesToTest.remove(type);
            }
        }
        
        return typesToTest.isEmpty();
    }

    private boolean hasAnyUserCompletedAllScorms(List<ScormPackage> scormPackages, List<User> users) {
        List<ScormPackage> scormPackagesToTest = new ArrayList(scormPackages);
        
        for (ScormPackage spackage : scormPackages) {
            boolean found = false;
            for (User user: users) {
                boolean hasCompleted = scormManager.getScormForCurrentUser(spackage.id, user.id).passed;
                if (hasCompleted) {
                    found = true;
                    break;
                }
            }
            
            if (found) {
                scormPackagesToTest.remove(spackage);
            }
        }
        
        return scormPackagesToTest.isEmpty();
    }

    private List<User> getCompanyOwners(Company companyObject) {
        if (companyObject == null)
            return new ArrayList();
        
        return userManager.getUsersByCompanyIdSecure(companyObject.id)
                .stream()
                .filter(o -> o.isCompanyOwner)
                .collect(Collectors.toList());
    }

    private void addAndSendConfirmationRequest(Event event, User user, String source, List<User> companyOwners, String bookingId) {
        EventRequest request = new EventRequest();
        request.eventId = event.id;
        request.userId = user.id;
        request.source = source;
        request.bookingId = bookingId;
        
        saveObject(request);
        requests.put(request.id, request);
        
        Application settingsApp = getSettingsApplication();
        String email = storePool.getStore(storeId).configuration.emailAdress;
        
        for (User companyOwner : companyOwners) {
            String emailAddress = companyOwner.emailAddress;
            String content = settingsApp.getSetting("requestConfirmationMail");
            String subject = settingsApp.getSetting("requestConfirmationSubject");
            
            content = formatRequestEmail(content, event, user, request.id);
            
            if (!content.isEmpty() && !subject.isEmpty()) {
                content = formatText(content, companyOwner, event);
                messageManager.sendMail(event, emailAddress, user.fullName, subject, content, email, "");
           }
        }
    }

    private String formatRequestEmail(String text, Event event, User requestedUser, String requestId) {
        String addr = "http://www.promeisteracademy.se/?page=confirmeventrequest&requestId="+requestId;
        text = text.replace("{Requested.Link}", addr);
        text = text.replace("{RequestedUser.FullName}", requestedUser.fullName);
        return text;
    }

    @Override
    public EventRequest getEventRequest(String id) {
        return requests.get(id);
    }

    @Override
    public void handleEventRequest(String id, boolean accepted) {
        EventRequest request = requests.remove(id);
        
        if (request == null)
            return;
        
        if (accepted) {
            bookingEngine.confirmBooking(request.bookingId);
        } else {
            removeUserFromEvent(request.eventId, request.userId, true);
        }
        
        deleteObject(request);
    }

    @Override
    public boolean isWaitingForConfirmation(String eventId, String userId) {
        return requests.values()
                .stream()
                .filter(o -> o.eventId.equals(eventId) && o.userId.equals(userId))
                .count()  > 0;
    }

    @Override
    public void addPersonalIdToEvent(String eventId, String userId, String personalId) {
        User user = userManager.getUserById(userId);
        userManager.checkUserAccess(user);
        
        Event event  = getEvent(eventId);
        if (event != null) {
            byte[] personalIdEncryptedId = encryptionManager.encrypt(personalId);
            event.encryptedPersonalIds.put(userId, personalIdEncryptedId);
            saveObject(event);
        }
    }

    @Override
    public Map<String, String> decodePersonalIds(String eventId, String privateKey) {
        Event event  = getEvent(eventId);
        Map<String, String> returnMap = new HashMap();
        
        if (event != null) {
            for (String userId : event.encryptedPersonalIds.keySet()) {
                User user = userManager.getUserById(userId);
                String decryptedPersonalId = encryptionManager.decryption(privateKey, event.encryptedPersonalIds.get(userId));
                returnMap.put(decryptedPersonalId, user.fullName);
            }
        }
        
        return returnMap;
    }
}