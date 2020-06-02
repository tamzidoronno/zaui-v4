package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIBookingComRateManagerManager {

      public Communicator transport;

      public APIBookingComRateManagerManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Pms booking.com ratemanager.
     */
     public JsonElement getRateManagerConfig(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getRateManagerConfig";
          gs_json_object_data.interfaceName = "core.ratemanager.IBookingComRateManagerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Pms booking.com ratemanager.
     */
     public void pushAllBookings(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "pushAllBookings";
          gs_json_object_data.interfaceName = "core.ratemanager.IBookingComRateManagerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms booking.com ratemanager.
     */
     public void pushInventoryList(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "pushInventoryList";
          gs_json_object_data.interfaceName = "core.ratemanager.IBookingComRateManagerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms booking.com ratemanager.
     */
     public void saveRateManagerConfig(String gs_multiLevelName, Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "saveRateManagerConfig";
          gs_json_object_data.interfaceName = "core.ratemanager.IBookingComRateManagerManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Pms booking.com ratemanager.
     */
     public JsonElement updateRate(String gs_multiLevelName, Object start, Object end, Object roomId, Object rate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.args.put("rate",new Gson().toJson(rate));
          gs_json_object_data.method = "updateRate";
          gs_json_object_data.interfaceName = "core.ratemanager.IBookingComRateManagerManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
