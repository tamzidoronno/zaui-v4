package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPmsGetShopOverView {

      public Communicator transport;

      public APIPmsGetShopOverView(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author boggi
     */
     public JsonElement getCustomerObject(Object storeI)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeI",new Gson().toJson(storeI));
          gs_json_object_data.method = "getCustomerObject";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsGetShopOverView";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public JsonElement getCustomerToSetup()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getCustomerToSetup";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsGetShopOverView";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author boggi
     */
     public void saveCustomerObject(Object object)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("object",new Gson().toJson(object));
          gs_json_object_data.method = "saveCustomerObject";
          gs_json_object_data.interfaceName = "core.pmsmanager.IPmsGetShopOverView";
          String result = transport.send(gs_json_object_data);
     }

}
