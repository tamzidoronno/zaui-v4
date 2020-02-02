package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APINewsLetterManager {

      public Communicator transport;

      public APINewsLetterManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Calling this function will start sending newsletter with a five minute interval for all recipients.
     * @param group
     */
     public void sendNewsLetter(Object group)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("group",new Gson().toJson(group));
          gs_json_object_data.method = "sendNewsLetter";
          gs_json_object_data.interfaceName = "core.messagemanager.INewsLetterManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Send a preview to the selected contacts.
     * @param group
     */
     public void sendNewsLetterPreview(Object group)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("group",new Gson().toJson(group));
          gs_json_object_data.method = "sendNewsLetterPreview";
          gs_json_object_data.interfaceName = "core.messagemanager.INewsLetterManager";
          String result = transport.send(gs_json_object_data);
     }

}
