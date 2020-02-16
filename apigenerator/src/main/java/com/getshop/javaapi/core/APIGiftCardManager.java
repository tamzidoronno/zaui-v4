package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGiftCardManager {

      public Communicator transport;

      public APIGiftCardManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void deleteGiftCard(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteGiftCard";
          gs_json_object_data.interfaceName = "core.giftcard.IGiftCardManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllCards()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllCards";
          gs_json_object_data.interfaceName = "core.giftcard.IGiftCardManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getGiftCard(Object giftCardCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("giftCardCode",new Gson().toJson(giftCardCode));
          gs_json_object_data.method = "getGiftCard";
          gs_json_object_data.interfaceName = "core.giftcard.IGiftCardManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement payOrderWithGiftCard(Object code, Object orderId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.method = "payOrderWithGiftCard";
          gs_json_object_data.interfaceName = "core.giftcard.IGiftCardManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void saveGiftCard(Object card)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("card",new Gson().toJson(card));
          gs_json_object_data.method = "saveGiftCard";
          gs_json_object_data.interfaceName = "core.giftcard.IGiftCardManager";
          String result = transport.send(gs_json_object_data);
     }

}
