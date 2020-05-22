package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsPaymentTerminal {

      public Communicator transport;

      public APIPmsPaymentTerminal(Communicator transport){
           this.transport = transport;
      }

     /**
     * Property management system.<br>
     */
     public void addProductToRoom(String gs_multiLevelName, Object productId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "addProductToRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Change the number of guests on a room.
     * @param pmsBookingRoomId
     * @param count
     * @return The new price for the room.
     */
     public JsonElement changeGuestCountOnRoom(String gs_multiLevelName, Object pmsBookingRoomId, Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "changeGuestCountOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a map of types that the booking can be changed to with number of rooms available accioated to it.
     * @param pmsBookingRoomId
     * @return
     */
     public JsonElement changeRoomTypeOnRoom(String gs_multiLevelName, Object pmsBookingRoomId, Object newTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.args.put("newTypeId",new Gson().toJson(newTypeId));
          gs_json_object_data.method = "changeRoomTypeOnRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement findBookings(String gs_multiLevelName, Object phoneNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("phoneNumber",new Gson().toJson(phoneNumber));
          gs_json_object_data.method = "findBookings";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all rooms available as a map where the room size (number of guest) is returned as second result.
     * Sorted as largest room first.
     * @param data
     * @return
     */
     public JsonElement getMaxNumberOfRooms(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "getMaxNumberOfRooms";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement getOrderSummary(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getOrderSummary";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a map of types that the booking can be changed to with number of rooms available accioated to it.
     * @param pmsBookingRoomId
     * @return
     */
     public JsonElement getRoomTypesThatRoomCanBeChangedTo(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "getRoomTypesThatRoomCanBeChangedTo";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Property management system.<br>
     */
     public JsonElement payIndividualRoom(String gs_multiLevelName, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "payIndividualRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Change the number of guests on a room.
     * @param pmsBookingRoomId
     * @param count
     * @return The new price for the room.
     */
     public void printReciept(String gs_multiLevelName, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "printReciept";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public void removeProductFromRoom(String gs_multiLevelName, Object productId, Object pmsBookingRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("pmsBookingRoomId",new Gson().toJson(pmsBookingRoomId));
          gs_json_object_data.method = "removeProductFromRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Property management system.<br>
     */
     public JsonElement startBooking(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "startBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Change the number of guests on a room.
     * @param pmsBookingRoomId
     * @param count
     * @return The new price for the room.
     */
     public JsonElement updateBooking(String gs_multiLevelName, Object booking, Object user, Object company)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.args.put("user",new Gson().toJson(user));
          gs_json_object_data.args.put("company",new Gson().toJson(company));
          gs_json_object_data.method = "updateBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsPaymentTerminal";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
