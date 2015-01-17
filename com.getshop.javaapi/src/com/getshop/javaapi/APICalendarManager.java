package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APICalendarManager {

      public Transporter transport;

      public APICalendarManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void addUserSilentlyToEvent(java.lang.String eventId, java.lang.String userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "addUserSilentlyToEvent";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add a given user to a given event.
     * @param userId The user id to add to a the given event (see usermanager for more inforamtion about this id)
     * @param eventId The event id to attach to the user.
     * @param password A password you want to attach to the email that is being sent to the user.
     * @param username A username you want to attach to the email that is being sent to the user.
     * @return
     * @throws ErrorException
     */
     public void addUserToEvent(java.lang.String userId, java.lang.String eventId, java.lang.String password, java.lang.String username, java.lang.String source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("username",new Gson().toJson(username));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "addUserToEvent";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Adds a user to a page event
     *
     * @param userId
     * @throws ErrorException
     */
     public void addUserToPageEvent(java.lang.String userId, java.lang.String bookingAppId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("bookingAppId",new Gson().toJson(bookingAppId));
          gs_json_object_data.method = "addUserToPageEvent";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Apply a set of filters,
     * if this filters are applied, it will not return entries
     * that does not match the filter criteria.
     */
     public void applyFilter(java.util.List filters)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filters",new Gson().toJson(filters));
          gs_json_object_data.method = "applyFilter";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Confirms a entry.
     *
     * @param entryId
     * @throws ErrorException
     */
     public void confirmEntry(java.lang.String entryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.method = "confirmEntry";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new entry to the calendar on a given date.
     * @param year The year to attach the entry to
     * @param month The month to attach the entry to
     * @param day The day to attach the entry to
     * @param entry The entry to attach
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.calendarmanager.data.Entry createEntry(int year, int month, int day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "createEntry";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.Entry>() {}.getType();
          com.thundashop.core.calendarmanager.data.Entry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete an existing entry by a given id from the calendar.
     * @param id The id of the entry to delete.
     * @throws ErrorException
     */
     public void deleteEntry(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteEntry";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Delete a location by id
     *
     * @param location
     * @throws ErrorException
     */
     public void deleteLocation(java.lang.String locationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("locationId",new Gson().toJson(locationId));
          gs_json_object_data.method = "deleteLocation";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a set of filters that
     * has been applied to the current session
     * calendar.
     *
     * @return
     */
     public java.util.List getActiveFilters()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getActiveFilters";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<java.lang.String>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list of
     */
     public java.util.List getAllEventsConnectedToPage(java.lang.String pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getAllEventsConnectedToPage";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Entry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all locations.
     *
     * @return List<Location>
     */
     public java.util.List getAllLocations()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllLocations";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Location>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries to a given day
     * @param year The year to fetch the entries on.
     * @param month The month to fetch the entries on.
     * @param day The day to fetch the entries on.
     * @return
     * @throws ErrorException
     */
     public java.util.List getEntries(int year, int month, int day, java.util.List filters)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.args.put("filters",new Gson().toJson(filters));
          gs_json_object_data.method = "getEntries";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Entry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public java.util.List getEntriesByUserId(java.lang.String userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getEntriesByUserId";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Entry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Gives you the specified entry by id
     *
     * @param entryId
     * @return
     */
     public com.thundashop.core.calendarmanager.data.Entry getEntry(java.lang.String entryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.method = "getEntry";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.Entry>() {}.getType();
          com.thundashop.core.calendarmanager.data.Entry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public com.thundashop.core.calendarmanager.data.EventPartitipated getEventPartitipatedData(java.lang.String pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getEventPartitipatedData";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.EventPartitipated>() {}.getType();
          com.thundashop.core.calendarmanager.data.EventPartitipated object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * This returns a list of all entries
     * that is connected to a page.
     *
     * @return
     * @throws ErrorException
     */
     public java.util.List getEventsGroupedByPageId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getEventsGroupedByPageId";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.FilterResult>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a set of filters that
     * can be applied to the Calendar.
     */
     public java.util.List getFilters()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getFilters";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<java.lang.String>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * When an event is sent it automatically creates and log an history entry.
     * Use this function to get all the history for a given event.
     *
     * @param eventId
     * @return
     */
     public java.util.List getHistory(java.lang.String eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getHistory";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.ReminderHistory>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all data attached to a given month.
     * @param year The year to fetch
     * @param month The month to fetch
     * @param includeExtraEvents
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.calendarmanager.data.Month getMonth(int year, int month, boolean includeExtraEvents)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.args.put("includeExtraEvents",new Gson().toJson(includeExtraEvents));
          gs_json_object_data.method = "getMonth";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.Month>() {}.getType();
          com.thundashop.core.calendarmanager.data.Month object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns all months with only valid entries
     * and all entries are sorted by date.
     *
     * @return
     * @throws ErrorException
     */
     public java.util.List getMonths()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMonths";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Month>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries
     */
     public java.util.List getMonthsAfter(int year, int month)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.method = "getMonthsAfter";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Month>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public java.lang.String getSignature(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getSignature";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove a given user from a given event.
     * @param userId The userid for the event to be removed. (see usermanager for more inforamtion about this id)
     * @param eventId The id of the event for the user to be removed from.
     * @return
     * @throws ErrorException
     */
     public void removeUserFromEvent(java.lang.String userId, java.lang.String eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "removeUserFromEvent";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Save an already existing entry.
     * @param entry
     * @throws ErrorException
     */
     public void saveEntry(com.thundashop.core.calendarmanager.data.Entry entry)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.method = "saveEntry";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Adds a new location to the system.
     *
     * @param location
     * @throws ErrorException
     */
     public com.thundashop.core.calendarmanager.data.Location saveLocation(com.thundashop.core.calendarmanager.data.Location location)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("location",new Gson().toJson(location));
          gs_json_object_data.method = "saveLocation";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.Location>() {}.getType();
          com.thundashop.core.calendarmanager.data.Location object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

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
     public void sendReminderToUser(boolean byEmail, boolean bySMS, java.util.List users, java.lang.String text, java.lang.String subject, java.lang.String eventId, java.lang.String attachment, java.lang.String sendReminderToUser)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("byEmail",new Gson().toJson(byEmail));
          gs_json_object_data.args.put("bySMS",new Gson().toJson(bySMS));
          gs_json_object_data.args.put("users",new Gson().toJson(users));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.args.put("subject",new Gson().toJson(subject));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("attachment",new Gson().toJson(attachment));
          gs_json_object_data.args.put("sendReminderToUser",new Gson().toJson(sendReminderToUser));
          gs_json_object_data.method = "sendReminderToUser";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void setEventPartitipatedData(com.thundashop.core.calendarmanager.data.EventPartitipated eventData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventData",new Gson().toJson(eventData));
          gs_json_object_data.method = "setEventPartitipatedData";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void setSignature(java.lang.String userid, java.lang.String signature)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userid",new Gson().toJson(userid));
          gs_json_object_data.args.put("signature",new Gson().toJson(signature));
          gs_json_object_data.method = "setSignature";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Accept a candidate from waitinglist to
     * course.
     */
     public void transferFromWaitingList(java.lang.String entryId, java.lang.String userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "transferFromWaitingList";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Transfer a user from one event to another.
     *
     * Needs to be administrator becuase it updating the candidates password.
     *
     * @param evenId
     */
     public void transferUser(java.lang.String fromEventId, java.lang.String toEventId, java.lang.String userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fromEventId",new Gson().toJson(fromEventId));
          gs_json_object_data.args.put("toEventId",new Gson().toJson(toEventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "transferUser";
          gs_json_object_data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(gs_json_object_data);
     }

}
