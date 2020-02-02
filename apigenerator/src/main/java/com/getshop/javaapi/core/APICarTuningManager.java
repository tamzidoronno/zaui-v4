package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APICarTuningManager {

      public Communicator transport;

      public APICarTuningManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public JsonElement getCarTuningData(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getCarTuningData";
          gs_json_object_data.interfaceName = "core.cartuning.ICarTuningManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void saveCarTuningData(Object carList)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("carList",new Gson().toJson(carList));
          gs_json_object_data.method = "saveCarTuningData";
          gs_json_object_data.interfaceName = "core.cartuning.ICarTuningManager";
          String result = transport.send(gs_json_object_data);
     }

}
