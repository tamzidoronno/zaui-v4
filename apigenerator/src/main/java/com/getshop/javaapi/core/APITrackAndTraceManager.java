package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APITrackAndTraceManager {

      public Communicator transport;

      public APITrackAndTraceManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void acceptTodaysInstruction(Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "acceptTodaysInstruction";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void acknowledgeDriverMessage(Object msgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.method = "acknowledgeDriverMessage";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addDeliveryTaskToDestionation(Object destionatId, Object task)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destionatId",new Gson().toJson(destionatId));
          gs_json_object_data.args.put("task",new Gson().toJson(task));
          gs_json_object_data.method = "addDeliveryTaskToDestionation";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void addDriverToRoute(Object userId, Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "addDriverToRoute";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement addPickupOrder(Object destnationId, Object order, Object inTask)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destnationId",new Gson().toJson(destnationId));
          gs_json_object_data.args.put("order",new Gson().toJson(order));
          gs_json_object_data.args.put("inTask",new Gson().toJson(inTask));
          gs_json_object_data.method = "addPickupOrder";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void changeCountedDriverCopies(Object taskId, Object orderReference, Object quantity)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("orderReference",new Gson().toJson(orderReference));
          gs_json_object_data.args.put("quantity",new Gson().toJson(quantity));
          gs_json_object_data.method = "changeCountedDriverCopies";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeQuantity(Object taskId, Object orderReference, Object parcels, Object containers)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("orderReference",new Gson().toJson(orderReference));
          gs_json_object_data.args.put("parcels",new Gson().toJson(parcels));
          gs_json_object_data.args.put("containers",new Gson().toJson(containers));
          gs_json_object_data.method = "changeQuantity";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void checkRemovalOfRoutes()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "checkRemovalOfRoutes";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void deleteReplyMessage(Object replyMessageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("replyMessageId",new Gson().toJson(replyMessageId));
          gs_json_object_data.method = "deleteReplyMessage";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteRoute(Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "deleteRoute";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllExportedDataForRoute(Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "getAllExportedDataForRoute";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllRoutes()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllRoutes";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getCompletedCollectionTasks(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getCompletedCollectionTasks";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getDestinationById(Object destinationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.method = "getDestinationById";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getDriverMessage(Object msgId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("msgId",new Gson().toJson(msgId));
          gs_json_object_data.method = "getDriverMessage";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getDriverMessages(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "getDriverMessages";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getExceptions()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getExceptions";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getExport(Object routeId, Object currentState)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("currentState",new Gson().toJson(currentState));
          gs_json_object_data.method = "getExport";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getExportedData(Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getExportedData";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLoadStatus(Object statusId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("statusId",new Gson().toJson(statusId));
          gs_json_object_data.method = "getLoadStatus";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLoadStatuses()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLoadStatuses";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getMyRoutes()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMyRoutes";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getPooledDestiontions()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPooledDestiontions";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getPooledDestiontionsByUsersDepotId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPooledDestiontionsByUsersDepotId";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getReplyMessages()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getReplyMessages";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getRouteIdsThatHasNotCompleted()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getRouteIdsThatHasNotCompleted";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getRoutesById(Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "getRoutesById";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement getRoutesCompletedPast24Hours()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getRoutesCompletedPast24Hours";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void loadData(Object base64, Object fileName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("base64",new Gson().toJson(base64));
          gs_json_object_data.args.put("fileName",new Gson().toJson(fileName));
          gs_json_object_data.method = "loadData";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void loadDataBase64(Object base64, Object fileName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("base64",new Gson().toJson(base64));
          gs_json_object_data.args.put("fileName",new Gson().toJson(fileName));
          gs_json_object_data.method = "loadDataBase64";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void markAsArrived(Object destinationId, Object startedTimeStamp, Object lon, Object lat)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("startedTimeStamp",new Gson().toJson(startedTimeStamp));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.method = "markAsArrived";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void markAsCompleted(Object routeId, Object lat, Object lon)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.method = "markAsCompleted";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void markAsCompletedWithTimeStamp(Object routeId, Object lat, Object lon, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "markAsCompletedWithTimeStamp";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement markAsCompletedWithTimeStampAndPassword(Object routeId, Object lat, Object lon, Object date, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "markAsCompletedWithTimeStampAndPassword";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void markAsDeliverd(Object taskId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.method = "markAsDeliverd";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void markDeparting(Object destinationId, Object latitude, Object longitude, Object timeStamp, Object signatureImage, Object typedSignature)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("latitude",new Gson().toJson(latitude));
          gs_json_object_data.args.put("longitude",new Gson().toJson(longitude));
          gs_json_object_data.args.put("timeStamp",new Gson().toJson(timeStamp));
          gs_json_object_data.args.put("signatureImage",new Gson().toJson(signatureImage));
          gs_json_object_data.args.put("typedSignature",new Gson().toJson(typedSignature));
          gs_json_object_data.method = "markDeparting";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void markInstructionAsRead(Object destinationId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "markInstructionAsRead";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void markOrderWithException(Object taskId, Object orderReferenceNumber, Object exceptionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("orderReferenceNumber",new Gson().toJson(orderReferenceNumber));
          gs_json_object_data.args.put("exceptionId",new Gson().toJson(exceptionId));
          gs_json_object_data.method = "markOrderWithException";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void markRouteAsStarted(Object routeId, Object startedTimeStamp, Object lon, Object lat)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("startedTimeStamp",new Gson().toJson(startedTimeStamp));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.method = "markRouteAsStarted";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement markRouteAsStartedWithCheck(Object routeId, Object startedTimeStamp, Object lon, Object lat)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("startedTimeStamp",new Gson().toJson(startedTimeStamp));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.method = "markRouteAsStartedWithCheck";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void markTaskWithExceptionDeliverd(Object taskId, Object exceptionId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("exceptionId",new Gson().toJson(exceptionId));
          gs_json_object_data.method = "markTaskWithExceptionDeliverd";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement moveDesitinationToPool(Object routeId, Object destinationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.method = "moveDesitinationToPool";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement moveDestinationFromPoolToRoute(Object destId, Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destId",new Gson().toJson(destId));
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "moveDestinationFromPoolToRoute";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void registerCollectionData(Object destinationId, Object collectionTasks)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("collectionTasks",new Gson().toJson(collectionTasks));
          gs_json_object_data.method = "registerCollectionData";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void removeDriverToRoute(Object userId, Object routeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.method = "removeDriverToRoute";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void replyGeneral(Object routeId, Object text, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "replyGeneral";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void replyMessage(Object messageId, Object text, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("messageId",new Gson().toJson(messageId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "replyMessage";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void replyMessageForDestionation(Object destinationId, Object text, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "replyMessageForDestionation";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement saveDestination(Object destination)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destination",new Gson().toJson(destination));
          gs_json_object_data.method = "saveDestination";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void saveException(Object exception)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("exception",new Gson().toJson(exception));
          gs_json_object_data.method = "saveException";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveRoute(Object route)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("route",new Gson().toJson(route));
          gs_json_object_data.method = "saveRoute";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement sendMessageToDriver(Object driverId, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("driverId",new Gson().toJson(driverId));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendMessageToDriver";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void setCagesOrPalletCount(Object taskId, Object quantity)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("quantity",new Gson().toJson(quantity));
          gs_json_object_data.method = "setCagesOrPalletCount";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setDesitionationException(Object destinationId, Object exceptionId, Object lon, Object lat)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("exceptionId",new Gson().toJson(exceptionId));
          gs_json_object_data.args.put("lon",new Gson().toJson(lon));
          gs_json_object_data.args.put("lat",new Gson().toJson(lat));
          gs_json_object_data.method = "setDesitionationException";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public JsonElement setInstructionOnDestination(Object routeId, Object destinationId, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("routeId",new Gson().toJson(routeId));
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "setInstructionOnDestination";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void setScannedBarcodes(Object taskId, Object orderReference, Object barcodes, Object barcodeEnteredManually)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("taskId",new Gson().toJson(taskId));
          gs_json_object_data.args.put("orderReference",new Gson().toJson(orderReference));
          gs_json_object_data.args.put("barcodes",new Gson().toJson(barcodes));
          gs_json_object_data.args.put("barcodeEnteredManually",new Gson().toJson(barcodeEnteredManually));
          gs_json_object_data.method = "setScannedBarcodes";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setSequence(Object exceptionId, Object sequence)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("exceptionId",new Gson().toJson(exceptionId));
          gs_json_object_data.args.put("sequence",new Gson().toJson(sequence));
          gs_json_object_data.method = "setSequence";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all the pooled destinations.
     *
     * @param routeId
     * @param destinationId
     * @return
     */
     public void setSortingOfRoutes(Object sortingName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("sortingName",new Gson().toJson(sortingName));
          gs_json_object_data.method = "setSortingOfRoutes";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void unsetSkippedReason(Object destinationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("destinationId",new Gson().toJson(destinationId));
          gs_json_object_data.method = "unsetSkippedReason";
          gs_json_object_data.interfaceName = "core.trackandtrace.ITrackAndTraceManager";
          String result = transport.send(gs_json_object_data);
     }

}
