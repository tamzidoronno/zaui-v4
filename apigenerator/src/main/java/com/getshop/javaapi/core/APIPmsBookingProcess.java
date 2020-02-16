package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsBookingProcess {

      public Communicator transport;

      public APIPmsBookingProcess(Communicator transport){
           this.transport = transport;
      }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement addAddons(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "addAddons";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement addBookingItem(String gs_multiLevelName, Object bookingId, Object type, Object start, Object end, Object guestInfoFromRoom, Object bookingItemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("guestInfoFromRoom",new Gson().toJson(guestInfoFromRoom));
          gs_json_object_data.args.put("bookingItemId",new Gson().toJson(bookingItemId));
          gs_json_object_data.method = "addBookingItem";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement addBookingItemType(String gs_multiLevelName, Object bookingId, Object type, Object start, Object end, Object guestInfoFromRoom)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("guestInfoFromRoom",new Gson().toJson(guestInfoFromRoom));
          gs_json_object_data.method = "addBookingItemType";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public void addTestMessagesToQueue(String gs_multiLevelName, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "addTestMessagesToQueue";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public void cancelPaymentProcess(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "cancelPaymentProcess";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement changeDateOnRoom(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "changeDateOnRoom";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement changeGuestCountForRoom(String gs_multiLevelName, Object roomId, Object guestCount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("guestCount",new Gson().toJson(guestCount));
          gs_json_object_data.method = "changeGuestCountForRoom";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement changeNumberOnType(String gs_multiLevelName, Object change)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("change",new Gson().toJson(change));
          gs_json_object_data.method = "changeNumberOnType";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public void chargeOrderWithVerifoneTerminal(String gs_multiLevelName, Object orderId, Object terminalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("terminalId",new Gson().toJson(terminalId));
          gs_json_object_data.method = "chargeOrderWithVerifoneTerminal";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement completeBooking(String gs_multiLevelName, Object input)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("input",new Gson().toJson(input));
          gs_json_object_data.method = "completeBooking";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement completeBookingForTerminal(String gs_multiLevelName, Object input)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("input",new Gson().toJson(input));
          gs_json_object_data.method = "completeBookingForTerminal";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement getAddonsSummary(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "getAddonsSummary";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement getAllCategories(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllCategories";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement getBooking(String gs_multiLevelName, Object pmsBookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("pmsBookingId",new Gson().toJson(pmsBookingId));
          gs_json_object_data.method = "getBooking";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement getConfiguration(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getConfiguration";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement getPricesForRoom(String gs_multiLevelName, Object start, Object end, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "getPricesForRoom";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement getTerminalMessages(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getTerminalMessages";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement hasPrintCodeSupportOnTerminal(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "hasPrintCodeSupportOnTerminal";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement logOn(String gs_multiLevelName, Object logindata)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("logindata",new Gson().toJson(logindata));
          gs_json_object_data.method = "logOn";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement logOut(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "logOut";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement printCodeOnTerminal(String gs_multiLevelName, Object roomId, Object phoneNumber, Object terminalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("phoneNumber",new Gson().toJson(phoneNumber));
          gs_json_object_data.args.put("terminalId",new Gson().toJson(terminalId));
          gs_json_object_data.method = "printCodeOnTerminal";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public void printReciept(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "printReciept";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public void quickChangeGuestCountForRoom(String gs_multiLevelName, Object roomId, Object guestCount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("guestCount",new Gson().toJson(guestCount));
          gs_json_object_data.method = "quickChangeGuestCountForRoom";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement removeAddons(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "removeAddons";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement removeGroupedRooms(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "removeGroupedRooms";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement removeRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "removeRoom";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement saveGuestInformation(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "saveGuestInformation";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public void setBookingItemToCurrentBooking(String gs_multiLevelName, Object roomId, Object itemId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("itemId",new Gson().toJson(itemId));
          gs_json_object_data.method = "setBookingItemToCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement setGuestInformation(String gs_multiLevelName, Object bookerInfo)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("bookerInfo",new Gson().toJson(bookerInfo));
          gs_json_object_data.method = "setGuestInformation";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public void simpleCompleteCurrentBooking(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "simpleCompleteCurrentBooking";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement startBooking(String gs_multiLevelName, Object arg)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("arg",new Gson().toJson(arg));
          gs_json_object_data.method = "startBooking";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Booking process for property management system.<br>
     */
     public JsonElement startPaymentProcess(String gs_multiLevelName, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "startPaymentProcess";
          gs_json_object_data.interfaceName = "core.pmsbookingprocess.IPmsBookingProcess";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
