package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIContentManager {

      public Communicator transport;

      public APIContentManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Create a new instance for the content manager.<br>
     * An id will automatically be generated and returned on creation.<br>
     *
     * @param content The content to build upon.
     * @return The id for the new content.
     * @throws ErrorException
     */
     public JsonElement createContent(Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "createContent";
          gs_json_object_data.interfaceName = "app.content.IContentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove the content for a given id.
     * @param id The id to remove the content for.
     * @throws ErrorException
     */
     public void deleteContent(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteContent";
          gs_json_object_data.interfaceName = "app.content.IContentManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Fetch all content saved until now.
     * @return A list of all content accosiated with its given id.
     * @throws ErrorException
     */
     public JsonElement getAllContent()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllContent";
          gs_json_object_data.interfaceName = "app.content.IContentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch the content for a given id.
     * @param id The id which is identifying the content.
     * @return
     * @throws ErrorException
     */
     public JsonElement getContent(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getContent";
          gs_json_object_data.interfaceName = "app.content.IContentManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Update / replace the content for a given id.
     * @param id The id to update the content for.
     * @param content The content to update. This could be html / text.
     * @throws ErrorException
     */
     public void saveContent(Object id, Object content)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("content",new Gson().toJson(content));
          gs_json_object_data.method = "saveContent";
          gs_json_object_data.interfaceName = "app.content.IContentManager";
          String result = transport.send(gs_json_object_data);
     }

}
