package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIInvoiceManager {

      public Transporter transport;

      public APIInvoiceManager(Transporter transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void createInvoice(java.lang.String orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "createInvoice";
          gs_json_object_data.interfaceName = "core.pdf.IInvoiceManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public java.lang.String getBase64EncodedInvoice(java.lang.String orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "getBase64EncodedInvoice";
          gs_json_object_data.interfaceName = "core.pdf.IInvoiceManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
