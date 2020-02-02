package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPaymentTerminalManager {

      public Communicator transport;

      public APIPaymentTerminalManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * The payment terminal manager.<br>
     */
     public JsonElement getSetings(Object offset)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("offset",new Gson().toJson(offset));
          gs_json_object_data.method = "getSetings";
          gs_json_object_data.interfaceName = "core.paymentterminalmanager.IPaymentTerminalManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * The payment terminal manager.<br>
     */
     public void saveSettings(Object settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "saveSettings";
          gs_json_object_data.interfaceName = "core.paymentterminalmanager.IPaymentTerminalManager";
          String result = transport.send(gs_json_object_data);
     }

}
