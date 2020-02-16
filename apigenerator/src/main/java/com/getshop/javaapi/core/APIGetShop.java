package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIGetShop {

      public Communicator transport;

      public APIGetShop(Communicator transport){
           this.transport = transport;
      }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public void addLeadHistory(Object leadId, Object comment, Object start, Object end, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("leadId",new Gson().toJson(leadId));
          gs_json_object_data.args.put("comment",new Gson().toJson(comment));
          gs_json_object_data.args.put("start",new Gson().toJson(start));
          gs_json_object_data.args.put("end",new Gson().toJson(end));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "addLeadHistory";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @param code
     * @return
     */
     public void addToDibsAutoCollect(Object orderId, Object storeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("orderId",new Gson().toJson(orderId));
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.method = "addToDibsAutoCollect";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @param userId
     * @param partner
     * @param password
     * @throws ErrorException
     */
     public void addUserToPartner(Object userId, Object partner, Object password)  throws Exception  {
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
     public JsonElement canInvoiceOverEhf(Object vatNumber)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("vatNumber",new Gson().toJson(vatNumber));
          gs_json_object_data.method = "canInvoiceOverEhf";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement canStartRestoringUnit(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "canStartRestoringUnit";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public void changeLeadState(Object leadId, Object state)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("leadId",new Gson().toJson(leadId));
          gs_json_object_data.args.put("state",new Gson().toJson(state));
          gs_json_object_data.method = "changeLeadState";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement createLead(Object name)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.method = "createLead";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement createNewStore(Object startData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startData",new Gson().toJson(startData));
          gs_json_object_data.method = "createNewStore";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement createWebPage(Object webpageData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("webpageData",new Gson().toJson(webpageData));
          gs_json_object_data.method = "createWebPage";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find the store address for a given application.
     * @param uuid The appid.
     * @return
     * @throws ErrorException
     */
     public JsonElement findAddressForApplication(Object uuid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("uuid",new Gson().toJson(uuid));
          gs_json_object_data.method = "findAddressForApplication";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Need to figure out what address is connected to a specific uuid?
     * Remember this is query is quite slow. so cache the result.
     * @param uuid
     * @return
     * @throws ErrorException
     */
     public JsonElement findAddressForUUID(Object uuid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("uuid",new Gson().toJson(uuid));
          gs_json_object_data.method = "findAddressForUUID";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement getBase64EncodedPDFWebPage(Object urlToPage)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("urlToPage",new Gson().toJson(urlToPage));
          gs_json_object_data.method = "getBase64EncodedPDFWebPage";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement getBase64EncodedPDFWebPageFromHtml(Object html)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("html",new Gson().toJson(html));
          gs_json_object_data.method = "getBase64EncodedPDFWebPageFromHtml";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement getIpForUnitId(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getIpForUnitId";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement getLead(Object leadId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("leadId",new Gson().toJson(leadId));
          gs_json_object_data.method = "getLead";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement getLeads()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLeads";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @param code
     * @return
     */
     public JsonElement getOrdersToAutoPayFromDibs()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getOrdersToAutoPayFromDibs";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public JsonElement getPartnerData(Object partnerId, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("partnerId",new Gson().toJson(partnerId));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "getPartnerData";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement getRecoveryStatusForUnit(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getRecoveryStatusForUnit";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @param code
     * @return
     */
     public JsonElement getStores(Object code)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("code",new Gson().toJson(code));
          gs_json_object_data.method = "getStores";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement getUnitsAskedForUpdate()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getUnitsAskedForUpdate";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public void insertNewStore(Object password, Object newAddress, Object storeDatas, Object newStoreId, Object startData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.args.put("newAddress",new Gson().toJson(newAddress));
          gs_json_object_data.args.put("storeDatas",new Gson().toJson(storeDatas));
          gs_json_object_data.args.put("newStoreId",new Gson().toJson(newStoreId));
          gs_json_object_data.args.put("startData",new Gson().toJson(startData));
          gs_json_object_data.method = "insertNewStore";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public void loadEhfCompanies()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "loadEhfCompanies";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public void markLeadHistoryCompleted(Object leadHistoryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("leadHistoryId",new Gson().toJson(leadHistoryId));
          gs_json_object_data.method = "markLeadHistoryCompleted";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public void recoveryCompleted(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "recoveryCompleted";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get partner data for this user.
     * @return
     * @throws ErrorException
     */
     public void saveLead(Object lead)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lead",new Gson().toJson(lead));
          gs_json_object_data.method = "saveLead";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public void saveSmsCallback(Object smsResponses)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("smsResponses",new Gson().toJson(smsResponses));
          gs_json_object_data.method = "saveSmsCallback";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**Current leads
     *
     * @param ids
     * @throws ErrorException
     */
     public void setApplicationList(Object ids, Object partnerId, Object password)  throws Exception  {
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
     public void setRecoveryStatusForUnit(Object id, Object status)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("status",new Gson().toJson(status));
          gs_json_object_data.method = "setRecoveryStatusForUnit";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public void startRecoveryForUnit(Object id, Object ip, Object password)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.args.put("ip",new Gson().toJson(ip));
          gs_json_object_data.args.put("password",new Gson().toJson(password));
          gs_json_object_data.method = "startRecoveryForUnit";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement startStoreFromStore(Object startData)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("startData",new Gson().toJson(startData));
          gs_json_object_data.method = "startStoreFromStore";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new webpage
     * @return
     */
     public void toggleRemoteEditing()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "toggleRemoteEditing";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public void triggerPullRequest(Object storeId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("storeId",new Gson().toJson(storeId));
          gs_json_object_data.method = "triggerPullRequest";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new webpage
     * @return
     */
     public JsonElement unitsTryingToRecover()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "unitsTryingToRecover";
          gs_json_object_data.interfaceName = "core.getshop.IGetShop";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

}
