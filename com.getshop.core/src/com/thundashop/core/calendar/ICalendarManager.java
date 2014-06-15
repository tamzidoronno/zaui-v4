package com.thundashop.core.calendar;

import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.EventPartitipated;
import com.thundashop.core.calendarmanager.data.FilterResult;
import com.thundashop.core.calendarmanager.data.Location;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.calendarmanager.data.ReminderHistory;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Writing;
import java.util.List;
import java.util.Map;

/**
 * The calendar helps you keep track of happenings and other events.<br>
 * You can also attach users to the events and have reminders sent to them.<br>
 */
@GetShopApi
public interface ICalendarManager {
    /**
     * Create a new entry to the calendar on a given date.
     * @param year The year to attach the entry to
     * @param month The month to attach the entry to
     * @param day The day to attach the entry to
     * @param entry The entry to attach
     * @return
     * @throws ErrorException 
     */
    public Entry createEntry(int year, int month, int day) throws ErrorException;
    
    /**
     * Get all data attached to a given month.
     * @param year The year to fetch
     * @param month The month to fetch
     * @param includeExtraEvents 
     * @return
     * @throws ErrorException 
     */
    public Month getMonth(int year, int month, boolean includeExtraEvents) throws ErrorException;
    
    /**
     * Returns all months with only valid entries
     * and all entries are sorted by date.
     * 
     * @return
     * @throws ErrorException 
     */
    public List<Month> getMonths() throws ErrorException;
    
    /**
     * Delete an existing entry by a given id from the calendar.
     * @param id The id of the entry to delete.
     * @throws ErrorException 
     */
    @Editor
    @Writing
    public void deleteEntry(String id) throws ErrorException;
    
    /**
     Remind a given list of users about a given entry.
     * @param entryId The id of the entry to remind about.
     * @param byEmail Remind users by email (true to send)
     * @param bySMS Remind users by sms (true to send)
     * @param users A list of user ids to remind.
     * @param text The text to send when reminding.
     * @param subject A subject to attach to the email.
     * @return
     * @throws ErrorException 
     */
    @Editor
    @Writing
    public void sendReminderToUser(boolean byEmail, boolean bySMS, List<String> users, String text, String subject, String eventId, String attachment, String sendReminderToUser) throws ErrorException;
    
    /**
     * Get all entries to a given day
     * @param year The year to fetch the entries on.
     * @param month The month to fetch the entries on.
     * @param day The day to fetch the entries on.
     * @return
     * @throws ErrorException 
     */
    public List<Entry> getEntries(int year, int month, int day, List<String> filters) throws ErrorException;
    
    /**
     * Add a given user to a given event.
     * @param userId The user id to add to a the given event (see usermanager for more inforamtion about this id)
     * @param eventId The event id to attach to the user.
     * @param password A password you want to attach to the email that is being sent to the user.
     * @param username A username you want to attach to the email that is being sent to the user.
     * @return
     * @throws ErrorException 
     */
    public void addUserToEvent(String userId, String eventId, String password, String username, String source) throws ErrorException;
    
    /**
     * Deprecated and a fallback method.
     * 
     * @param userId
     * @param eventId
     * @param password
     * @param username
     * @throws ErrorException 
     */
    public void addUserToEvent(String userId, String eventId, String password, String username) throws ErrorException;
    
    /**
     * Adds a user to a page event
     * 
     * @param userId
     * @throws ErrorException 
     */
    public void addUserToPageEvent(String userId, String bookingAppId) throws ErrorException;
    
    /**
     * Remove a given user from a given event.
     * @param userId The userid for the event to be removed. (see usermanager for more inforamtion about this id)
     * @param eventId The id of the event for the user to be removed from.
     * @return
     * @throws ErrorException 
     */
    @Editor
    @Writing
    public void removeUserFromEvent(String userId, String eventId) throws ErrorException;
    
    /**
     * Gives you the specified entry by id
     * 
     * @param entryId
     * @return 
     */
    public Entry getEntry(String entryId);
    
    
    /**
     * Save an already existing entry.
     * @param entry
     * @throws ErrorException 
     */
    public void saveEntry(Entry entry) throws ErrorException;
    
    /**
     * Confirms a entry.
     * 
     * @param entryId
     * @throws ErrorException 
     */
    @Editor
    @Writing
    public void confirmEntry(String entryId) throws ErrorException;
    
    /**
     * Returns a set of filters that
     * can be applied to the Calendar.
     */
    public List<String> getFilters() throws ErrorException;
    
    /**
     * Apply a set of filters, 
     * if this filters are applied, it will not return entries
     * that does not match the filter criteria.
     */
    public void applyFilter(List<String> filters);
    
    /**
     * Returns a set of filters that 
     * has been applied to the current session
     * calendar.
     * 
     * @return 
     */
    public List<String> getActiveFilters();
    
    /**
     * Accept a candidate from waitinglist to 
     * course.
     */
    @Editor
    @Writing
    public void transferFromWaitingList(String entryId, String userId) throws ErrorException;
    
    /**
     * When an event is sent it automatically creates and log an history entry.
     * Use this function to get all the history for a given event.
     * 
     * @param eventId
     * @return 
     */
    @Editor
    public List<ReminderHistory> getHistory(String eventId);
    
    /**
     * Transfer a user from one event to another.
     * 
     * Needs to be administrator becuase it updating the candidates password.
     * 
     * @param evenId 
     */
    @Administrator
    public void transferUser(String fromEventId, String toEventId, String userId) throws ErrorException;
    
    /**
     * Returns a list of 
     */
    public List<Entry> getAllEventsConnectedToPage(String pageId);
    
    /**
     * Adds a new location to the system. 
     * 
     * @param location
     * @throws ErrorException 
     */
    @Administrator
    @Writing
    public Location saveLocation(Location location) throws ErrorException;
    
    /**
     * Delete a location by id
     * 
     * @param location
     * @throws ErrorException 
     */
    @Administrator
    @Writing
    public void deleteLocation(String locationId) throws ErrorException;
    
    /**
     * Get all locations.
     * 
     * @return List<Location>
     */
    public List<Location> getAllLocations();
    
    /**
     * Get all entries
     */
    public List<Month> getMonthsAfter(int year, int month);
    
    /**
     * This returns a list of all entries
     * that is connected to a page.
     * 
     * @return
     * @throws ErrorException 
     */
    @Editor
    public List<FilterResult> getEventsGroupedByPageId() throws ErrorException;
    
    /**
     * return a list of entires that a specified user
     * has been attending to
     */
    @Editor
    public List<Entry> getEntriesByUserId(String userId) throws ErrorException;
    
    
    @Administrator
    public EventPartitipated getEventPartitipatedData(String pageId) throws ErrorException;
    
    @Administrator
    public void setEventPartitipatedData(EventPartitipated eventData) throws ErrorException;
    
    @Administrator
    public String getSignature(String id) throws ErrorException;
    
    @Administrator
    public void setSignature(String userid, String signature) throws ErrorException;
    
}
