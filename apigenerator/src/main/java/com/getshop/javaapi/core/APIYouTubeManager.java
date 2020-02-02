package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIYouTubeManager {

      public Communicator transport;

      public APIYouTubeManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * The youtube manager handles the communication between the google youtube api and the frontend.
     */
     public JsonElement searchYoutube(Object searchword)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchword",new Gson().toJson(searchword));
          gs_json_object_data.method = "searchYoutube";
          gs_json_object_data.interfaceName = "core.youtubemanager.IYouTubeManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
