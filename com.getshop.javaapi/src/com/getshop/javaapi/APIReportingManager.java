package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIReportingManager {

      public Transporter transport;

      public APIReportingManager(Transporter transport){
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
     public java.util.List getAllEventsFromSession(java.lang.String startDate, java.lang.String stopDate, java.lang.String searchSessionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("searchSessionId",new Gson().toJson(searchSessionId));
          gs_json_object_data.method = "getAllEventsFromSession";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.EventLog>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
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
     public java.util.List getConnectedUsers(java.lang.String startdate, java.lang.String stopDate, com.thundashop.core.reportingmanager.data.ReportFilter filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startdate",new Gson().toJson(startdate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getConnectedUsers";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.UserConnected>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * List all orders created at a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public java.util.List getOrdersCreated(java.lang.String startDate, java.lang.String stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getOrdersCreated";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.OrderCreated>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the page id for all page
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public java.util.List getPageViews(java.lang.String startDate, java.lang.String stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getPageViews";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.PageView>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all viewed product for a given time period.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public java.util.List getProductViewed(java.lang.String startDate, java.lang.String stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getProductViewed";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.ProductViewed>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
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
     public java.util.List getReport(java.lang.String startDate, java.lang.String stopDate, int type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getReport";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.Report>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list of all users trying / logging on at a given time interval.
     * @param startDate "yyyy-mm-dd"
     * @param stopDate "yyyy-mm-dd"
     * @return
     * @throws ErrorException
     */
     public java.util.List getUserLoggedOn(java.lang.String startDate, java.lang.String stopDate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startDate",new Gson().toJson(startDate));
          gs_json_object_data.args.put("stopDate",new Gson().toJson(stopDate));
          gs_json_object_data.method = "getUserLoggedOn";
          gs_json_object_data.interfaceName = "core.reportingmanager.IReportingManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.reportingmanager.data.LoggedOnUser>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
