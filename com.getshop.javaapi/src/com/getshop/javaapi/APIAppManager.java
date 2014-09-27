package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIAppManager {

      public Transporter transport;

      public APIAppManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Create a new application.
     * @param appName The name of the application
     * @return
     */
     public com.thundashop.core.appmanager.data.ApplicationSettings createApplication(java.lang.String appName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appName",new Gson().toJson(appName));
          data.method = "createApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.appmanager.data.ApplicationSettings>() {}.getType();
          com.thundashop.core.appmanager.data.ApplicationSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete an application owned by you.
     * @param identificationId
     * @throws ErrorException
     */
     public void deleteApplication(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "deleteApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
     }

     /**
     * Get all the applications added to this store.
     * @param includeAppSettings Do you need the application settings object or not?
     * @throws ErrorException
     */
     public java.util.Map getAllApplicationSubscriptions(boolean includeAppSettings)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("includeAppSettings",new Gson().toJson(includeAppSettings));
          data.method = "getAllApplicationSubscriptions";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.Map<java.lang.String,com.thundashop.core.appmanager.data.ApplicationSubscription>>() {}.getType();
          java.util.Map object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all the applications connected to you.
     * @return
     */
     public com.thundashop.core.appmanager.data.AvailableApplications getAllApplications()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getAllApplications";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.appmanager.data.AvailableApplications>() {}.getType();
          com.thundashop.core.appmanager.data.AvailableApplications object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the settings for a given id.
     * @param id
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.appmanager.data.ApplicationSettings getApplication(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.appmanager.data.ApplicationSettings>() {}.getType();
          com.thundashop.core.appmanager.data.ApplicationSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Returns an application setting for the given id.
     *
     * @param appSettingsId
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.appmanager.data.ApplicationSettings getApplicationSettings(java.lang.String appSettingsId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appSettingsId",new Gson().toJson(appSettingsId));
          data.method = "getApplicationSettings";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.appmanager.data.ApplicationSettings>() {}.getType();
          com.thundashop.core.appmanager.data.ApplicationSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get a list of all applicationsettings that is in
     * use for this webopage.
     *
     * @return
     */
     public java.util.List getApplicationSettingsUsedByWebPage()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getApplicationSettingsUsedByWebPage";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.ApplicationSettings>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all application that has been marked for synchronization.
     * When this method is called all objects related to this will unqueued.
     * @return
     * @throws ErrorException
     */
     public java.util.List getSyncApplications()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getSyncApplications";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.appmanager.data.ApplicationSynchronization>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if the synchronization client is connected or not.
     * @return boolean
     * @throws ErrorException
     */
     public boolean isSyncToolConnected()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "isSyncToolConnected";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Boolean>() {}.getType();
          Boolean object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Save applications
     * @param settings
     */
     public void saveApplication(com.thundashop.core.appmanager.data.ApplicationSettings settings)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("settings",new Gson().toJson(settings));
          data.method = "saveApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
     }

     /**
     * Notify the synchronization server to synchronize this application for the logged on user.
     * @param id
     * @throws ErrorException
     */
     public void setSyncApplication(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "setSyncApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
     }

}
