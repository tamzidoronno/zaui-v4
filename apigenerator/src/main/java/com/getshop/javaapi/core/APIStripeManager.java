package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIStripeManager {

      public Communicator transport;

      public APIStripeManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public JsonElement chargeOrder(Object orderId, Object cardId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("cardId",new Gson().toJson(cardId));
          gs_json_object_data.method = "chargeOrder";
          gs_json_object_data.interfaceName = "core.stripe.IStripeManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement chargeSofort(Object orderId, Object source)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("source",new Gson().toJson(source));
          gs_json_object_data.method = "chargeSofort";
          gs_json_object_data.interfaceName = "core.stripe.IStripeManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement createAndChargeCustomer(Object orderId, Object token)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("token",new Gson().toJson(token));
          gs_json_object_data.method = "createAndChargeCustomer";
          gs_json_object_data.interfaceName = "core.stripe.IStripeManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement createSessionForPayment(Object orderId, Object address)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("address",new Gson().toJson(address));
          gs_json_object_data.method = "createSessionForPayment";
          gs_json_object_data.interfaceName = "core.stripe.IStripeManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void handleWebhookCallback(Object callbackResult)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("callbackResult",new Gson().toJson(callbackResult));
          gs_json_object_data.method = "handleWebhookCallback";
          gs_json_object_data.interfaceName = "core.stripe.IStripeManager";
          String result = transport.send(gs_json_object_data);
     }

}
