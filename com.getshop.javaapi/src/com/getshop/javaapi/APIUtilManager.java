package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIUtilManager {

      public Transporter transport;

      public APIUtilManager(Transporter transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public java.util.HashMap getCompaniesFromBrReg(java.lang.String search)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("search",new Gson().toJson(search));
          data.method = "getCompaniesFromBrReg";
          data.interfaceName = "core.utils.IUtilManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,java.lang.String>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public com.thundashop.core.usermanager.data.Company getCompanyFromBrReg(java.lang.String companyVatNumber)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("companyVatNumber",new Gson().toJson(companyVatNumber));
          data.method = "getCompanyFromBrReg";
          data.interfaceName = "core.utils.IUtilManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.Company>() {}.getType();
          com.thundashop.core.usermanager.data.Company object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
