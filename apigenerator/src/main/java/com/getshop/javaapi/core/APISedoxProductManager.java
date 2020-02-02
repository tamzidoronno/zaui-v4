package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISedoxProductManager {

      public Communicator transport;

      public APISedoxProductManager(Communicator transport){
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
     public void addCommentToUser(Object userId, Object comment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.method = "addCommentToUser";
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
     public void addCreditToSlave(Object slaveId, Object amount)  throws Exception  {
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
     public void addFileToProduct(Object base64EncodedFile, Object fileName, Object fileType, Object productId, Object options)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("base64EncodedFile",new Gson().toJson(base64EncodedFile));
          gs_json_object_data.args.put("fileName",new Gson().toJson(fileName));
          gs_json_object_data.args.put("fileType",new Gson().toJson(fileType));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("options",new Gson().toJson(options));
          gs_json_object_data.method = "addFileToProduct";
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
     public void addFileToProductAsync(Object sedoxBinaryFile, Object fileType, Object fileName, Object productId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("sedoxBinaryFile",new Gson().toJson(sedoxBinaryFile));
          gs_json_object_data.args.put("fileType",new Gson().toJson(fileType));
          gs_json_object_data.args.put("fileName",new Gson().toJson(fileName));
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.method = "addFileToProductAsync";
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
     public void addReference(Object productId, Object reference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.method = "addReference";
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
     public void addSlaveToUser(Object masterUserId, Object slaveUserId)  throws Exception  {
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
     public void addUserCredit(Object id, Object description, Object amount)  throws Exception  {
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
     public void changeDeveloperStatus(Object userId, Object disabled)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("disabled",new Gson().toJson(disabled));
          gs_json_object_data.method = "changeDeveloperStatus";
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
     public void clearManager()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "clearManager";
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
     public void createSedoxProduct(Object sedoxProduct, Object base64encodedOriginalFile, Object originalFileName, Object forSlaveId, Object origin, Object comment, Object useCredit, Object options, Object reference)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("sedoxProduct",new Gson().toJson(sedoxProduct));
          gs_json_object_data.args.put("base64encodedOriginalFile",new Gson().toJson(base64encodedOriginalFile));
          gs_json_object_data.args.put("originalFileName",new Gson().toJson(originalFileName));
          gs_json_object_data.args.put("forSlaveId",new Gson().toJson(forSlaveId));
          gs_json_object_data.args.put("origin",new Gson().toJson(origin));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.args.put("useCredit",new Gson().toJson(useCredit));
          gs_json_object_data.args.put("options",new Gson().toJson(options));
          gs_json_object_data.args.put("reference",new Gson().toJson(reference));
          gs_json_object_data.method = "createSedoxProduct";
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
     public void finishUpload(Object forSlaveId, Object sharedProduct, Object useCredit, Object comment, Object originalFile, Object cmdEncryptedFile, Object options, Object base64EncodeString, Object originalFileName, Object origin, Object fromUserId, Object referenceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("forSlaveId",new Gson().toJson(forSlaveId));
          gs_json_object_data.args.put("sharedProduct",new Gson().toJson(sharedProduct));
          gs_json_object_data.args.put("useCredit",new Gson().toJson(useCredit));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.args.put("originalFile",new Gson().toJson(originalFile));
          gs_json_object_data.args.put("cmdEncryptedFile",new Gson().toJson(cmdEncryptedFile));
          gs_json_object_data.args.put("options",new Gson().toJson(options));
          gs_json_object_data.args.put("base64EncodeString",new Gson().toJson(base64EncodeString));
          gs_json_object_data.args.put("originalFileName",new Gson().toJson(originalFileName));
          gs_json_object_data.args.put("origin",new Gson().toJson(origin));
          gs_json_object_data.args.put("fromUserId",new Gson().toJson(fromUserId));
          gs_json_object_data.args.put("referenceId",new Gson().toJson(referenceId));
          gs_json_object_data.method = "finishUpload";
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
     public JsonElement getAllUsers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUsers";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllUsersAsTreeNodes()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUsersAsTreeNodes";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getAllUsersWithNegativeCreditLimit()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllUsersWithNegativeCreditLimit";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getCurrentUserCreditHistory(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getCurrentUserCreditHistory";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getCurrentUserCreditHistoryCount(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getCurrentUserCreditHistoryCount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getDevelopers()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getDevelopers";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getExtraInformationForFile(Object productId, Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "getExtraInformationForFile";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getFileNotProcessedToDayCount(Object daysBack)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("daysBack",new Gson().toJson(daysBack));
          gs_json_object_data.method = "getFileNotProcessedToDayCount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getLatestProductsList(Object count)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("count",new Gson().toJson(count));
          gs_json_object_data.method = "getLatestProductsList";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getNextFileId()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getNextFileId";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getOrders(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getOrders";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getOrdersPageCount(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getOrdersPageCount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getPriceForProduct(Object productId, Object files)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.method = "getPriceForProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getProductById(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getProductById";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getProductIds()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getProductIds";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getProductsByDaysBack(Object day)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("day",new Gson().toJson(day));
          gs_json_object_data.method = "getProductsByDaysBack";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProductsFirstUploadedByCurrentUser(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getProductsFirstUploadedByCurrentUser";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getProductsFirstUploadedByCurrentUserTotalPages(Object filterData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filterData",new Gson().toJson(filterData));
          gs_json_object_data.method = "getProductsFirstUploadedByCurrentUserTotalPages";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getSedoxProductByMd5Sum(Object md5sum)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("md5sum",new Gson().toJson(md5sum));
          gs_json_object_data.method = "getSedoxProductByMd5Sum";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSedoxUserAccount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSedoxUserAccount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSedoxUserAccountById(Object userid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userid",new Gson().toJson(userid));
          gs_json_object_data.method = "getSedoxUserAccountById";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getSharedProductById(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getSharedProductById";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getSlaves(Object masterUserId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("masterUserId",new Gson().toJson(masterUserId));
          gs_json_object_data.method = "getSlaves";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getStatistic()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getStatistic";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getUploadHistory()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUploadHistory";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getUserFileDownloadCount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUserFileDownloadCount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement getUserFileUploadCount()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUserFileUploadCount";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void invokeCreditUpdate()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "invokeCreditUpdate";
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
     public JsonElement login(Object emailAddress, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("emailAddress",new Gson().toJson(emailAddress));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "login";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void markAsFinished(Object productId, Object finished)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("finished",new Gson().toJson(finished));
          gs_json_object_data.method = "markAsFinished";
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
     public void notifyForCustomer(Object productId, Object extraText)  throws Exception  {
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
     public JsonElement purchaseOnlyForCustomer(Object productId, Object files)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.method = "purchaseOnlyForCustomer";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement purchaseProduct(Object productId, Object files)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.method = "purchaseProduct";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void refreshEvcCredit()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "refreshEvcCredit";
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
     public void removeBinaryFileFromProduct(Object productId, Object fileId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.method = "removeBinaryFileFromProduct";
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
     public void removeSlaveFromMaster(Object slaveId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slaveId",new Gson().toJson(slaveId));
          gs_json_object_data.method = "removeSlaveFromMaster";
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
     public void requestSpecialFile(Object productId, Object comment)  throws Exception  {
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
     public JsonElement search(Object search)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("search",new Gson().toJson(search));
          gs_json_object_data.method = "search";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public JsonElement searchForUsers(Object searchString)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("searchString",new Gson().toJson(searchString));
          gs_json_object_data.method = "searchForUsers";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement searchUserFiles(Object search)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("search",new Gson().toJson(search));
          gs_json_object_data.method = "searchUserFiles";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
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
     public void sendProductByMail(Object productId, Object extraText, Object files)  throws Exception  {
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
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void sendProductToDifferentEmail(Object productId, Object emailAddress, Object files, Object extraText)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("emailAddress",new Gson().toJson(emailAddress));
          gs_json_object_data.args.put("files",new Gson().toJson(files));
          gs_json_object_data.args.put("extraText",new Gson().toJson(extraText));
          gs_json_object_data.method = "sendProductToDifferentEmail";
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
     public void setChecksum(Object productId, Object checksum)  throws Exception  {
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
     public void setCreditAllowedLimist(Object userId, Object creditlimit)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("creditlimit",new Gson().toJson(creditlimit));
          gs_json_object_data.method = "setCreditAllowedLimist";
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
     public void setEvcId(Object userId, Object evcId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("evcId",new Gson().toJson(evcId));
          gs_json_object_data.method = "setEvcId";
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
     public void setExtraInformationForFile(Object productId, Object fileId, Object text)  throws Exception  {
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
     * Developers is simply an getshop user that is registered as an developer.
     * Active developers are administrators that has an SedoxUser with the flag
     * isActiveDeveloper = true
     *
     * @return
     * @throws ErrorException
     */
     public void setFixedPrice(Object userId, Object price)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("price",new Gson().toJson(price));
          gs_json_object_data.method = "setFixedPrice";
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
     public void setPushoverId(Object pushover)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pushover",new Gson().toJson(pushover));
          gs_json_object_data.method = "setPushoverId";
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
     public void setPushoverIdForUser(Object pushover, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pushover",new Gson().toJson(pushover));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "setPushoverIdForUser";
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
     public void setSpecialRequestsForFile(Object productId, Object fileId, Object dpf, Object egr, Object decat, Object vmax, Object adblue, Object dtc, Object flaps)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("fileId",new Gson().toJson(fileId));
          gs_json_object_data.args.put("dpf",new Gson().toJson(dpf));
          gs_json_object_data.args.put("egr",new Gson().toJson(egr));
          gs_json_object_data.args.put("decat",new Gson().toJson(decat));
          gs_json_object_data.args.put("vmax",new Gson().toJson(vmax));
          gs_json_object_data.args.put("adblue",new Gson().toJson(adblue));
          gs_json_object_data.args.put("dtc",new Gson().toJson(dtc));
          gs_json_object_data.args.put("flaps",new Gson().toJson(flaps));
          gs_json_object_data.method = "setSpecialRequestsForFile";
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
     public void setType(Object productId, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "setType";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void sync(Object option)  throws Exception  {
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
     public void syncFromMagento(Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "syncFromMagento";
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
     public void toggleAllowNegativeCredit(Object userId, Object allow)  throws Exception  {
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
     public void toggleAllowWindowsApp(Object userId, Object allow)  throws Exception  {
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
     public void toggleBadCustomer(Object userId, Object badCustomer)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.args.put("badCustomer",new Gson().toJson(badCustomer));
          gs_json_object_data.method = "toggleBadCustomer";
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
     public void toggleIsNorwegian(Object userId, Object isNorwegian)  throws Exception  {
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
     public void togglePassiveSlaveMode(Object userId, Object toggle)  throws Exception  {
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
     public void toggleSaleableProduct(Object productId, Object saleable)  throws Exception  {
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
     public void toggleStartStop(Object productId, Object toggle)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("productId",new Gson().toJson(productId));
          gs_json_object_data.args.put("toggle",new Gson().toJson(toggle));
          gs_json_object_data.method = "toggleStartStop";
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
     public void transferCreditToSlave(Object slaveId, Object amount)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("slaveId",new Gson().toJson(slaveId));
          gs_json_object_data.args.put("amount",new Gson().toJson(amount));
          gs_json_object_data.method = "transferCreditToSlave";
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
     public void updateEvcCreditAccounts()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "updateEvcCreditAccounts";
          gs_json_object_data.interfaceName = "core.sedox.ISedoxProductManager";
          String result = transport.send(gs_json_object_data);
     }

}
