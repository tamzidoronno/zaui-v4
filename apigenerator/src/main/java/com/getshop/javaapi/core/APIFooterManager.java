package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIFooterManager {

      public Communicator transport;

      public APIFooterManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Get the current configuration.
     * @return
     * @throws ErrorException
     */
     public JsonElement getConfiguration()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getConfiguration";
          gs_json_object_data.interfaceName = "app.footer.IFooterManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Change the layout for the columns.<br>
     * Defaults to 1 if nothing else is set.<br>
     * @param numberOfColumns The number of columns you want to display.
     * @throws ErrorException
     */
     public JsonElement setLayout(Object numberOfColumns)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("numberOfColumns",new Gson().toJson(numberOfColumns));
          gs_json_object_data.method = "setLayout";
          gs_json_object_data.interfaceName = "app.footer.IFooterManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Change the type of a given column.
     * @param column The column it regards
     * @param type The type,0 for text, 1 for list.
     * @return
     * @throws ErrorException
     */
     public JsonElement setType(Object column, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("column",new Gson().toJson(column));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "setType";
          gs_json_object_data.interfaceName = "app.footer.IFooterManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
