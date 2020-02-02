package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIBigStock {

      public Communicator transport;

      public APIBigStock(Communicator transport){
           this.transport = transport;
      }

     /**
     * Update the credit account.
     *
     * @param credits
     * @param password
     * @throws ErrorException
     */
     public void addGetShopImageIdToBigStockOrder(Object downloadUrl, Object imageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("downloadUrl",new Gson().toJson(downloadUrl));
          gs_json_object_data.args.put("imageId",new Gson().toJson(imageId));
          gs_json_object_data.method = "addGetShopImageIdToBigStockOrder";
          gs_json_object_data.interfaceName = "core.bigstock.IBigStock";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update the credit account.
     *
     * @param credits
     * @param password
     * @throws ErrorException
     */
     public JsonElement getAvailableCredits()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableCredits";
          gs_json_object_data.interfaceName = "core.bigstock.IBigStock";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Purchases a picture from the bigstock library.
     *
     * @param imageId
     * @param sizeCode
     * @return downloadUrl
     * @throws ErrorException
     */
     public JsonElement purchaseImage(Object imageId, Object sizeCode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("imageId",new Gson().toJson(imageId));
          gs_json_object_data.args.put("sizeCode",new Gson().toJson(sizeCode));
          gs_json_object_data.method = "purchaseImage";
          gs_json_object_data.interfaceName = "core.bigstock.IBigStock";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Update the credit account.
     *
     * @param credits
     * @param password
     * @throws ErrorException
     */
     public void setCreditAccount(Object credits, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("credits",new Gson().toJson(credits));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "setCreditAccount";
          gs_json_object_data.interfaceName = "core.bigstock.IBigStock";
          String result = transport.send(gs_json_object_data);
     }

}
