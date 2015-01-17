package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APISedoxProductManager {

      public Transporter transport;

      public APISedoxProductManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void addCreditToSlave(java.lang.String slaveId, double amount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slaveId",new Gson().toJson(slaveId));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.method = "addCreditToSlave";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void addFileToProduct(java.lang.String base64EncodedFile, java.lang.String fileName, java.lang.String fileType, java.lang.String productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("base64EncodedFile",new Gson().toJson(base64EncodedFile));
          gs_json_object_data.args.put("fileName",new Gson().toJson(fileName));
          gs_json_object_data.args.put("fileType",new Gson().toJson(fileType));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "addFileToProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void addSlaveToUser(java.lang.String masterUserId, java.lang.String slaveUserId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("masterUserId",new Gson().toJson(masterUserId));
          gs_json_object_data.args.put("slaveUserId",new Gson().toJson(slaveUserId));
          gs_json_object_data.method = "addSlaveToUser";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void addUserCredit(java.lang.String id, java.lang.String description, int amount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("description",new Gson().toJson(description));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.method = "addUserCredit";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * This will disable/enable the developer. Useful if a developer goes on vacation
     * or needs an hour sleep.
     */
     public void changeDeveloperStatus(java.lang.String userId, boolean disabled)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("disabled",new Gson().toJson(disabled));
          gs_json_object_data.method = "changeDeveloperStatus";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void createSedoxProduct(com.thundashop.core.sedox.SedoxProduct sedoxProduct, java.lang.String base64encodedOriginalFile, java.lang.String originalFileName, java.lang.String forSlaveId, java.lang.String origin)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("sedoxProduct",new Gson().toJson(sedoxProduct));
          gs_json_object_data.args.put("base64encodedOriginalFile",new Gson().toJson(base64encodedOriginalFile));
          gs_json_object_data.args.put("originalFileName",new Gson().toJson(originalFileName));
          gs_json_object_data.args.put("forSlaveId",new Gson().toJson(forSlaveId));
          gs_json_object_data.args.put("origin",new Gson().toJson(origin));
          gs_json_object_data.method = "createSedoxProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public java.util.List getAllUsersWithNegativeCreditLimit()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUsersWithNegativeCreditLimit";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.sedox.SedoxUser>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public java.util.List getDevelopers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getDevelopers";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.sedox.SedoxUser>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public java.lang.String getExtraInformationForFile(java.lang.String productId, int fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "getExtraInformationForFile";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.sedox.SedoxProduct getProductById(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getProductById";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.sedox.SedoxProduct>() {}.getType();
          com.thundashop.core.sedox.SedoxProduct object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public java.util.List getProductsByDaysBack(int day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getProductsByDaysBack";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.sedox.SedoxProduct>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public java.util.List getProductsFirstUploadedByCurrentUser()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getProductsFirstUploadedByCurrentUser";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.sedox.SedoxProduct>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.sedox.SedoxProduct getSedoxProductByMd5Sum(java.lang.String md5sum)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("md5sum",new Gson().toJson(md5sum));
          gs_json_object_data.method = "getSedoxProductByMd5Sum";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.sedox.SedoxProduct>() {}.getType();
          com.thundashop.core.sedox.SedoxProduct object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public com.thundashop.core.sedox.SedoxUser getSedoxUserAccount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSedoxUserAccount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.sedox.SedoxUser>() {}.getType();
          com.thundashop.core.sedox.SedoxUser object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public com.thundashop.core.sedox.SedoxUser getSedoxUserAccountById(java.lang.String userid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userid",new Gson().toJson(userid));
          gs_json_object_data.method = "getSedoxUserAccountById";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.sedox.SedoxUser>() {}.getType();
          com.thundashop.core.sedox.SedoxUser object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public java.util.List getSlaves(java.lang.String masterUserId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("masterUserId",new Gson().toJson(masterUserId));
          gs_json_object_data.method = "getSlaves";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.sedox.SedoxUser>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.usermanager.data.User login(java.lang.String emailAddress, java.lang.String password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("emailAddress",new Gson().toJson(emailAddress));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "login";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.usermanager.data.User>() {}.getType();
          com.thundashop.core.usermanager.data.User object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void notifyForCustomer(java.lang.String productId, java.lang.String extraText)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("extraText",new Gson().toJson(extraText));
          gs_json_object_data.method = "notifyForCustomer";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.sedox.SedoxOrder purchaseOnlyForCustomer(java.lang.String productId, java.util.List files)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.method = "purchaseOnlyForCustomer";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.sedox.SedoxOrder>() {}.getType();
          com.thundashop.core.sedox.SedoxOrder object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public java.lang.String purchaseProduct(java.lang.String productId, java.util.List files)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.method = "purchaseProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void removeBinaryFileFromProduct(java.lang.String productId, int fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "removeBinaryFileFromProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void requestSpecialFile(java.lang.String productId, java.lang.String comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "requestSpecialFile";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public com.thundashop.core.sedox.SedoxProductSearchPage search(com.thundashop.core.sedox.SedoxSearch search)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("search",new Gson().toJson(search));
          gs_json_object_data.method = "search";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.sedox.SedoxProductSearchPage>() {}.getType();
          com.thundashop.core.sedox.SedoxProductSearchPage object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public java.util.List searchForUsers(java.lang.String searchString)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchString",new Gson().toJson(searchString));
          gs_json_object_data.method = "searchForUsers";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.usermanager.data.User>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void sendProductByMail(java.lang.String productId, java.lang.String extraText, java.util.List files)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("extraText",new Gson().toJson(extraText));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.method = "sendProductByMail";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Return the products created by days back.
     * day = 0 // Means that it will returns the list of todays files
     * day = 1 // Means that it will returns the list of yesterdays files
     *
     * @param day
     * @return
     * @throws ErrorException
     */
     public void setChecksum(java.lang.String productId, java.lang.String checksum)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("checksum",new Gson().toJson(checksum));
          gs_json_object_data.method = "setChecksum";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void setExtraInformationForFile(java.lang.String productId, int fileId, java.lang.String text)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.method = "setExtraInformationForFile";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void sync(java.lang.String option)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("option",new Gson().toJson(option));
          gs_json_object_data.method = "sync";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void toggleAllowNegativeCredit(java.lang.String userId, boolean allow)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("allow",new Gson().toJson(allow));
          gs_json_object_data.method = "toggleAllowNegativeCredit";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void toggleAllowWindowsApp(java.lang.String userId, boolean allow)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("allow",new Gson().toJson(allow));
          gs_json_object_data.method = "toggleAllowWindowsApp";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void toggleIsNorwegian(java.lang.String userId, boolean isNorwegian)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("isNorwegian",new Gson().toJson(isNorwegian));
          gs_json_object_data.method = "toggleIsNorwegian";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void togglePassiveSlaveMode(java.lang.String userId, boolean toggle)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("toggle",new Gson().toJson(toggle));
          gs_json_object_data.method = "togglePassiveSlaveMode";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void toggleSaleableProduct(java.lang.String productId, boolean saleable)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("saleable",new Gson().toJson(saleable));
          gs_json_object_data.method = "toggleSaleableProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void toggleStartStop(java.lang.String productId, boolean toggle)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("toggle",new Gson().toJson(toggle));
          gs_json_object_data.method = "toggleStartStop";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

}
