package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APISupportManager {

      public Communicator transport;

      public APISupportManager(Communicator transport){
           this.transport = transport;
      }

     /**
     *
     * @author ktonder
     */
     public void addToSupportCase(Object supportCaseId, Object history)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("supportCaseId",new Gson().toJson(supportCaseId));
          gs_json_object_data.args.put("history",new Gson().toJson(history));
          gs_json_object_data.method = "addToSupportCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void assignCareTakerForCase(Object caseId, Object userId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("caseId",new Gson().toJson(caseId));
          gs_json_object_data.args.put("userId",new Gson().toJson(userId));
          gs_json_object_data.method = "assignCareTakerForCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeModuleForCase(Object caseId, Object moduleId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("caseId",new Gson().toJson(caseId));
          gs_json_object_data.args.put("moduleId",new Gson().toJson(moduleId));
          gs_json_object_data.method = "changeModuleForCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeStateForCase(Object caseId, Object stateId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("caseId",new Gson().toJson(caseId));
          gs_json_object_data.args.put("stateId",new Gson().toJson(stateId));
          gs_json_object_data.method = "changeStateForCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeSupportCaseType(Object caseId, Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("caseId",new Gson().toJson(caseId));
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "changeSupportCaseType";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void changeTitleOnCase(Object caseId, Object title)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("caseId",new Gson().toJson(caseId));
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.method = "changeTitleOnCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void createLead(Object name, Object email, Object prefix, Object phone)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("name",new Gson().toJson(name));
          gs_json_object_data.args.put("email",new Gson().toJson(email));
          gs_json_object_data.args.put("prefix",new Gson().toJson(prefix));
          gs_json_object_data.args.put("phone",new Gson().toJson(phone));
          gs_json_object_data.method = "createLead";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement createSupportCase(Object supportCase)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("supportCase",new Gson().toJson(supportCase));
          gs_json_object_data.method = "createSupportCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void deleteLead(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteLead";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getFeatureListEntry(Object entryId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.method = "getFeatureListEntry";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getFeatureThree(Object moduleId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("moduleId",new Gson().toJson(moduleId));
          gs_json_object_data.method = "getFeatureThree";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLead(Object leadId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("leadId",new Gson().toJson(leadId));
          gs_json_object_data.method = "getLead";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getLeads(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getLeads";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getServerStatusList()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getServerStatusList";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSupportCase(Object supportCaseId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("supportCaseId",new Gson().toJson(supportCaseId));
          gs_json_object_data.method = "getSupportCase";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSupportCases(Object filter)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("filter",new Gson().toJson(filter));
          gs_json_object_data.method = "getSupportCases";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public JsonElement getSupportStatistics()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getSupportStatistics";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     *
     * @author ktonder
     */
     public void helloWorld()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "helloWorld";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveFeatureThree(Object moduleId, Object list)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("moduleId",new Gson().toJson(moduleId));
          gs_json_object_data.args.put("list",new Gson().toJson(list));
          gs_json_object_data.method = "saveFeatureThree";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void saveLead(Object lead)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("lead",new Gson().toJson(lead));
          gs_json_object_data.method = "saveLead";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     *
     * @author ktonder
     */
     public void updateFeatureListEntry(Object entryId, Object text, Object title, Object language)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("entryId",new Gson().toJson(entryId));
          gs_json_object_data.args.put("text",new Gson().toJson(text));
          gs_json_object_data.args.put("title",new Gson().toJson(title));
          gs_json_object_data.args.put("language",new Gson().toJson(language));
          gs_json_object_data.method = "updateFeatureListEntry";
          gs_json_object_data.interfaceName = "core.support.ISupportManager";
          String result = transport.send(gs_json_object_data);
     }

}
