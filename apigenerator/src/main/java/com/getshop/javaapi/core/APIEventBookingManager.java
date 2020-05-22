package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIEventBookingManager {

      public Communicator transport;

      public APIEventBookingManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addExternalCertificate(String gs_multiLevelName, Object userId, Object fileId, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "addExternalCertificate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addLocationFilter(String gs_multiLevelName, Object locationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("locationId",new Gson().toJson(locationId));
          gs_json_object_data.method = "addLocationFilter";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addManuallyParticipatedEvent(String gs_multiLevelName, Object man)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("man",new Gson().toJson(man));
          gs_json_object_data.method = "addManuallyParticipatedEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addPersonalIdToEvent(String gs_multiLevelName, Object eventId, Object userId, Object personalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("personalId",new Gson().toJson(personalId));
          gs_json_object_data.method = "addPersonalIdToEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addTypeFilter(String gs_multiLevelName, Object bookingItemTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingItemTypeId",new Gson().toJson(bookingItemTypeId));
          gs_json_object_data.method = "addTypeFilter";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addUserComment(String gs_multiLevelName, Object userId, Object eventId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "addUserComment";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addUserToEvent(String gs_multiLevelName, Object eventId, Object userId, Object silent, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("silent",new Gson().toJson(silent));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "addUserToEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void bookCurrentUserToEvent(String gs_multiLevelName, Object eventId, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "bookCurrentUserToEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement canDownloadCertificate(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "canDownloadCertificate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void cancelEvent(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "cancelEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void checkToSendReminders(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "checkToSendReminders";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void clearEventBookingManagerForAllData(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "clearEventBookingManagerForAllData";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void clearFilters(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "clearFilters";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void clearLocationFilters(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "clearLocationFilters";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createEvent(String gs_multiLevelName, Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "createEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement decodePersonalIds(String gs_multiLevelName, Object eventId, Object privateKey)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("privateKey",new Gson().toJson(privateKey));
          gs_json_object_data.method = "decodePersonalIds";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteCertificate(String gs_multiLevelName, Object certificateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("certificateId",new Gson().toJson(certificateId));
          gs_json_object_data.method = "deleteCertificate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteEvent(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "deleteEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteExternalCertificates(String gs_multiLevelName, Object userId, Object fileId, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "deleteExternalCertificates";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteInvoiceGroup(String gs_multiLevelName, Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "deleteInvoiceGroup";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteLocation(String gs_multiLevelName, Object locationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("locationId",new Gson().toJson(locationId));
          gs_json_object_data.method = "deleteLocation";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteManullyParticipatedEvent(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteManullyParticipatedEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteReminderTemplate(String gs_multiLevelName, Object templateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("templateId",new Gson().toJson(templateId));
          gs_json_object_data.method = "deleteReminderTemplate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteSubLocation(String gs_multiLevelName, Object subLocationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("subLocationId",new Gson().toJson(subLocationId));
          gs_json_object_data.method = "deleteSubLocation";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteUserComment(String gs_multiLevelName, Object userId, Object eventId, Object commentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("commentId",new Gson().toJson(commentId));
          gs_json_object_data.method = "deleteUserComment";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getActiveLocations(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getActiveLocations";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllEvents(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getAllEvents";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllLocations(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getAllLocations";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBookingItemTypeByPageId(String gs_multiLevelName, Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getBookingItemTypeByPageId";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBookingItemTypes(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getBookingItemTypes";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBookingTypeMetaData(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getBookingTypeMetaData";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBookingsByPageId(String gs_multiLevelName, Object pageId, Object showOnlyNew)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("showOnlyNew",new Gson().toJson(showOnlyNew));
          gs_json_object_data.method = "getBookingsByPageId";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCertificate(String gs_multiLevelName, Object certificateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("certificateId",new Gson().toJson(certificateId));
          gs_json_object_data.method = "getCertificate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCertificateForEvent(String gs_multiLevelName, Object eventId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getCertificateForEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCertificates(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getCertificates";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getCompaniesWhereNoCanditasHasCompletedTests(String gs_multiLevelName, Object testIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("testIds",new Gson().toJson(testIds));
          gs_json_object_data.method = "getCompaniesWhereNoCanditasHasCompletedTests";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEvent(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventByPageId(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getEventByPageId";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventLog(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getEventLog";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventRequest(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getEventRequest";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEvents(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getEvents";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsByLocation(String gs_multiLevelName, Object locationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("locationId",new Gson().toJson(locationId));
          gs_json_object_data.method = "getEventsByLocation";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsByType(String gs_multiLevelName, Object eventTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventTypeId",new Gson().toJson(eventTypeId));
          gs_json_object_data.method = "getEventsByType";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsForDay(String gs_multiLevelName, Object year, Object month, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getEventsForDay";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsForPdf(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getEventsForPdf";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsForUser(String gs_multiLevelName, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getEventsForUser";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEventsWhereEndDateBetween(String gs_multiLevelName, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getEventsWhereEndDateBetween";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getExternalCertificates(String gs_multiLevelName, Object userId, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getExternalCertificates";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getFilteredLocations(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getFilteredLocations";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getFromDateTimeFilter(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getFromDateTimeFilter";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getInterests(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getInterests";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getInvoiceGroup(String gs_multiLevelName, Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "getInvoiceGroup";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getInvoiceGroups(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getInvoiceGroups";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLocation(String gs_multiLevelName, Object locationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("locationId",new Gson().toJson(locationId));
          gs_json_object_data.method = "getLocation";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getMandatoryCourses(String gs_multiLevelName, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getMandatoryCourses";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getManuallyAddedEventParticipant(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getManuallyAddedEventParticipant";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getManuallyAddedEvents(String gs_multiLevelName, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getManuallyAddedEvents";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getMyEvents(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getMyEvents";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPriceForEventType(String gs_multiLevelName, Object bookingItemTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingItemTypeId",new Gson().toJson(bookingItemTypeId));
          gs_json_object_data.method = "getPriceForEventType";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getPriceForEventTypeAndUserId(String gs_multiLevelName, Object eventId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getPriceForEventTypeAndUserId";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getReminder(String gs_multiLevelName, Object reminderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("reminderId",new Gson().toJson(reminderId));
          gs_json_object_data.method = "getReminder";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getReminderTemplate(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getReminderTemplate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getReminderTemplates(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getReminderTemplates";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getReminders(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getReminders";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSource(String gs_multiLevelName, Object eventId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getSource";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStatistic(String gs_multiLevelName, Object startDate, Object stopDate, Object groupIds, Object eventTypeIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("groupIds",new Gson().toJson(groupIds));
          gs_json_object_data.args.put("eventTypeIds",new Gson().toJson(eventTypeIds));
          gs_json_object_data.method = "getStatistic";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStatisticGroupedByLocations(String gs_multiLevelName, Object startDate, Object stopDate, Object groupIds, Object eventTypeIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("groupIds",new Gson().toJson(groupIds));
          gs_json_object_data.args.put("eventTypeIds",new Gson().toJson(eventTypeIds));
          gs_json_object_data.method = "getStatisticGroupedByLocations";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getToDateTimeFilter(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getToDateTimeFilter";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getUsersForEvent(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getUsersForEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getUsersForEventWaitinglist(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "getUsersForEventWaitinglist";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void handleEventRequest(String gs_multiLevelName, Object id, Object accepted)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("accepted",new Gson().toJson(accepted));
          gs_json_object_data.method = "handleEventRequest";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement hasCompletedMandatoryEvent(String gs_multiLevelName, Object eventTypeId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventTypeId",new Gson().toJson(eventTypeId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "hasCompletedMandatoryEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement hasForcedMandatoryTest(String gs_multiLevelName, Object eventTypeId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventTypeId",new Gson().toJson(eventTypeId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "hasForcedMandatoryTest";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement isUserSignedUpForEvent(String gs_multiLevelName, Object eventId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "isUserSignedUpForEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement isWaitingForConfirmation(String gs_multiLevelName, Object eventId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "isWaitingForConfirmation";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void markAsReady(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "markAsReady";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markQuestBackSent(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "markQuestBackSent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void moveUserToEvent(String gs_multiLevelName, Object userId, Object fromEventId, Object toEventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("fromEventId",new Gson().toJson(fromEventId));
          gs_json_object_data.args.put("toEventId",new Gson().toJson(toEventId));
          gs_json_object_data.method = "moveUserToEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void registerEventIntrest(String gs_multiLevelName, Object interest)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("interest",new Gson().toJson(interest));
          gs_json_object_data.method = "registerEventIntrest";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeInterest(String gs_multiLevelName, Object bookingItemTypeId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingItemTypeId",new Gson().toJson(bookingItemTypeId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "removeInterest";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeUserFromEvent(String gs_multiLevelName, Object eventId, Object userId, Object silent)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("silent",new Gson().toJson(silent));
          gs_json_object_data.method = "removeUserFromEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveBookingTypeMetaData(String gs_multiLevelName, Object bookingItemTypeMetadata)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingItemTypeMetadata",new Gson().toJson(bookingItemTypeMetadata));
          gs_json_object_data.method = "saveBookingTypeMetaData";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveCertificate(String gs_multiLevelName, Object certificate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("certificate",new Gson().toJson(certificate));
          gs_json_object_data.method = "saveCertificate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveEvent(String gs_multiLevelName, Object event)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("event",new Gson().toJson(event));
          gs_json_object_data.method = "saveEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveGroupInvoicing(String gs_multiLevelName, Object invoiceGroup)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("invoiceGroup",new Gson().toJson(invoiceGroup));
          gs_json_object_data.method = "saveGroupInvoicing";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveLocation(String gs_multiLevelName, Object location)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("location",new Gson().toJson(location));
          gs_json_object_data.method = "saveLocation";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveReminderTemplate(String gs_multiLevelName, Object template)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("template",new Gson().toJson(template));
          gs_json_object_data.method = "saveReminderTemplate";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void sendDiplomas(String gs_multiLevelName, Object reminder, Object userid, Object base64)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("reminder",new Gson().toJson(reminder));
          gs_json_object_data.args.put("userid",new Gson().toJson(userid));
          gs_json_object_data.args.put("base64",new Gson().toJson(base64));
          gs_json_object_data.method = "sendDiplomas";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void sendReminder(String gs_multiLevelName, Object reminder)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("reminder",new Gson().toJson(reminder));
          gs_json_object_data.method = "sendReminder";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setForcedMandatoryAccess(String gs_multiLevelName, Object userId, Object bookingItemIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("bookingItemIds",new Gson().toJson(bookingItemIds));
          gs_json_object_data.method = "setForcedMandatoryAccess";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setGroupInvoiceingStatus(String gs_multiLevelName, Object eventId, Object userId, Object groupId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("groupId",new Gson().toJson(groupId));
          gs_json_object_data.method = "setGroupInvoiceingStatus";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setParticipationStatus(String gs_multiLevelName, Object eventId, Object userId, Object status)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("status",new Gson().toJson(status));
          gs_json_object_data.method = "setParticipationStatus";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setTimeFilter(String gs_multiLevelName, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "setTimeFilter";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void startScheduler(String gs_multiLevelName, Object scheduler)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("scheduler",new Gson().toJson(scheduler));
          gs_json_object_data.method = "startScheduler";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void toggleHide(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "toggleHide";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void toggleLocked(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "toggleLocked";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void transferUserFromWaitingToEvent(String gs_multiLevelName, Object userId, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "transferUserFromWaitingToEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void unCancelEvent(String gs_multiLevelName, Object eventId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("eventId",new Gson().toJson(eventId));
          gs_json_object_data.method = "unCancelEvent";
          gs_json_object_data.interfaceName = "core.eventbooking.IEventBookingManager";
          String result = transport.send(gs_json_object_data);
     }

}
