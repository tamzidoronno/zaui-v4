package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIVerifoneManager {

      public Communicator transport;

      public APIVerifoneManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Handle payments trough verifone.
     */
     public void cancelPaymentProcess(Object terminalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("terminalId",new Gson().toJson(terminalId));
          gs_json_object_data.method = "cancelPaymentProcess";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Handle payments trough verifone.
     */
     public void chargeOrder(Object orderId, Object terminalId, Object overrideDevMode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("terminalId",new Gson().toJson(terminalId));
          gs_json_object_data.args.put("overrideDevMode",new Gson().toJson(overrideDevMode));
          gs_json_object_data.method = "chargeOrder";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Handle payments trough verifone.
     */
     public void clearMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "clearMessages";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Handle payments trough verifone.
     */
     public void doXreport(Object terminalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("terminalId",new Gson().toJson(terminalId));
          gs_json_object_data.method = "doXreport";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Handle payments trough verifone.
     */
     public void doZreport(Object terminalId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("terminalId",new Gson().toJson(terminalId));
          gs_json_object_data.method = "doZreport";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Handle payments trough verifone.
     */
     public JsonElement getCurrentPaymentOrderId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCurrentPaymentOrderId";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Handle payments trough verifone.
     */
     public JsonElement getTerminalMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getTerminalMessages";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Handle payments trough verifone.
     */
     public JsonElement isPaymentInProgress()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isPaymentInProgress";
          gs_json_object_data.interfaceName = "core.verifonemanager.IVerifoneManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
