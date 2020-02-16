package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIOcrManager {

      public Communicator transport;

      public APIOcrManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * The ordermanager handles all orders created by this store.<br>
     * An order is usually created after the order has been added to the cart.<br>
     */
     public void scanOcrFiles()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "scanOcrFiles";
          gs_json_object_data.interfaceName = "core.ocr.IOcrManager";
          String result = transport.send(gs_json_object_data);
     }

}
