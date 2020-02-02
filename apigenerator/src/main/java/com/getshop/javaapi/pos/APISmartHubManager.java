package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISmartHubManager {

      public Communicator transport;

      public APISmartHubManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void changeDeviceId(Object smarthubdeviceid, Object newDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubdeviceid",new Gson().toJson(smarthubdeviceid));
          gs_json_object_data.args.put("newDeviceId",new Gson().toJson(newDeviceId));
          gs_json_object_data.method = "changeDeviceId";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deactivatePriortiyUpdate(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "deactivatePriortiyUpdate";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteDevice(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "deleteDevice";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteSmartHub(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteSmartHub";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllDevicesSmartHub(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getAllDevicesSmartHub";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSmartDevice(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "getSmartDevice";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSmartHub(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getSmartHub";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSmartHubAssociations()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSmartHubAssociations";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSmartHubs()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSmartHubs";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement isSmartOnline(Object smartHubId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smartHubId",new Gson().toJson(smartHubId));
          gs_json_object_data.method = "isSmartOnline";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void pingSmartHub(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "pingSmartHub";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void runCheck(Object smarthubId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubId",new Gson().toJson(smarthubId));
          gs_json_object_data.method = "runCheck";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveSmartHub(Object smartHub)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smartHub",new Gson().toJson(smartHub));
          gs_json_object_data.method = "saveSmartHub";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void sendInitSignalChip(Object smartHubId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smartHubId",new Gson().toJson(smartHubId));
          gs_json_object_data.method = "sendInitSignalChip";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setGidAndType(Object smartHubId, Object deviceId, Object gid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smartHubId",new Gson().toJson(smartHubId));
          gs_json_object_data.args.put("deviceId",new Gson().toJson(deviceId));
          gs_json_object_data.args.put("gid",new Gson().toJson(gid));
          gs_json_object_data.method = "setGidAndType";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setPriorityUpdate(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "setPriorityUpdate";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void setStopCommunication(Object smarthubDeviceId, Object communicate)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.args.put("communicate",new Gson().toJson(communicate));
          gs_json_object_data.method = "setStopCommunication";
          gs_json_object_data.interfaceName = "core.smarthub.ISmartHubManager";
          String result = transport.send(gs_json_object_data);
     }

}
