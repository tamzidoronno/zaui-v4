package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIWubookManager {

      public Communicator transport;

      public APIWubookManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Wubook management system.<br>
     */
     public void activateWubookCallback(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "activateWubookCallback";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public void addBooking(String gs_multiLevelName, Object rcode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rcode",new Gson().toJson(rcode));
          gs_json_object_data.method = "addBooking";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement addNewBookingsPastDays(String gs_multiLevelName, Object daysback)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("daysback",new Gson().toJson(daysback));
          gs_json_object_data.method = "addNewBookingsPastDays";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public void addRestriction(String gs_multiLevelName, Object restriction)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("restriction",new Gson().toJson(restriction));
          gs_json_object_data.method = "addRestriction";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public void checkForNoShowsAndMark(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "checkForNoShowsAndMark";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public void deleteAllRooms(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "deleteAllRooms";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement deleteBooking(String gs_multiLevelName, Object rcode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rcode",new Gson().toJson(rcode));
          gs_json_object_data.method = "deleteBooking";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public void deleteRestriction(String gs_multiLevelName, Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteRestriction";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public void doUpdateMinStay(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "doUpdateMinStay";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public void doubleCheckDeletedBookings(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "doubleCheckDeletedBookings";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement fetchAllBookings(String gs_multiLevelName, Object daysback)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("daysback",new Gson().toJson(daysback));
          gs_json_object_data.method = "fetchAllBookings";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement fetchBooking(String gs_multiLevelName, Object rcode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rcode",new Gson().toJson(rcode));
          gs_json_object_data.method = "fetchBooking";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement fetchBookingCodes(String gs_multiLevelName, Object daysback)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("daysback",new Gson().toJson(daysback));
          gs_json_object_data.method = "fetchBookingCodes";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public void fetchBookingFromCallback(String gs_multiLevelName, Object rcode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rcode",new Gson().toJson(rcode));
          gs_json_object_data.method = "fetchBookingFromCallback";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement fetchBookings(String gs_multiLevelName, Object daysBack, Object registrations)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("daysBack",new Gson().toJson(daysBack));
          gs_json_object_data.args.put("registrations",new Gson().toJson(registrations));
          gs_json_object_data.method = "fetchBookings";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public void fetchNewBookings(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "fetchNewBookings";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement getAllRestriction(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getAllRestriction";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement getCallbackUrl(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getCallbackUrl";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement getLogEntries(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getLogEntries";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement getOtas(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getOtas";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement getRoomRates(String gs_multiLevelName, Object channelId, Object channelType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("channelId",new Gson().toJson(channelId));
          gs_json_object_data.args.put("channelType",new Gson().toJson(channelType));
          gs_json_object_data.method = "getRoomRates";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement getWubookRoomData(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "getWubookRoomData";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement insertAllRooms(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "insertAllRooms";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement markCCInvalid(String gs_multiLevelName, Object rcode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rcode",new Gson().toJson(rcode));
          gs_json_object_data.method = "markCCInvalid";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement markNoShow(String gs_multiLevelName, Object rcode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("rcode",new Gson().toJson(rcode));
          gs_json_object_data.method = "markNoShow";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement newOta(String gs_multiLevelName, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "newOta";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public void saveWubookRoomData(String gs_multiLevelName, Object res)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("res",new Gson().toJson(res));
          gs_json_object_data.method = "saveWubookRoomData";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public void setRoomRates(String gs_multiLevelName, Object channelId, Object rates, Object channelType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("channelId",new Gson().toJson(channelId));
          gs_json_object_data.args.put("rates",new Gson().toJson(rates));
          gs_json_object_data.args.put("channelType",new Gson().toJson(channelType));
          gs_json_object_data.method = "setRoomRates";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement testConnection(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "testConnection";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement updateAvailabilityFromButton(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "updateAvailabilityFromButton";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement updateMinStay(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "updateMinStay";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement updatePrices(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "updatePrices";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement updatePricesBetweenDates(String gs_multiLevelName, Object now, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.args.put("now",new Gson().toJson(now));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "updatePricesBetweenDates";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Wubook management system.<br>
     */
     public JsonElement updateShortAvailability(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("gs_multiLevelName",new Gson().toJson(gs_multiLevelName));
          gs_json_object_data.method = "updateShortAvailability";
          gs_json_object_data.interfaceName = "core.wubook.IWubookManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
