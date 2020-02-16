package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APINewsManager {

      public Communicator transport;

      public APINewsManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Add a new news entry.
     * @param news The news object to add.
     * @return The id for this news entry.
     * @throws ErrorExceNption
     */
     public JsonElement addNews(Object newsEntry, Object newsListId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newsEntry",new Gson().toJson(newsEntry));
          gs_json_object_data.args.put("newsListId",new Gson().toJson(newsListId));
          gs_json_object_data.method = "addNews";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Add a subscriber.
     * Whenever a new news is updated to this, the subscribe will get an email.
     * @param email The email address for the subscriber.
     * @return
     * @throws ErrorException
     */
     public JsonElement addSubscriber(Object email)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.method = "addSubscriber";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Publishing news.
     *
     * @param id
     * @throws ErrorException
     */
     public void applyUserFilter(Object newsListId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newsListId",new Gson().toJson(newsListId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "applyUserFilter";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Publishing news.
     *
     * @param id
     * @throws ErrorException
     */
     public void changeDateOfNews(Object id, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "changeDateOfNews";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Delete a given news id.
     * @param id The id for the news to delete.
     * @throws ErrorException
     */
     public void deleteNews(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteNews";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Fetch all news added.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllNews(Object newsListId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newsListId",new Gson().toJson(newsListId));
          gs_json_object_data.method = "getAllNews";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get all subscribers.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllSubscribers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllSubscribers";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get all subscribers.
     * @return
     * @throws ErrorException
     */
     public JsonElement getNewsForPage(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getNewsForPage";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Publishing news.
     *
     * @param id
     * @throws ErrorException
     */
     public JsonElement getNewsUsers(Object newsListId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newsListId",new Gson().toJson(newsListId));
          gs_json_object_data.method = "getNewsUsers";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Publishing news.
     *
     * @param id
     * @throws ErrorException
     */
     public JsonElement isFiltered(Object newsListId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("newsListId",new Gson().toJson(newsListId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "isFiltered";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Publishing news.
     *
     * @param id
     * @throws ErrorException
     */
     public void publishNews(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "publishNews";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Remove an existing subscriber.
     * @param subscriberId The subscribers id found in the MailSubscriber object.
     * @return
     * @throws ErrorException
     */
     public void removeSubscriber(Object subscriberId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("subscriberId",new Gson().toJson(subscriberId));
          gs_json_object_data.method = "removeSubscriber";
          gs_json_object_data.interfaceName = "app.news.INewsManager";
          String result = transport.send(gs_json_object_data);
     }

}
