package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIStoreApplicationPool {

      public Transporter transport;

      public APIStoreApplicationPool(Transporter transport){
           this.transport = transport;
      }

     /**
     * Activate an application.
     *
     * @param applicationId
     */
     public void activateApplication(java.lang.String applicationId)  throws Exception  {
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
     public void activateModule(java.lang.String module)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("module",new Gson().toJson(module));
          gs_json_object_data.method = "activateModule";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Actiave a module by a given module id.
     *
     * @param module
     */
     public void deactivateApplication(java.lang.String applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "deactivateApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Returns a list of all available applications.
     *
     * @return
     */
     public java.util.List getActivatedModules()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getActivatedModules";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.ApplicationModule>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return a list of all applucation modules available
     *
     * @return
     */
     public java.util.List getAllAvailableModules()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAllAvailableModules";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.ApplicationModule>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return an activated application by the given Id.
     *
     * @param id
     * @return
     */
     public com.thundashop.core.appmanager.data.Application getApplication(java.lang.String id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.appmanager.data.Application>() {}.getType();
          com.thundashop.core.appmanager.data.Application object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list of all applications this store has activated.
     *
     * @return
     */
     public java.util.List getApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.Application>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list of all applications that are available for this store.
     * This also includes applications that has not yet been activated by the
     * administrator.
     *
     * @return
     */
     public java.util.List getAvailableApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.Application>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * This is a filtered list of the getAvailableApplications function.
     *
     * @return
     */
     public java.util.List getAvailableApplicationsThatIsNotActivated()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableApplicationsThatIsNotActivated";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.Application>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns a list of all available theme applications.
     *
     * @return
     */
     public java.util.List getAvailableThemeApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getAvailableThemeApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.Application>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Activate an application.
     *
     * @param applicationId
     */
     public java.util.HashMap getPaymentSettingsApplication()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getPaymentSettingsApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,java.util.List<com.thundashop.core.storemanager.data.SettingsRow>>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns shipment applications.
     *
     * @return
     */
     public java.util.List getShippingApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getShippingApplications";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.Application>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Return the themeapplication that is currently set.
     *
     * @return
     */
     public com.thundashop.core.appmanager.data.Application getThemeApplication()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getThemeApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.appmanager.data.Application>() {}.getType();
          com.thundashop.core.appmanager.data.Application object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Actiave a module by a given module id.
     *
     * @param module
     */
     public void setSetting(java.lang.String applicationId, com.thundashop.core.common.Setting settings)  throws Exception  {
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
     public void setThemeApplication(java.lang.String applicationId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.method = "setThemeApplication";
          gs_json_object_data.interfaceName = "core.applications.IStoreApplicationPool";
          String result = transport.send(gs_json_object_data);
     }

}
