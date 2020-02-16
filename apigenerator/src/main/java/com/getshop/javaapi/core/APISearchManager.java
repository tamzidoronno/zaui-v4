package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISearchManager {

      public Communicator transport;

      public APISearchManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Search for a word.
     * Returns a map where key is the content and value is the pageId.
     * @param searchWord
     * @return
     */
     public JsonElement search(Object searchWord)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchWord",new Gson().toJson(searchWord));
          gs_json_object_data.method = "search";
          gs_json_object_data.interfaceName = "core.searchmanager.ISearchManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
