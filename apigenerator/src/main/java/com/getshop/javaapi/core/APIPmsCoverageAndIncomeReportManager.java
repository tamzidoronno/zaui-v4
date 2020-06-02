package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsCoverageAndIncomeReportManager {

      public Communicator transport;

      public APIPmsCoverageAndIncomeReportManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public void deleteSegment(String gs_multiLevelName, Object segmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("segmentId",new Gson().toJson(segmentId));
          gs_json_object_data.method = "deleteSegment";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void forceUpdateSegmentsOnBooking(String gs_multiLevelName, Object bookingId, Object segmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.args.put("segmentId",new Gson().toJson(segmentId));
          gs_json_object_data.method = "forceUpdateSegmentsOnBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getCustomerReport(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getCustomerReport";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getSegment(String gs_multiLevelName, Object segmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("segmentId",new Gson().toJson(segmentId));
          gs_json_object_data.method = "getSegment";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getSegmentForBooking(String gs_multiLevelName, Object bookingId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("bookingId",new Gson().toJson(bookingId));
          gs_json_object_data.method = "getSegmentForBooking";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getSegmentForRoom(String gs_multiLevelName, Object roomId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("roomId",new Gson().toJson(roomId));
          gs_json_object_data.method = "getSegmentForRoom";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getSegments(String gs_multiLevelName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.method = "getSegments";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getStatistics(String gs_multiLevelName, Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getStatistics";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void recalculateSegments(String gs_multiLevelName, Object segmentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("segmentId",new Gson().toJson(segmentId));
          gs_json_object_data.method = "recalculateSegments";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author boggi
     */
     public void saveSegments(String gs_multiLevelName, Object segment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.multiLevelName = gs_multiLevelName;
          gs_json_object_data.args.put("segment",new Gson().toJson(segment));
          gs_json_object_data.method = "saveSegments";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsCoverageAndIncomeReportManager";
          String result = transport.send(gs_json_object_data);
     }

}
