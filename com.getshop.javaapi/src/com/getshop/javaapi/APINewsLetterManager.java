package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APINewsLetterManager {

      public Transporter transport;

      public APINewsLetterManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Calling this function will start sending newsletter with a five minute interval for all recipients.
     * @param group
     */
     public void sendNewsLetter(com.thundashop.core.messagemanager.NewsLetterGroup group)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("group",new Gson().toJson(group));
          data.method = "sendNewsLetter";
          data.interfaceName = "core.messagemanager.INewsLetterManager";
          String result = transport.send(data);
     }

     /**
     * Send a preview to the selected contacts.
     * @param group
     */
     public void sendNewsLetterPreview(com.thundashop.core.messagemanager.NewsLetterGroup group)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("group",new Gson().toJson(group));
          data.method = "sendNewsLetterPreview";
          data.interfaceName = "core.messagemanager.INewsLetterManager";
          String result = transport.send(data);
     }

}
