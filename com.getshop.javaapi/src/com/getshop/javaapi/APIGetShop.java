package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIGetShop {

      public Transporter transport;

      public APIGetShop(Transporter transport){
           this.transport = transport;
      }

     /**
     *
     * @param userId
     * @param partner
     * @param password
     * @throws ErrorException
     */
     public void addUserToPartner(java.lang.String userId, java.lang.String partner, java.lang.String password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("partner",new Gson().toJson(partner));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "addUserToPartner";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public com.thundashop.core.storemanager.data.Store createWebPage(com.thundashop.core.getshop.data.WebPageData webpageData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("webpageData",new Gson().toJson(webpageData));
          gs_json_object_data.method = "createWebPage";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.storemanager.data.Store>() {}.getType();
          com.thundashop.core.storemanager.data.Store object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Find the store address for a given application.
     * @param uuid The appid.
     * @return
     * @throws ErrorException
     */
     public java.lang.String findAddressForApplication(java.lang.String uuid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("uuid",new Gson().toJson(uuid));
          gs_json_object_data.method = "findAddressForApplication";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Need to figure out what address is connected to a specific uuid?
     * Remember this is query is quite slow. so cache the result.
     * @param uuid
     * @return
     * @throws ErrorException
     */
     public java.lang.String findAddressForUUID(java.lang.String uuid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("uuid",new Gson().toJson(uuid));
          gs_json_object_data.method = "findAddressForUUID";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.getshop.data.PartnerData getPartnerData(java.lang.String partnerId, java.lang.String password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("partnerId",new Gson().toJson(partnerId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getPartnerData";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.getshop.data.PartnerData>() {}.getType();
          com.thundashop.core.getshop.data.PartnerData object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @param code
     * @return
     */
     public java.util.List getStores(java.lang.String code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "getStores";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.getshop.data.GetshopStore>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public void saveSmsCallback(com.thundashop.core.getshop.data.SmsResponse smsResponses)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smsResponses",new Gson().toJson(smsResponses));
          gs_json_object_data.method = "saveSmsCallback";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @param ids
     * @throws ErrorException
     */
     public void setApplicationList(java.util.List ids, java.lang.String partnerId, java.lang.String password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("ids",new Gson().toJson(ids));
          gs_json_object_data.args.put("partnerId",new Gson().toJson(partnerId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "setApplicationList";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public java.lang.String startStoreFromStore(com.thundashop.core.getshop.data.StartData startData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startData",new Gson().toJson(startData));
          gs_json_object_data.method = "startStoreFromStore";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
