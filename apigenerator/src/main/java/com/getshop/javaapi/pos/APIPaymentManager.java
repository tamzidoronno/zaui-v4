package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPaymentManager {

      public Communicator transport;

      public APIPaymentManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public JsonElement getConfig(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getConfig";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getGeneralPaymentConfig()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getGeneralPaymentConfig";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStorePaymentConfiguration(Object paymentAppId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentAppId",new Gson().toJson(paymentAppId));
          gs_json_object_data.method = "getStorePaymentConfiguration";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getStorePaymentConfigurations(Object paymentAppId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("paymentAppId",new Gson().toJson(paymentAppId));
          gs_json_object_data.method = "getStorePaymentConfigurations";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void resetAllAccountingConfigurationForUsersAndOrders(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "resetAllAccountingConfigurationForUsersAndOrders";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveGeneralPaymentConfig(Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "saveGeneralPaymentConfig";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveStorePaymentConfiguration(Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "saveStorePaymentConfiguration";
          gs_json_object_data.interfaceName = "core.paymentmanager.IPaymentManager";
          String result = transport.send(gs_json_object_data);
     }

}
