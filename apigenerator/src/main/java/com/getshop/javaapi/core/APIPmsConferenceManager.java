package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsConferenceManager {

      public Communicator transport;

      public APIPmsConferenceManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public void addGuestToEvent(Object guestId, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("guestId",new Gson().toJson(guestId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "addGuestToEvent";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void deleteConference(Object conferenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("conferenceId",new Gson().toJson(conferenceId));
          gs_json_object_data.method = "deleteConference";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void deleteConferenceEvent(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteConferenceEvent";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void deleteEventEntry(Object eventEntryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventEntryId",new Gson().toJson(eventEntryId));
          gs_json_object_data.method = "deleteEventEntry";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void deleteItem(Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "deleteItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getAllConferences(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllConferences";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getAllGuestsForEvent(Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getAllGuestsForEvent";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getAllItem(Object toItem)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("toItem",new Gson().toJson(toItem));
          gs_json_object_data.method = "getAllItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getConference(Object conferenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("conferenceId",new Gson().toJson(conferenceId));
          gs_json_object_data.method = "getConference";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getConferenceEvent(Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getConferenceEvent";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getConferenceEvents(Object confernceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("confernceId",new Gson().toJson(confernceId));
          gs_json_object_data.method = "getConferenceEvents";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getConferenceEventsBetweenTime(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getConferenceEventsBetweenTime";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getConferenceEventsByFilter(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getConferenceEventsByFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getDiffLog(Object conferenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("conferenceId",new Gson().toJson(conferenceId));
          gs_json_object_data.method = "getDiffLog";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getEventEntries(Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getEventEntries";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getEventEntriesByFilter(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getEventEntriesByFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getEventEntry(Object eventEntryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("eventEntryId",new Gson().toJson(eventEntryId));
          gs_json_object_data.method = "getEventEntry";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getItem(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void removeGuestFromEvent(Object guestId, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("guestId",new Gson().toJson(guestId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "removeGuestFromEvent";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public JsonElement saveConference(Object conference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("conference",new Gson().toJson(conference));
          gs_json_object_data.method = "saveConference";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement saveConferenceEvent(Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "saveConferenceEvent";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void saveEventEntry(Object entry)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entry",new Gson().toJson(entry));
          gs_json_object_data.method = "saveEventEntry";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public JsonElement saveItem(Object item)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.method = "saveItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement searchConferences(Object searchWord)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchWord",new Gson().toJson(searchWord));
          gs_json_object_data.method = "searchConferences";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsConferenceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
