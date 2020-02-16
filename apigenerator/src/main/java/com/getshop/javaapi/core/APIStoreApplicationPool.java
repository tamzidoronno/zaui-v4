package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIStoreApplicationPool {

      public Communicator transport;

      public APIStoreApplicationPool(Communicator transport){
           this.transport = transport;
      }

     /**
     * Activate an application.
     *
     * @param applicationId
     */
     public void activateApplication(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "activateApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Actiave a module by a given module id.
     *
     * @param module
     */
     public void activateModule(Object module)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("module",new Gson().toJson(module));
          gs_json_object_data.method = "activateModule";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Activate an application.
     *
     * @param applicationId
     */
     public void activatePaymentApplication(Object applicationId, Object account)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.args.put("account",new Gson().toJson(account));
          gs_json_object_data.method = "activatePaymentApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Actiave a module by a given module id.
     *
     * @param module
     */
     public void deactivateApplication(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "deactivateApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns shipment applications.
     *
     * @return
     */
     public JsonElement getActivatedApplications(Object systemType)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("systemType",new Gson().toJson(systemType));
          gs_json_object_data.method = "getActivatedApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all available applications.
     *
     * @return
     */
     public JsonElement getActivatedModules()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getActivatedModules";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns shipment applications.
     *
     * @return
     */
     public JsonElement getActivatedPaymentApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getActivatedPaymentApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return a list of all applucation modules available
     *
     * @return
     */
     public JsonElement getAllAvailableModules()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllAvailableModules";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return an activated application by the given Id.
     *
     * @param id
     * @return
     */
     public JsonElement getApplication(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all applications this store has activated.
     *
     * @return
     */
     public JsonElement getApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all applications that are available for this store.
     * This also includes applications that has not yet been activated by the
     * administrator.
     *
     * @return
     */
     public JsonElement getAvailableApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * This is a filtered list of the getAvailableApplications function.
     *
     * @return
     */
     public JsonElement getAvailableApplicationsThatIsNotActivated()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableApplicationsThatIsNotActivated";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all available theme applications.
     *
     * @return
     */
     public JsonElement getAvailableThemeApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableThemeApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns shipment applications.
     *
     * @return
     */
     public JsonElement getPaymentApplicationsIds()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPaymentApplicationsIds";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Activate an application.
     *
     * @param applicationId
     */
     public JsonElement getPaymentSettingsApplication()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPaymentSettingsApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns shipment applications.
     *
     * @return
     */
     public JsonElement getShippingApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getShippingApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Return the themeapplication that is currently set.
     *
     * @return
     */
     public JsonElement getThemeApplication()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getThemeApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Returns a list of all applications this store has activated.
     *
     * @return
     */
     public JsonElement isActivated(Object appId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appId",new Gson().toJson(appId));
          gs_json_object_data.method = "isActivated";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Actiave a module by a given module id.
     *
     * @param module
     */
     public void setSetting(Object applicationId, Object settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "setSetting";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Use this function to change or set the theme application you wish to use.
     *
     * @param applicationId
     */
     public void setThemeApplication(Object applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "setThemeApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

}
