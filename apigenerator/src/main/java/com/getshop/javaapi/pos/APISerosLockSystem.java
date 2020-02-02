package com.getshop.javaapi.pos;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISerosLockSystem {

      public Communicator transport;

      public APISerosLockSystem(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void changeAutoCloseTimeForLock(Object smarthubDeviceId, Object time)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.args.put("time",new Gson().toJson(time));
          gs_json_object_data.method = "changeAutoCloseTimeForLock";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeState(Object smarthubDeviceId, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "changeState";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void deleteAllCodesOnLock(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "deleteAllCodesOnLock";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLockById(Object lockId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.method = "getLockById";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLockBySmartHubDeviceId(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "getLockBySmartHubDeviceId";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLocks()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLocks";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLogs(Object smartHubDeviceId, Object start, Object end)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smartHubDeviceId",new Gson().toJson(smartHubDeviceId));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.method = "getLogs";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void pingDeviceBySmarthubDeviceId(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "pingDeviceBySmarthubDeviceId";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void replaceSmartHubDeviceOnLock(Object lockId, Object newSmartHubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lockId",new Gson().toJson(lockId));
          gs_json_object_data.args.put("newSmartHubDeviceId",new Gson().toJson(newSmartHubDeviceId));
          gs_json_object_data.method = "replaceSmartHubDeviceOnLock";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void resendAllCodes(Object smarthubDeviceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smarthubDeviceId",new Gson().toJson(smarthubDeviceId));
          gs_json_object_data.method = "resendAllCodes";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveLock(Object lock)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lock",new Gson().toJson(lock));
          gs_json_object_data.method = "saveLock";
          gs_json_object_data.interfaceName = "core.locksystem.ISerosLockSystem";
          String result = transport.send(gs_json_object_data);
     }

}
