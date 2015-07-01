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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("eventId",new Gson().toJson(eventId));
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "addUserSilentlyToEvent";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("eventId",new Gson().toJson(eventId));
          data.args.put("password",new Gson().toJson(password));
          data.args.put("username",new Gson().toJson(username));
          data.args.put("source",new Gson().toJson(source));
          data.method = "addUserToEvent";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Adds a user to a page event
     *
     * @param userId
     * @throws ErrorException
     */
     public void addUserToPageEvent(java.lang.String userId, java.lang.String bookingAppId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("bookingAppId",new Gson().toJson(bookingAppId));
          data.method = "addUserToPageEvent";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Apply a set of filters,
     * if this filters are applied, it will not return entries
     * that does not match the filter criteria.
     */
     public void applyFilter(java.util.List filters)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("filters",new Gson().toJson(filters));
          data.method = "applyFilter";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Confirms a entry.
     *
     * @param entryId
     * @throws ErrorException
     */
     public void confirmEntry(java.lang.String entryId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entryId",new Gson().toJson(entryId));
          data.method = "confirmEntry";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void createANewDiplomaPeriod(java.util.Date startDate, java.util.Date stopDate)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("startDate",new Gson().toJson(startDate));
          data.args.put("stopDate",new Gson().toJson(stopDate));
          data.method = "createANewDiplomaPeriod";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.args.put("month",new Gson().toJson(month));
          data.args.put("day",new Gson().toJson(day));
          data.method = "createEntry";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.Entry>() {}.getType();
          com.thundashop.core.calendarmanager.data.Entry object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void deleteDiplomaPeriode(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "deleteDiplomaPeriode";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Delete an existing entry by a given id from the calendar.
     * @param id The id of the entry to delete.
     * @throws ErrorException
     */
     public void deleteEntry(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "deleteEntry";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Delete a location by id
     *
     * @param location
     * @throws ErrorException
     */
     public void deleteLocation(java.lang.String locationId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("locationId",new Gson().toJson(locationId));
          data.method = "deleteLocation";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Returns a set of filters that
     * has been applied to the current session
     * calendar.
     *
     * @return
     */
     public java.util.List getActiveFilters()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getActiveFilters";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<java.lang.String>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list of
     */
     public java.util.List getAllEventsConnectedToPage(java.lang.String pageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.method = "getAllEventsConnectedToPage";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllLocations";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Location>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public java.util.List getArea()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getArea";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.LocationArea>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public com.thundashop.core.calendarmanager.data.DiplomaPeriod getDiplomaPeriod(java.util.Date date)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("date",new Gson().toJson(date));
          data.method = "getDiplomaPeriod";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.DiplomaPeriod>() {}.getType();
          com.thundashop.core.calendarmanager.data.DiplomaPeriod object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public java.util.List getDiplomaPeriods()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getDiplomaPeriods";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.DiplomaPeriod>>() {}.getType();
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.args.put("month",new Gson().toJson(month));
          data.args.put("day",new Gson().toJson(day));
          data.args.put("filters",new Gson().toJson(filters));
          data.method = "getEntries";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Entry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries in a region based on the point
     * @param point
     * @return
     */
     public com.thundashop.core.calendarmanager.data.LocationArea getEntriesByPosition(com.thundashop.core.calendarmanager.data.LocationPoint point)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("point",new Gson().toJson(point));
          data.method = "getEntriesByPosition";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.LocationArea>() {}.getType();
          com.thundashop.core.calendarmanager.data.LocationArea object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public java.util.List getEntriesByUserId(java.lang.String userId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "getEntriesByUserId";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entryId",new Gson().toJson(entryId));
          data.method = "getEntry";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.method = "getEventPartitipatedData";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getEventsGroupedByPageId";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getFilters";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("eventId",new Gson().toJson(eventId));
          data.method = "getHistory";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.args.put("month",new Gson().toJson(month));
          data.args.put("includeExtraEvents",new Gson().toJson(includeExtraEvents));
          data.method = "getMonth";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getMonths";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Month>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries
     */
     public java.util.List getMonthsAfter(int year, int month)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("year",new Gson().toJson(year));
          data.args.put("month",new Gson().toJson(month));
          data.method = "getMonthsAfter";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Month>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries in a region based on the point
     * @param point
     * @return
     */
     public java.util.List getMyEvents()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getMyEvents";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.calendarmanager.data.Entry>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public java.lang.String getSignature(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getSignature";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries in a region based on the point
     * @param point
     * @return
     */
     public boolean isUserOnEvent(java.lang.String userId, java.lang.String eventId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("eventId",new Gson().toJson(eventId));
          data.method = "isUserOnEvent";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries in a region based on the point
     * @param point
     * @return
     */
     public boolean isUserOnWaiting(java.lang.String userId, java.lang.String eventId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("eventId",new Gson().toJson(eventId));
          data.method = "isUserOnWaiting";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all entries in a region based on the point
     * @param point
     * @return
     */
     public void registerToken(java.lang.String token)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("token",new Gson().toJson(token));
          data.method = "registerToken";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void removeSignature(java.lang.String userId, java.lang.String diplomId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("diplomId",new Gson().toJson(diplomId));
          data.method = "removeSignature";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Remove a given user from a given event.
     * @param userId The userid for the event to be removed. (see usermanager for more inforamtion about this id)
     * @param eventId The id of the event for the user to be removed from.
     * @return
     * @throws ErrorException
     */
     public void removeUserFromEvent(java.lang.String userId, java.lang.String eventId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("eventId",new Gson().toJson(eventId));
          data.method = "removeUserFromEvent";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Save an already existing entry.
     * @param entry
     * @throws ErrorException
     */
     public void saveEntry(com.thundashop.core.calendarmanager.data.Entry entry)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entry",new Gson().toJson(entry));
          data.method = "saveEntry";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Adds a new location to the system.
     *
     * @param location
     * @throws ErrorException
     */
     public com.thundashop.core.calendarmanager.data.Location saveLocation(com.thundashop.core.calendarmanager.data.Location location)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("location",new Gson().toJson(location));
          data.method = "saveLocation";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.calendarmanager.data.Location>() {}.getType();
          com.thundashop.core.calendarmanager.data.Location object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void saveLocationArea(com.thundashop.core.calendarmanager.data.LocationArea area)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("area",new Gson().toJson(area));
          data.method = "saveLocationArea";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("byEmail",new Gson().toJson(byEmail));
          data.args.put("bySMS",new Gson().toJson(bySMS));
          data.args.put("users",new Gson().toJson(users));
          data.args.put("text",new Gson().toJson(text));
          data.args.put("subject",new Gson().toJson(subject));
          data.args.put("eventId",new Gson().toJson(eventId));
          data.args.put("attachment",new Gson().toJson(attachment));
          data.args.put("sendReminderToUser",new Gson().toJson(sendReminderToUser));
          data.method = "sendReminderToUser";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void setDiplomaPeriodeBackground(java.lang.String diplomaId, java.lang.String background)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("diplomaId",new Gson().toJson(diplomaId));
          data.args.put("background",new Gson().toJson(background));
          data.method = "setDiplomaPeriodeBackground";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void setDiplomaTextColor(java.lang.String diplomaId, java.lang.String textColor)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("diplomaId",new Gson().toJson(diplomaId));
          data.args.put("textColor",new Gson().toJson(textColor));
          data.method = "setDiplomaTextColor";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void setEventPartitipatedData(com.thundashop.core.calendarmanager.data.EventPartitipated eventData)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("eventData",new Gson().toJson(eventData));
          data.method = "setEventPartitipatedData";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * return a list of entires that a specified user
     * has been attending to
     */
     public void setSignature(java.lang.String userid, java.lang.String signature, java.lang.String dimplomaId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userid",new Gson().toJson(userid));
          data.args.put("signature",new Gson().toJson(signature));
          data.args.put("dimplomaId",new Gson().toJson(dimplomaId));
          data.method = "setSignature";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Accept a candidate from waitinglist to
     * course.
     */
     public void transferFromWaitingList(java.lang.String entryId, java.lang.String userId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("entryId",new Gson().toJson(entryId));
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "transferFromWaitingList";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

     /**
     * Transfer a user from one event to another.
     *
     * Needs to be administrator becuase it updating the candidates password.
     *
     * @param evenId
     */
     public void transferUser(java.lang.String fromEventId, java.lang.String toEventId, java.lang.String userId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("fromEventId",new Gson().toJson(fromEventId));
          data.args.put("toEventId",new Gson().toJson(toEventId));
          data.args.put("userId",new Gson().toJson(userId));
          data.method = "transferUser";
          data.interfaceName = "core.calendar.ICalendarManager";
          String result = transport.send(data);
     }

}
