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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("orderId",new Gson().toJson(orderId));
          data.method = "createInvoice";
          data.interfaceName = "core.pdf.IInvoiceManager";
          String result = transport.send(data);
     }

}
