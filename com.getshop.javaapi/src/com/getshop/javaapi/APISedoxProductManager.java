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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("slaveId",new Gson().toJson(slaveId));
          data.args.put("amount",new Gson().toJson(amount));
          data.method = "addCreditToSlave";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("base64EncodedFile",new Gson().toJson(base64EncodedFile));
          data.args.put("fileName",new Gson().toJson(fileName));
          data.args.put("fileType",new Gson().toJson(fileType));
          data.args.put("productId",new Gson().toJson(productId));
          data.method = "addFileToProduct";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("masterUserId",new Gson().toJson(masterUserId));
          data.args.put("slaveUserId",new Gson().toJson(slaveUserId));
          data.method = "addSlaveToUser";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.args.put("description",new Gson().toJson(description));
          data.args.put("amount",new Gson().toJson(amount));
          data.method = "addUserCredit";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
     }

     /**
     * This will disable/enable the developer. Useful if a developer goes on vacation
     * or needs an hour sleep.
     */
     public void changeDeveloperStatus(java.lang.String userId, boolean disabled)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("disabled",new Gson().toJson(disabled));
          data.method = "changeDeveloperStatus";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("sedoxProduct",new Gson().toJson(sedoxProduct));
          data.args.put("base64encodedOriginalFile",new Gson().toJson(base64encodedOriginalFile));
          data.args.put("originalFileName",new Gson().toJson(originalFileName));
          data.args.put("forSlaveId",new Gson().toJson(forSlaveId));
          data.args.put("origin",new Gson().toJson(origin));
          data.method = "createSedoxProduct";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
     }

     /**
     *
     * @author ktonder
     */
     public java.util.List getAllUsersWithNegativeCreditLimit()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllUsersWithNegativeCreditLimit";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getDevelopers";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("fileId",new Gson().toJson(fileId));
          data.method = "getExtraInformationForFile";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getProductById";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("day",new Gson().toJson(day));
          data.method = "getProductsByDaysBack";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getProductsFirstUploadedByCurrentUser";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("md5sum",new Gson().toJson(md5sum));
          data.method = "getSedoxProductByMd5Sum";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getSedoxUserAccount";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userid",new Gson().toJson(userid));
          data.method = "getSedoxUserAccountById";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("masterUserId",new Gson().toJson(masterUserId));
          data.method = "getSlaves";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("emailAddress",new Gson().toJson(emailAddress));
          data.args.put("password",new Gson().toJson(password));
          data.method = "login";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("extraText",new Gson().toJson(extraText));
          data.method = "notifyForCustomer";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("files",new Gson().toJson(files));
          data.method = "purchaseOnlyForCustomer";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("files",new Gson().toJson(files));
          data.method = "purchaseProduct";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("fileId",new Gson().toJson(fileId));
          data.method = "removeBinaryFileFromProduct";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("comment",new Gson().toJson(comment));
          data.method = "requestSpecialFile";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
     }

     /**
     *
     * @author ktonder
     */
     public com.thundashop.core.sedox.SedoxProductSearchPage search(com.thundashop.core.sedox.SedoxSearch search)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("search",new Gson().toJson(search));
          data.method = "search";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("searchString",new Gson().toJson(searchString));
          data.method = "searchForUsers";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("extraText",new Gson().toJson(extraText));
          data.args.put("files",new Gson().toJson(files));
          data.method = "sendProductByMail";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("checksum",new Gson().toJson(checksum));
          data.method = "setChecksum";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("fileId",new Gson().toJson(fileId));
          data.args.put("text",new Gson().toJson(text));
          data.method = "setExtraInformationForFile";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
     }

     /**
     *
     * @author ktonder
     */
     public void sync(java.lang.String option)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("option",new Gson().toJson(option));
          data.method = "sync";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("allow",new Gson().toJson(allow));
          data.method = "toggleAllowNegativeCredit";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("allow",new Gson().toJson(allow));
          data.method = "toggleAllowWindowsApp";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("userId",new Gson().toJson(userId));
          data.args.put("toggle",new Gson().toJson(toggle));
          data.method = "togglePassiveSlaveMode";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("saleable",new Gson().toJson(saleable));
          data.method = "toggleSaleableProduct";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
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
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("productId",new Gson().toJson(productId));
          data.args.put("toggle",new Gson().toJson(toggle));
          data.method = "toggleStartStop";
          data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(data);
     }

}
