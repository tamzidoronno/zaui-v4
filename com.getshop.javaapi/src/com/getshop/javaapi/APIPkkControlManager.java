package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIPkkControlManager {

      public Transporter transport;

      public APIPkkControlManager(Transporter transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public com.thundashop.core.pkkcontrol.PkkControlData getPkkControlData(java.lang.String licensePlate)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("licensePlate",new Gson().toJson(licensePlate));
          data.method = "getPkkControlData";
          data.interfaceName = "core.pkk.IPkkControlManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.pkkcontrol.PkkControlData>() {}.getType();
          com.thundashop.core.pkkcontrol.PkkControlData object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public java.util.List getPkkControls()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getPkkControls";
          data.interfaceName = "core.pkk.IPkkControlManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.pkkcontrol.PkkControlData>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void registerPkkControl(com.thundashop.core.pkkcontrol.PkkControlData data)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("data",new Gson().toJson(data));
          data.method = "registerPkkControl";
          data.interfaceName = "core.pkk.IPkkControlManager";
          String result = transport.send(data);
     }

     /**
     *
     * @author ktonder
     */
     public void removePkkControl(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "removePkkControl";
          data.interfaceName = "core.pkk.IPkkControlManager";
          String result = transport.send(data);
     }

}
