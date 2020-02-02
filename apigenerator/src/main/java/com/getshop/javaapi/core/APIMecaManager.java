package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIMecaManager {

      public Communicator transport;

      public APIMecaManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author emil
     */
     public JsonElement answerControlRequest(Object carId, Object answer)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("answer",new Gson().toJson(answer));
          gs_json_object_data.method = "answerControlRequest";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement answerServiceRequest(Object carId, Object answer)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("answer",new Gson().toJson(answer));
          gs_json_object_data.method = "answerServiceRequest";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public void callMe(Object cellPhone)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cellPhone",new Gson().toJson(cellPhone));
          gs_json_object_data.method = "callMe";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public JsonElement createFleet(Object fleet)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fleet",new Gson().toJson(fleet));
          gs_json_object_data.method = "createFleet";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public void deleteCar(Object carId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.method = "deleteCar";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void deleteFleet(Object fleetId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fleetId",new Gson().toJson(fleetId));
          gs_json_object_data.method = "deleteFleet";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public JsonElement getBase64ExcelReport(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getBase64ExcelReport";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getCar(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getCar";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getCarByPageId(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getCarByPageId";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getCarsByCellphone(Object cellPhone)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cellPhone",new Gson().toJson(cellPhone));
          gs_json_object_data.method = "getCarsByCellphone";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getCarsForMecaFleet(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getCarsForMecaFleet";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getCarsPKKList()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCarsPKKList";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getCarsServiceList(Object needService)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("needService",new Gson().toJson(needService));
          gs_json_object_data.method = "getCarsServiceList";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getFleetByCar(Object car)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("car",new Gson().toJson(car));
          gs_json_object_data.method = "getFleetByCar";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getFleetPageId(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getFleetPageId";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public JsonElement getFleets()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getFleets";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public void markControlAsCompleted(Object carId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.method = "markControlAsCompleted";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void noShowPkk(Object carId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.method = "noShowPkk";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void noShowService(Object carId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.method = "noShowService";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void notifyByPush(Object phoneNumber, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("phoneNumber",new Gson().toJson(phoneNumber));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "notifyByPush";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void registerDeviceToCar(Object deviceId, Object cellPhone)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("deviceId",new Gson().toJson(deviceId));
          gs_json_object_data.args.put("cellPhone",new Gson().toJson(cellPhone));
          gs_json_object_data.method = "registerDeviceToCar";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void requestNextControl(Object carId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "requestNextControl";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void requestNextService(Object carId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "requestNextService";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void resetServiceInterval(Object carId, Object date, Object kilometers)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.args.put("kilometers",new Gson().toJson(kilometers));
          gs_json_object_data.method = "resetServiceInterval";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void runNotificationCheck()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "runNotificationCheck";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void saveFleet(Object fleet)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("fleet",new Gson().toJson(fleet));
          gs_json_object_data.method = "saveFleet";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public JsonElement saveFleetCar(Object pageId, Object car)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("car",new Gson().toJson(car));
          gs_json_object_data.method = "saveFleetCar";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author emil
     */
     public void saveMecaFleetSettings(Object settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "saveMecaFleetSettings";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void sendEmail(Object cellPhone, Object message)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cellPhone",new Gson().toJson(cellPhone));
          gs_json_object_data.args.put("message",new Gson().toJson(message));
          gs_json_object_data.method = "sendEmail";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void sendInvite(Object mecaCarId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("mecaCarId",new Gson().toJson(mecaCarId));
          gs_json_object_data.method = "sendInvite";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void sendKilometerRequest(Object carId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.method = "sendKilometerRequest";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void sendKilometers(Object cellPhone, Object kilometers)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("cellPhone",new Gson().toJson(cellPhone));
          gs_json_object_data.args.put("kilometers",new Gson().toJson(kilometers));
          gs_json_object_data.method = "sendKilometers";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void sendNotificationToStoreOwner()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "sendNotificationToStoreOwner";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void setCommentOnCar(Object carId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "setCommentOnCar";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void setManuallyControlDate(Object carId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "setManuallyControlDate";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public void setManuallyServiceDate(Object carId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "setManuallyServiceDate";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author emil
     */
     public JsonElement suggestDate(Object carId, Object date)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carId",new Gson().toJson(carId));
          gs_json_object_data.args.put("date",new Gson().toJson(date));
          gs_json_object_data.method = "suggestDate";
          gs_json_object_data.interfaceName = "core.mecamanager.IMecaManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
