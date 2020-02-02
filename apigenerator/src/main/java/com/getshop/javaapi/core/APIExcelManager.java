package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIExcelManager {

      public Communicator transport;

      public APIExcelManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Excel management.
     */
     public JsonElement getBase64Excel(Object array)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("array",new Gson().toJson(array));
          gs_json_object_data.method = "getBase64Excel";
          gs_json_object_data.interfaceName = "core.excelmanager.IExcelManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Excel management.
     */
     public JsonElement getPreparedExcelSheet()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPreparedExcelSheet";
          gs_json_object_data.interfaceName = "core.excelmanager.IExcelManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Excel management.
     */
     public void prepareExcelSheet(Object name, Object array)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("array",new Gson().toJson(array));
          gs_json_object_data.method = "prepareExcelSheet";
          gs_json_object_data.interfaceName = "core.excelmanager.IExcelManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Excel management.
     */
     public void startExcelSheet()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "startExcelSheet";
          gs_json_object_data.interfaceName = "core.excelmanager.IExcelManager";
          String result = transport.send(gs_json_object_data);
     }

}
