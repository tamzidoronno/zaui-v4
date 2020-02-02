package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIReportingManager {

      public Communicator transport;

      public APIReportingManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Fetch all activity data for a given session at a given period in time.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @param searchSessionId The id of the session to fetch data from.
     * @return
     * @throws ErrorException
     */
     public JsonElement getAllEventsFromSession(Object startDate, Object stopDate, Object searchSessionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("searchSessionId",new Gson().toJson(searchSessionId));
          gs_json_object_data.method = "getAllEventsFromSession";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all users which connected at a given time period.
     * These are users who has been logging on to your store.
     * @param startdate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @param filter A report filter if you want to filter out more information, use null to avoid the filter.
     * @return
     * @throws ErrorException
     */
     public JsonElement getConnectedUsers(Object startdate, Object stopDate, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startdate",new Gson().toJson(startdate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getConnectedUsers";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * List all orders created at a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public JsonElement getOrdersCreated(Object startDate, Object stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getOrdersCreated";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch the page id for all page
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public JsonElement getPageViews(Object startDate, Object stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getPageViews";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all viewed product for a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public JsonElement getProductViewed(Object startDate, Object stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getProductViewed";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a report for a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @param type 0, hourly, 1. daily, 2. weekly, 3. monthly
     * @return
     * @throws ErrorException
     */
     public JsonElement getReport(Object startDate, Object stopDate, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getReport";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch a list of all users trying / logging on at a given time interval.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public JsonElement getUserLoggedOn(Object startDate, Object stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getUserLoggedOn";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
