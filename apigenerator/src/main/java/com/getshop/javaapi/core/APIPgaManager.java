package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPgaManager {

      public Communicator transport;

      public APIPgaManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBooking(String gs_multiLevelName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "getBooking";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getBookingItemTypeName(String gs_multiLevelName, Object bookingItemTypeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingItemTypeId",new Gson().toJson(bookingItemTypeId));
          gs_json_object_data.method = "getBookingItemTypeName";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getConference(String gs_multiLevelName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "getConference";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getEvents(String gs_multiLevelName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "getEvents";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getRooms(String gs_multiLevelName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "getRooms";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSummaries(String gs_multiLevelName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "getSummaries";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getUser(String gs_multiLevelName, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "getUser";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void updateGuests(String gs_multiLevelName, Object token, Object booking)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.args.put("booking",new Gson().toJson(booking));
          gs_json_object_data.method = "updateGuests";
          gs_json_object_data.interfaceName = "core.pga.IPgaManager";
          String result = transport.send(gs_json_object_data);
     }

}
