package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsManager {

      public Communicator transport;

      public APIPmsManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void addAddonToCurrentBooking(String gs_multiLevelName, Object itemtypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemtypeId",new Gson().toJson(itemtypeId));
          gs_json_object_data.method = "addAddonToCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void addAddonToRoom(String gs_multiLevelName, Object addon, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("addon",new Gson().toJson(addon));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "addAddonToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void addAddonsToBooking(String gs_multiLevelName, Object type, Object roomId, Object remove)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("remove",new Gson().toJson(remove));
          gs_json_object_data.method = "addAddonsToBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void addAddonsToBookingIgnoreRestriction(String gs_multiLevelName, Object type, Object roomId, Object remove)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("remove",new Gson().toJson(remove));
          gs_json_object_data.method = "addAddonsToBookingIgnoreRestriction";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement addBookingItem(String gs_multiLevelName, Object bookingId, Object item, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "addBookingItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement addBookingItemType(String gs_multiLevelName, Object bookingId, Object item, Object start, Object end, Object guestInfoFromRoom)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("guestInfoFromRoom",new Gson().toJson(guestInfoFromRoom));
          gs_json_object_data.method = "addBookingItemType";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement addCartItemToRoom(String gs_multiLevelName, Object item, Object pmsBookingRoomId, Object addedBy)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("item",new Gson().toJson(item));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("addedBy",new Gson().toJson(addedBy));
          gs_json_object_data.method = "addCartItemToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void addComment(String gs_multiLevelName, Object bookingId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "addComment";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void addCommentToRoom(String gs_multiLevelName, Object roomId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "addCommentToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void addProductToRoom(String gs_multiLevelName, Object productId, Object pmsRoomId, Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "addProductToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void addRepeatingData(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "addRepeatingData";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement addSuggestedUserToBooking(String gs_multiLevelName, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "addSuggestedUserToBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement addToWaitingList(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "addToWaitingList";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void addToWorkSpace(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "addToWorkSpace";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public void attachOrderToBooking(String gs_multiLevelName, Object bookingId, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "attachOrderToBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void cancelRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "cancelRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement changeDates(String gs_multiLevelName, Object roomId, Object bookingId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "changeDates";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void changeInvoiceDate(String gs_multiLevelName, Object roomId, Object newDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("newDate",new Gson().toJson(newDate));
          gs_json_object_data.method = "changeInvoiceDate";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void checkDoorStatusControl(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkDoorStatusControl";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void checkForDeadCodesApac(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkForDeadCodesApac";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void checkForRoomsToClose(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkForRoomsToClose";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void checkIfGuestHasArrived(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkIfGuestHasArrived";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void checkInRoom(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "checkInRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void checkOutRoom(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "checkOutRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void cleanupOrdersThatDoesNoLongerExists(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "cleanupOrdersThatDoesNoLongerExists";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement closeItem(String gs_multiLevelName, Object id, Object start, Object end, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "closeItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void completeCareTakerJob(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "completeCareTakerJob";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement completeCurrentBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "completeCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void confirmBooking(String gs_multiLevelName, Object bookingId, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "confirmBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement convertTextDate(String gs_multiLevelName, Object text)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.method = "convertTextDate";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement createAddonsThatCanBeAddedToRoom(String gs_multiLevelName, Object productId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "createAddonsThatCanBeAddedToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void createAllVirtualOrders(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "createAllVirtualOrders";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void createChannel(String gs_multiLevelName, Object channel)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("channel",new Gson().toJson(channel));
          gs_json_object_data.method = "createChannel";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void createNewPricePlan(String gs_multiLevelName, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "createNewPricePlan";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void createNewUserOnBooking(String gs_multiLevelName, Object bookingId, Object name, Object orgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("orgId",new Gson().toJson(orgId));
          gs_json_object_data.method = "createNewUserOnBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement createOrder(String gs_multiLevelName, Object bookingId, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "createOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement createOrderFromCheckout(String gs_multiLevelName, Object row, Object paymentMethodId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("row",new Gson().toJson(row));
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "createOrderFromCheckout";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement createPrepaymentOrder(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "createPrepaymentOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement createUser(String gs_multiLevelName, Object newUser)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("newUser",new Gson().toJson(newUser));
          gs_json_object_data.method = "createUser";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void deleteAllBookings(String gs_multiLevelName, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "deleteAllBookings";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void deleteBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "deleteBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void deleteDeliveryLogEntry(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteDeliveryLogEntry";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void deletePmsFilter(String gs_multiLevelName, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "deletePmsFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void deletePricePlan(String gs_multiLevelName, Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "deletePricePlan";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public void detachOrderFromBooking(String gs_multiLevelName, Object bookingId, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "detachOrderFromBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void doChargeCardFromAutoBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "doChargeCardFromAutoBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void doNotification(String gs_multiLevelName, Object key, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "doNotification";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement doesOrderCorrolateToRoom(String gs_multiLevelName, Object pmsBookingRoomsId, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomsId",new Gson().toJson(pmsBookingRoomsId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "doesOrderCorrolateToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void endRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "endRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void endRoomWithDate(String gs_multiLevelName, Object pmsRoomId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "endRoomWithDate";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void failedChargeCard(String gs_multiLevelName, Object orderId, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "failedChargeCard";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement findRelatedByUserId(String gs_multiLevelName, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "findRelatedByUserId";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement findRelatedGuests(String gs_multiLevelName, Object guest)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("guest",new Gson().toJson(guest));
          gs_json_object_data.method = "findRelatedGuests";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void forceMarkRoomAsCleaned(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "forceMarkRoomAsCleaned";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void freezeSubscription(String gs_multiLevelName, Object pmsBookingRoomId, Object freezeUntil)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("freezeUntil",new Gson().toJson(freezeUntil));
          gs_json_object_data.method = "freezeSubscription";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement generateNewCodeForRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "generateNewCodeForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void generatePgaAccess(String gs_multiLevelName, Object pmsBookingId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "generatePgaAccess";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement generateRepeatDateRanges(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "generateRepeatDateRanges";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement getAccesories(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAccesories";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getActivitiesEntries(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getActivitiesEntries";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAdditionalInfo(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "getAdditionalInfo";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getAdditionalTypeInformation(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAdditionalTypeInformation";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getAdditionalTypeInformationById(String gs_multiLevelName, Object typeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("typeId",new Gson().toJson(typeId));
          gs_json_object_data.method = "getAdditionalTypeInformationById";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAddonsAvailable(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAddonsAvailable";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement getAddonsForRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "getAddonsForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement getAddonsWithDiscount(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getAddonsWithDiscount";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement getAddonsWithDiscountForBooking(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getAddonsWithDiscountForBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllAdditionalInformationOnRooms(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllAdditionalInformationOnRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllBookings(String gs_multiLevelName, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "getAllBookings";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getAllBookingsForLoggedOnUser(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllBookingsForLoggedOnUser";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getAllBookingsUnsecure(String gs_multiLevelName, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "getAllBookingsUnsecure";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllCrmUsers(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllCrmUsers";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllPmsFilters(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllPmsFilters";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getAllRoomTypes(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAllRoomTypes";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllRoomsNeedCleaningToday(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllRoomsNeedCleaningToday";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllRoomsOnOrder(String gs_multiLevelName, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getAllRoomsOnOrder";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement getAllRoomsThatHasAddonsOfType(String gs_multiLevelName, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getAllRoomsThatHasAddonsOfType";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement getAllRoomsWithUnsettledAmount(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getAllRoomsWithUnsettledAmount";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getAllUsers(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getAllUsers";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getAvailabilityForType(String gs_multiLevelName, Object bookingItemId, Object startTime, Object endTime, Object intervalInMinutes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingItemId",new Gson().toJson(bookingItemId));
          gs_json_object_data.args.put("startTime",new Gson().toJson(startTime));
          gs_json_object_data.args.put("endTime",new Gson().toJson(endTime));
          gs_json_object_data.args.put("intervalInMinutes",new Gson().toJson(intervalInMinutes));
          gs_json_object_data.method = "getAvailabilityForType";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getBookingFromBookingEngineId(String gs_multiLevelName, Object bookingEngineId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingEngineId",new Gson().toJson(bookingEngineId));
          gs_json_object_data.method = "getBookingFromBookingEngineId";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getBookingFromRoom(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getBookingFromRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getBookingFromRoomIgnoreDeleted(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "getBookingFromRoomIgnoreDeleted";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getBookingSummaryText(String gs_multiLevelName, Object pmsBookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.method = "getBookingSummaryText";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getBookingWithOrderId(String gs_multiLevelName, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getBookingWithOrderId";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If the autoassigned routines has failed for some reason, this will reset the status
     * and let the system retry to autoassigned the failed rooms.
     */
     public JsonElement getBookingsFromGroupInvoicing(String gs_multiLevelName, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getBookingsFromGroupInvoicing";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement getBookingsWithUnsettledAmountBetween(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getBookingsWithUnsettledAmountBetween";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement getCardsToSave(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getCardsToSave";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getCareTakerJob(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getCareTakerJob";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getCareTakerJobs(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getCareTakerJobs";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getChannelMatrix(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getChannelMatrix";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getCleaningHistoryForItem(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "getCleaningHistoryForItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getCleaningStatistics(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getCleaningStatistics";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getConferenceData(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getConferenceData";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getConfiguration(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getConfiguration";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getContract(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getContract";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getContractByLanguage(String gs_multiLevelName, Object language)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("language",new Gson().toJson(language));
          gs_json_object_data.method = "getContractByLanguage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getCurrenctContract(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getCurrenctContract";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getCurrentBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getDefaultDateRange(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getDefaultDateRange";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getDefaultMessage(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getDefaultMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getDeliveryLog(String gs_multiLevelName, Object productIds, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("productIds",new Gson().toJson(productIds));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getDeliveryLog";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getDeliveryLogByView(String gs_multiLevelName, Object viewId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getDeliveryLogByView";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getEarliestEndDate(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getEarliestEndDate";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * If the autoassigned routines has failed for some reason, this will reset the status
     * and let the system retry to autoassigned the failed rooms.
     */
     public JsonElement getExtraOrderIds(String gs_multiLevelName, Object pmsBookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.method = "getExtraOrderIds";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getFutureConferenceData(String gs_multiLevelName, Object fromDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("fromDate",new Gson().toJson(fromDate));
          gs_json_object_data.method = "getFutureConferenceData";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement getGroupedConferenceData(String gs_multiLevelName, Object fromDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("fromDate",new Gson().toJson(fromDate));
          gs_json_object_data.method = "getGroupedConferenceData";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getIntervalAvailability(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getIntervalAvailability";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getItemsForView(String gs_multiLevelName, Object viewId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("viewId",new Gson().toJson(viewId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "getItemsForView";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getLogEntries(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getLogEntries";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getMessage(String gs_multiLevelName, Object bookingId, Object key)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("key",new Gson().toJson(key));
          gs_json_object_data.method = "getMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getMyRooms(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getMyRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getNumberOfAvailable(String gs_multiLevelName, Object itemType, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemType",new Gson().toJson(itemType));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getNumberOfAvailable";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getNumberOfCustomers(String gs_multiLevelName, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "getNumberOfCustomers";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getPmsBookingFilter(String gs_multiLevelName, Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "getPmsBookingFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement getPrecastedRoom(String gs_multiLevelName, Object roomId, Object bookingItemTypeId, Object from, Object to)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("bookingItemTypeId",new Gson().toJson(bookingItemTypeId));
          gs_json_object_data.args.put("from",new Gson().toJson(from));
          gs_json_object_data.args.put("to",new Gson().toJson(to));
          gs_json_object_data.method = "getPrecastedRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getPriceForRoomWhenBooking(String gs_multiLevelName, Object start, Object end, Object itemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("itemType",new Gson().toJson(itemType));
          gs_json_object_data.method = "getPriceForRoomWhenBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getPrices(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getPrices";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getPricesByCode(String gs_multiLevelName, Object code, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getPricesByCode";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getRoomForItem(String gs_multiLevelName, Object itemId, Object atTime)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.args.put("atTime",new Gson().toJson(atTime));
          gs_json_object_data.method = "getRoomForItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getRoomsNeedingCheckoutCleaning(String gs_multiLevelName, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getRoomsNeedingCheckoutCleaning";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getRoomsNeedingIntervalCleaning(String gs_multiLevelName, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getRoomsNeedingIntervalCleaning";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getRoomsNeedingIntervalCleaningSimple(String gs_multiLevelName, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getRoomsNeedingIntervalCleaningSimple";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getRoomsToSwap(String gs_multiLevelName, Object roomId, Object moveToType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("moveToType",new Gson().toJson(moveToType));
          gs_json_object_data.method = "getRoomsToSwap";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getSimpleCleaningOverview(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getSimpleCleaningOverview";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getSimpleInventoryList(String gs_multiLevelName, Object roomName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomName",new Gson().toJson(roomName));
          gs_json_object_data.method = "getSimpleInventoryList";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getSimpleRooms(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getSimpleRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getSimpleRoomsForGroup(String gs_multiLevelName, Object bookingEngineId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingEngineId",new Gson().toJson(bookingEngineId));
          gs_json_object_data.method = "getSimpleRoomsForGroup";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getStatistics(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getStatistics";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement getSummary(String gs_multiLevelName, Object pmsBookingId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getSummary";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement getSummaryForAllRooms(String gs_multiLevelName, Object pmsBookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.method = "getSummaryForAllRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement getSummaryWithoutAccrued(String gs_multiLevelName, Object pmsBookingId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getSummaryWithoutAccrued";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getWorkSpaceRooms(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getWorkSpaceRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement getpriceCodes(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getpriceCodes";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement hasLockSystemActive(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "hasLockSystemActive";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public JsonElement hasNoBookings(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "hasNoBookings";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void hourlyProcessor(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "hourlyProcessor";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement initBookingRules(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "initBookingRules";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement isActive(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "isActive";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement isClean(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "isClean";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement isUsedToday(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "isUsedToday";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void logEntry(String gs_multiLevelName, Object logText, Object bookingId, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("logText",new Gson().toJson(logText));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "logEntry";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void logEntryObject(String gs_multiLevelName, Object log)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("log",new Gson().toJson(log));
          gs_json_object_data.method = "logEntryObject";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void markAddonDelivered(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "markAddonDelivered";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void markIgnoreUnsettledAmount(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "markIgnoreUnsettledAmount";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void markKeyDeliveredForAllEndedRooms(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "markKeyDeliveredForAllEndedRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void markOtaPaymentsAutomaticallyPaidOnCheckin(String gs_multiLevelName, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "markOtaPaymentsAutomaticallyPaidOnCheckin";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void markRoomAsCleaned(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "markRoomAsCleaned";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void markRoomAsCleanedWithoutLogging(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "markRoomAsCleanedWithoutLogging";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void markRoomDirty(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "markRoomDirty";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void massUpdatePrices(String gs_multiLevelName, Object price, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("price",new Gson().toJson(price));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "massUpdatePrices";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void mergeBookingsOnOrders(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "mergeBookingsOnOrders";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public void moveAllOnUserToUser(String gs_multiLevelName, Object tomainuser, Object secondaryuser)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("tomainuser",new Gson().toJson(tomainuser));
          gs_json_object_data.args.put("secondaryuser",new Gson().toJson(secondaryuser));
          gs_json_object_data.method = "moveAllOnUserToUser";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement moveRoomToBooking(String gs_multiLevelName, Object roomId, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "moveRoomToBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void orderChanged(String gs_multiLevelName, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "orderChanged";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void orderCreated(String gs_multiLevelName, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "orderCreated";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void printCode(String gs_multiLevelName, Object gdsDeviceId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("gdsDeviceId",new Gson().toJson(gdsDeviceId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "printCode";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void processor(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "processor";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public void recheckOrdersAddedToBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "recheckOrdersAddedToBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void reinstateStay(String gs_multiLevelName, Object pmsBookingRoomId, Object minutes)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("minutes",new Gson().toJson(minutes));
          gs_json_object_data.method = "reinstateStay";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void removeAddonFromRoom(String gs_multiLevelName, Object id, Object pmsBookingRooms)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("pmsBookingRooms",new Gson().toJson(pmsBookingRooms));
          gs_json_object_data.method = "removeAddonFromRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void removeAddonFromRoomById(String gs_multiLevelName, Object addonId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("addonId",new Gson().toJson(addonId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "removeAddonFromRoomById";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void removeCareTakerJob(String gs_multiLevelName, Object jobId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("jobId",new Gson().toJson(jobId));
          gs_json_object_data.method = "removeCareTakerJob";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void removeChannel(String gs_multiLevelName, Object channel)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("channel",new Gson().toJson(channel));
          gs_json_object_data.method = "removeChannel";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement removeFromBooking(String gs_multiLevelName, Object bookingId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "removeFromBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void removeFromCurrentBooking(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "removeFromCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement removeFromWaitingList(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "removeFromWaitingList";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void removePgaAccess(String gs_multiLevelName, Object pmsBookingId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "removePgaAccess";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void removeProductFromRoom(String gs_multiLevelName, Object pmsBookingRoomId, Object productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "removeProductFromRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void reportMissingInventory(String gs_multiLevelName, Object inventories, Object itemId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("inventories",new Gson().toJson(inventories));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "reportMissingInventory";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If the autoassigned routines has failed for some reason, this will reset the status
     * and let the system retry to autoassigned the failed rooms.
     */
     public void resetCheckingAutoAssignedStatus(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "resetCheckingAutoAssignedStatus";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * If the autoassigned routines has failed for some reason, this will reset the status
     * and let the system retry to autoassigned the failed rooms.
     */
     public void resetDeparmentsOnOrders(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "resetDeparmentsOnOrders";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void resetPriceForRoom(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "resetPriceForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void returnedKey(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "returnedKey";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public JsonElement saveAccessory(String gs_multiLevelName, Object accessory)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("accessory",new Gson().toJson(accessory));
          gs_json_object_data.method = "saveAccessory";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void saveAdditionalTypeInformation(String gs_multiLevelName, Object info)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("info",new Gson().toJson(info));
          gs_json_object_data.method = "saveAdditionalTypeInformation";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void saveBooking(String gs_multiLevelName, Object booking)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.method = "saveBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void saveCareTakerJob(String gs_multiLevelName, Object job)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("job",new Gson().toJson(job));
          gs_json_object_data.method = "saveCareTakerJob";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void saveConferenceData(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "saveConferenceData";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void saveConfiguration(String gs_multiLevelName, Object notifications)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("notifications",new Gson().toJson(notifications));
          gs_json_object_data.method = "saveConfiguration";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void saveFilter(String gs_multiLevelName, Object name, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "saveFilter";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendCode(String gs_multiLevelName, Object prefix, Object phoneNumber, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phoneNumber",new Gson().toJson(phoneNumber));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "sendCode";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendConfirmation(String gs_multiLevelName, Object email, Object bookingId, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "sendConfirmation";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendMessage(String gs_multiLevelName, Object bookingId, Object email, Object title, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendMessage";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendMessageOnRoom(String gs_multiLevelName, Object email, Object title, Object message, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "sendMessageOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void sendMessageToAllTodaysGuests(String gs_multiLevelName, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendMessageToAllTodaysGuests";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void sendMissingPayment(String gs_multiLevelName, Object orderId, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "sendMissingPayment";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendPaymentLink(String gs_multiLevelName, Object orderId, Object bookingId, Object email, Object prefix, Object phone)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phone",new Gson().toJson(phone));
          gs_json_object_data.method = "sendPaymentLink";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendPaymentLinkWithText(String gs_multiLevelName, Object orderId, Object bookingId, Object email, Object prefix, Object phone, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phone",new Gson().toJson(phone));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendPaymentLinkWithText";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendPaymentRequest(String gs_multiLevelName, Object bookingId, Object email, Object prefix, Object phone, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phone",new Gson().toJson(phone));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendPaymentRequest";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendSmsOnRoom(String gs_multiLevelName, Object prefix, Object phone, Object message, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phone",new Gson().toJson(phone));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "sendSmsOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void sendSmsToGuest(String gs_multiLevelName, Object guestId, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("guestId",new Gson().toJson(guestId));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendSmsToGuest";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void sendStatistics(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "sendStatistics";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement setBestCouponChoiceForCurrentBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "setBestCouponChoiceForCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void setBooking(String gs_multiLevelName, Object addons)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("addons",new Gson().toJson(addons));
          gs_json_object_data.method = "setBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void setBookingByAdmin(String gs_multiLevelName, Object booking, Object keepRoomPrices)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.args.put("keepRoomPrices",new Gson().toJson(keepRoomPrices));
          gs_json_object_data.method = "setBookingByAdmin";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement setBookingItem(String gs_multiLevelName, Object roomId, Object bookingId, Object itemId, Object split)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.args.put("split",new Gson().toJson(split));
          gs_json_object_data.method = "setBookingItem";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement setBookingItemAndDate(String gs_multiLevelName, Object roomId, Object itemId, Object split, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.args.put("split",new Gson().toJson(split));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "setBookingItemAndDate";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void setCurrentBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "setCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void setDefaultAddons(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "setDefaultAddons";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void setGuestOnRoom(String gs_multiLevelName, Object guests, Object bookingId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("guests",new Gson().toJson(guests));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "setGuestOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void setGuestOnRoomWithoutModifyingAddons(String gs_multiLevelName, Object guests, Object bookingId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("guests",new Gson().toJson(guests));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "setGuestOnRoomWithoutModifyingAddons";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void setNewCleaningIntervalOnRoom(String gs_multiLevelName, Object roomId, Object interval)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("interval",new Gson().toJson(interval));
          gs_json_object_data.method = "setNewCleaningIntervalOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement setNewRoomType(String gs_multiLevelName, Object roomId, Object bookingId, Object newType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("newType",new Gson().toJson(newType));
          gs_json_object_data.method = "setNewRoomType";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Key = date / day
     * @return
     */
     public void setNewStartDateAndAssignToRoom(String gs_multiLevelName, Object roomId, Object newStartDate, Object bookingItemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("newStartDate",new Gson().toJson(newStartDate));
          gs_json_object_data.args.put("bookingItemId",new Gson().toJson(bookingItemId));
          gs_json_object_data.method = "setNewStartDateAndAssignToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement setPrices(String gs_multiLevelName, Object code, Object prices)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("prices",new Gson().toJson(prices));
          gs_json_object_data.method = "setPrices";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void simpleCompleteCurrentBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "simpleCompleteCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void splitBooking(String gs_multiLevelName, Object roomIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomIds",new Gson().toJson(roomIds));
          gs_json_object_data.method = "splitBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void splitStay(String gs_multiLevelName, Object roomId, Object splitDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("splitDate",new Gson().toJson(splitDate));
          gs_json_object_data.method = "splitStay";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement startBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "startBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement swapRoom(String gs_multiLevelName, Object roomId, Object roomIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("roomIds",new Gson().toJson(roomIds));
          gs_json_object_data.method = "swapRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void toggleAddon(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "toggleAddon";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public void toggleAutoCreateOrders(String gs_multiLevelName, Object bookingId, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "toggleAutoCreateOrders";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void togglePrioritizedRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "togglePrioritizedRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void transferFromOldCodeToNew(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "transferFromOldCodeToNew";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public void transferTicketsAsAddons(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "transferTicketsAsAddons";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void tryAddToEngine(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "tryAddToEngine";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void unConfirmBooking(String gs_multiLevelName, Object bookingId, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "unConfirmBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public void undeleteBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "undeleteBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void undoCheckOut(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "undoCheckOut";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void undoLastCleaning(String gs_multiLevelName, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "undoLastCleaning";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void updateAdditionalInformationOnRooms(String gs_multiLevelName, Object info)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("info",new Gson().toJson(info));
          gs_json_object_data.method = "updateAdditionalInformationOnRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Key = date / day
     * @return
     */
     public void updateAddons(String gs_multiLevelName, Object items, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("items",new Gson().toJson(items));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "updateAddons";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void updateAddonsBasedOnGuestCount(String gs_multiLevelName, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "updateAddonsBasedOnGuestCount";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void updateAddonsCountToBooking(String gs_multiLevelName, Object type, Object roomId, Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "updateAddonsCountToBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void updateCommentOnBooking(String gs_multiLevelName, Object bookingId, Object commentId, Object newText)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("commentId",new Gson().toJson(commentId));
          gs_json_object_data.args.put("newText",new Gson().toJson(newText));
          gs_json_object_data.method = "updateCommentOnBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns true if there are anything in
     * the order that related to the specified room id.
     *
     * @param pmsBookingRoomsId
     * @param orderId
     * @return
     */
     public JsonElement updateOrderDetails(String gs_multiLevelName, Object bookingId, Object orderId, Object preview)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("preview",new Gson().toJson(preview));
          gs_json_object_data.method = "updateOrderDetails";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void updatePriceMatrixOnRoom(String gs_multiLevelName, Object pmsBookingRoomId, Object priceMatrix)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("priceMatrix",new Gson().toJson(priceMatrix));
          gs_json_object_data.method = "updatePriceMatrixOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement updatePrices(String gs_multiLevelName, Object prices)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("prices",new Gson().toJson(prices));
          gs_json_object_data.method = "updatePrices";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement updateRepeatingDataForBooking(String gs_multiLevelName, Object data, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "updateRepeatingDataForBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Whenever a card has been fetched using the automated process this function is called
     * DO NOT CHANGE IT!
     * @param bookingId
     */
     public JsonElement updateRoomByUser(String gs_multiLevelName, Object bookingId, Object room)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.method = "updateRoomByUser";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public void warnFailedBooking(String gs_multiLevelName, Object booking)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.method = "warnFailedBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement willAutoDelete(String gs_multiLevelName, Object pmsBookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.method = "willAutoDelete";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Use -1 as count to make it depend on guest count addons (1 if not depends on guest count)
     * @param productId
     * @param pmsRoomId
     * @param count
     */
     public void wubookCreditCardIsInvalid(String gs_multiLevelName, Object bookingId, Object reason)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("reason",new Gson().toJson(reason));
          gs_json_object_data.method = "wubookCreditCardIsInvalid";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsManager";
          String result = transport.send(gs_json_object_data);
     }

}
