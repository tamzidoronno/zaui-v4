package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIResturantManager {

      public Communicator transport;

      public APIResturantManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void addCartItemToCurrentTableSession(Object tableId, Object cartItem)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.args.put("cartItem",new Gson().toJson(cartItem));
          gs_json_object_data.method = "addCartItemToCurrentTableSession";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addCartItems(Object cartItems, Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "addCartItems";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void addCartItemsToReservation(Object cartItems, Object reservationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.args.put("reservationId",new Gson().toJson(reservationId));
          gs_json_object_data.method = "addCartItemsToReservation";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement changeToDifferentSession(Object sessionId, Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("sessionId",new Gson().toJson(sessionId));
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "changeToDifferentSession";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement checkPinCode(Object pincode, Object bookingId, Object pmsRoomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pincode",new Gson().toJson(pincode));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("pmsRoomId",new Gson().toJson(pmsRoomId));
          gs_json_object_data.method = "checkPinCode";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement completePayment(Object paymentMethodId, Object cartItemIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.args.put("cartItemIds",new Gson().toJson(cartItemIds));
          gs_json_object_data.method = "completePayment";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void createCartForReservation(Object reservationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reservationId",new Gson().toJson(reservationId));
          gs_json_object_data.method = "createCartForReservation";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void createCartForTable(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "createCartForTable";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createRoom(Object roomName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roomName",new Gson().toJson(roomName));
          gs_json_object_data.method = "createRoom";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createTable(Object roomId, Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "createTable";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createTableSession(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "createTableSession";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteTable(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "deleteTable";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllSessions()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllSessions";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement getAllSessionsForTable(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "getAllSessionsForTable";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement getCurrentTableData(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "getCurrentTableData";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getRoomById(Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "getRoomById";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getRooms()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getRooms";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getTableById(Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "getTableById";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement getTableDataForReservation(Object reservationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reservationId",new Gson().toJson(reservationId));
          gs_json_object_data.method = "getTableDataForReservation";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement getTableDayData(Object date, Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "getTableDayData";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement getTableReservation(Object reservationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("reservationId",new Gson().toJson(reservationId));
          gs_json_object_data.method = "getTableReservation";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement getTerminalMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTerminalMessages";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public JsonElement isOrderPriceCorrect(Object paymentMethodId, Object cartItems, Object price)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.args.put("cartItems",new Gson().toJson(cartItems));
          gs_json_object_data.args.put("price",new Gson().toJson(price));
          gs_json_object_data.method = "isOrderPriceCorrect";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void payOnRoom(Object room, Object cartItemsIds)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("room",new Gson().toJson(room));
          gs_json_object_data.args.put("cartItemsIds",new Gson().toJson(cartItemsIds));
          gs_json_object_data.method = "payOnRoom";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void prePrint(Object paymentMethodId, Object cartItemIds, Object printerId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentMethodId",new Gson().toJson(paymentMethodId));
          gs_json_object_data.args.put("cartItemIds",new Gson().toJson(cartItemIds));
          gs_json_object_data.args.put("printerId",new Gson().toJson(printerId));
          gs_json_object_data.method = "prePrint";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns all the sessions which the current table can change to,
     * this means that the session that is currently active is hidden.
     *
     * @param tableId
     * @return
     */
     public void startNewReservation(Object start, Object end, Object name, Object tableId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("tableId",new Gson().toJson(tableId));
          gs_json_object_data.method = "startNewReservation";
          gs_json_object_data.interfaceName = "core.resturantmanager.IResturantManager";
          String result = transport.send(gs_json_object_data);
     }

}
