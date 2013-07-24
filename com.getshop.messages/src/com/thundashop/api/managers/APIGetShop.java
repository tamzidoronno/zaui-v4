package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.getshop.data.GetshopStore;
import java.util.List;

public class APIGetShop {

      private Transporter transport;

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

     public void addUserToPartner(String userId, String partner, String password)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("partner",new Gson().toJson(partner));
          data.args.put("password",new Gson().toJson(password));
          data.method = "addUserToPartner";
          data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(data);
     }

     /**
     * When an administrator has logged on, it can call on this call to connect its store to a partner.
     */

     public void connectStoreToPartner(String partner)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("partner",new Gson().toJson(partner));
          data.method = "connectStoreToPartner";
          data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(data);
     }

     /**
     * Need to figure out what address is connected to a specific uuid?
     * @param uuid
     * @return String
     * @throws ErrorException 
     */

     public String findAddressForUUID(String uuid)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("uuid",new Gson().toJson(uuid));
          data.method = "findAddressForUUID";
          data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<String>() {}.getType();
          String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get the partner id attached to this user.
     * @return String
     * @throws ErrorException 
     */

     public String getPartnerId()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getPartnerId";
          data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<String>() {}.getType();
          String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * 
     * @param code
     * @return List<GetshopStore>
     */

     public List<GetshopStore> getStores(String code)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("code",new Gson().toJson(code));
          data.method = "getStores";
          data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<GetshopStore>>() {}.getType();
          List<GetshopStore> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * 
     * @param userId
     * @param partner
     * @param password
     * @throws ErrorException 
     */

     public List<GetshopStore> getStoresConnectedToMe()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getStoresConnectedToMe";
          data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<GetshopStore>>() {}.getType();
          List<GetshopStore> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
