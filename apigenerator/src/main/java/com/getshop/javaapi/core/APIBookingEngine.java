package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIBookingEngine {

      public Communicator transport;

      public APIBookingEngine(Communicator transport){
           this.transport = transport;
      }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement canAddBooking(String gs_multiLevelName, Object bookingsToAdd)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingsToAdd",new Gson().toJson(bookingsToAdd));
          gs_json_object_data.method = "canAddBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement canAddBookings(String gs_multiLevelName, Object bookingsToAdd)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingsToAdd",new Gson().toJson(bookingsToAdd));
          gs_json_object_data.method = "canAddBookings";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeBookingItemAndDateOnBooking(String gs_multiLevelName, Object booking, Object item, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "changeBookingItemAndDateOnBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeBookingItemOnBooking(String gs_multiLevelName, Object booking, Object item)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.method = "changeBookingItemOnBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeBookingItemType(String gs_multiLevelName, Object itemId, Object newTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.args.put("newTypeId",new Gson().toJson(newTypeId));
          gs_json_object_data.method = "changeBookingItemType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeDatesOnBooking(String gs_multiLevelName, Object bookingId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "changeDatesOnBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeDepartmentOnType(String gs_multiLevelName, Object bookingItemTypeId, Object departmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingItemTypeId",new Gson().toJson(bookingItemTypeId));
          gs_json_object_data.args.put("departmentId",new Gson().toJson(departmentId));
          gs_json_object_data.method = "changeDepartmentOnType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeSourceOnBooking(String gs_multiLevelName, Object bookingId, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "changeSourceOnBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void changeTypeOnBooking(String gs_multiLevelName, Object bookingId, Object itemTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("itemTypeId",new Gson().toJson(itemTypeId));
          gs_json_object_data.method = "changeTypeOnBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void checkConsistency(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkConsistency";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement createABookingItemType(String gs_multiLevelName, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createABookingItemType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement deleteBooking(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public void deleteBookingItem(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteBookingItem";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void deleteBookingItemType(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteBookingItemType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void deleteOpeningHours(String gs_multiLevelName, Object repeaterId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("repeaterId",new Gson().toJson(repeaterId));
          gs_json_object_data.method = "deleteOpeningHours";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void forceUnassignBookingInfuture(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "forceUnassignBookingInfuture";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAllAvailbleItems(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAllAvailbleItems";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAllAvailbleItemsWithBookingConsidered(String gs_multiLevelName, Object start, Object end, Object bookingid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("bookingid",new Gson().toJson(bookingid));
          gs_json_object_data.method = "getAllAvailbleItemsWithBookingConsidered";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAllBookings(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllBookings";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAllBookingsByBookingItem(String gs_multiLevelName, Object bookingItemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingItemId",new Gson().toJson(bookingItemId));
          gs_json_object_data.method = "getAllBookingsByBookingItem";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAllBookingsByBookingItemInDateRange(String gs_multiLevelName, Object bookingItemId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingItemId",new Gson().toJson(bookingItemId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAllBookingsByBookingItemInDateRange";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAvailbleItems(String gs_multiLevelName, Object typeId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("typeId",new Gson().toJson(typeId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAvailbleItems";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAvailbleItemsWithBookingConsidered(String gs_multiLevelName, Object typeId, Object start, Object end, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("typeId",new Gson().toJson(typeId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getAvailbleItemsWithBookingConsidered";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getAvailbleItemsWithBookingConsideredAndShuffling(String gs_multiLevelName, Object typeId, Object start, Object end, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("typeId",new Gson().toJson(typeId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getAvailbleItemsWithBookingConsideredAndShuffling";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getBooking";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getBookingItem(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getBookingItem";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getBookingItemType(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getBookingItemType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getBookingItemTypes(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getBookingItemTypes";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getBookingItemTypesWithSystemType(String gs_multiLevelName, Object systemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.method = "getBookingItemTypesWithSystemType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getBookingItems(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getBookingItems";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getConfig(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getConfig";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getDefaultRegistrationRules(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getDefaultRegistrationRules";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getNumberOfAvailable(String gs_multiLevelName, Object itemType, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemType",new Gson().toJson(itemType));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getNumberOfAvailable";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getOpeningHours(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "getOpeningHours";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getOpeningHoursWithType(String gs_multiLevelName, Object itemId, Object timePeriodeType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.args.put("timePeriodeType",new Gson().toJson(timePeriodeType));
          gs_json_object_data.method = "getOpeningHoursWithType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getTimelines(String gs_multiLevelName, Object id, Object startDate, Object endDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("endDate",new Gson().toJson(endDate));
          gs_json_object_data.method = "getTimelines";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement getTimelinesDirect(String gs_multiLevelName, Object start, Object end, Object itemTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("itemTypeId",new Gson().toJson(itemTypeId));
          gs_json_object_data.method = "getTimelinesDirect";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement saveBookingItem(String gs_multiLevelName, Object item)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.method = "saveBookingItem";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking engine management system.<br>
     */
     public void saveDefaultRegistrationRules(String gs_multiLevelName, Object rules)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rules",new Gson().toJson(rules));
          gs_json_object_data.method = "saveDefaultRegistrationRules";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void saveOpeningHours(String gs_multiLevelName, Object time, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("time",new Gson().toJson(time));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "saveOpeningHours";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public void setConfirmationRequired(String gs_multiLevelName, Object confirmationRequired)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("confirmationRequired",new Gson().toJson(confirmationRequired));
          gs_json_object_data.method = "setConfirmationRequired";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking engine management system.<br>
     */
     public JsonElement updateBookingItemType(String gs_multiLevelName, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "updateBookingItemType";
          gs_json_object_data.interfaceName = "core.bookingengine.IBookingEngine";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
