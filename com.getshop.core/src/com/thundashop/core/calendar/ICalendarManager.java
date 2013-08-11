package com.thundashop.core.calendar;

import com.thundashop.core.calendarmanager.data.Entry;
import com.thundashop.core.calendarmanager.data.Month;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

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
     * Delete an existing entry by a given id from the calendar.
     * @param id The id of the entry to delete.
     * @throws ErrorException 
     */
    @Editor
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
    public void sendReminderToUser(boolean byEmail, boolean bySMS, List<String> users, String text, String subject) throws ErrorException;
    
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
    public void addUserToEvent(String userId, String eventId, String password, String username) throws ErrorException;
    
    /**
     * Remove a given user from a given event.
     * @param userId The userid for the event to be removed. (see usermanager for more inforamtion about this id)
     * @param eventId The id of the event for the user to be removed from.
     * @return
     * @throws ErrorException 
     */
    @Editor
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
    @Administrator
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
}
