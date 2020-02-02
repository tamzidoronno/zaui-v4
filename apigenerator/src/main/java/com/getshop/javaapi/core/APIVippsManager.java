package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIVippsManager {

      public Communicator transport;

      public APIVippsManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Vipps management.
     */
     public JsonElement cancelOrder(Object orderId, Object ip)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("ip",new Gson().toJson(ip));
          gs_json_object_data.method = "cancelOrder";
          gs_json_object_data.interfaceName = "core.vippsmanager.IVippsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Vipps management.
     */
     public void checkForOrdersToCapture()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForOrdersToCapture";
          gs_json_object_data.interfaceName = "core.vippsmanager.IVippsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Vipps management.
     */
     public JsonElement checkIfOrderHasBeenCompleted(Object incOrderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("incOrderId",new Gson().toJson(incOrderId));
          gs_json_object_data.method = "checkIfOrderHasBeenCompleted";
          gs_json_object_data.interfaceName = "core.vippsmanager.IVippsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Vipps management.
     */
     public JsonElement startMobileRequest(Object phoneNumber, Object orderId, Object ip)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("phoneNumber",new Gson().toJson(phoneNumber));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("ip",new Gson().toJson(ip));
          gs_json_object_data.method = "startMobileRequest";
          gs_json_object_data.interfaceName = "core.vippsmanager.IVippsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
