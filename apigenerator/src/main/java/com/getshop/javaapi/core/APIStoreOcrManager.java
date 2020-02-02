package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIStoreOcrManager {

      public Communicator transport;

      public APIStoreOcrManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public void checkForPayments()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkForPayments";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void disconnectAccountId(Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "disconnectAccountId";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getAccountingId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAccountingId";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getAllTransactions()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllTransactions";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getOcrLinesForDay(Object year, Object month, Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("year",new Gson().toJson(year));
          gs_json_object_data.args.put("month",new Gson().toJson(month));
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getOcrLinesForDay";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement isActivated()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "isActivated";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void retryMatchOrders()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "retryMatchOrders";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void setAccountId(Object id, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "setAccountId";
          gs_json_object_data.interfaceName = "core.ocr.IStoreOcrManager";
          String result = transport.send(gs_json_object_data);
     }

}
