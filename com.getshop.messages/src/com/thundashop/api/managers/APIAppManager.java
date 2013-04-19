package com.thundashop.api.managers;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import com.thundashop.core.common.JsonObject2;
import java.util.Map;
import java.util.List;
import com.thundashop.core.appmanager.data.ApplicationSubscription;
import com.thundashop.core.appmanager.data.ApplicationSynchronization;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.AvailableApplications;

public class APIAppManager {

      private Transporter transport;

      public APIAppManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Create a new application.
     * @param appName The name of the application
     * @return ApplicationSettings
     */

     public ApplicationSettings createApplication(String appName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("appName",new Gson().toJson(appName));
          data.method = "createApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<ApplicationSettings>() {}.getType();
          ApplicationSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Delete an application owned by you.
     * @param identificationId
     * @throws ErrorException 
     */

     public void deleteApplication(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "deleteApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
     }

     /**
     * Get all the applications added to this store.
     * @throws ErrorException 
     */

     public Map<String,ApplicationSubscription> getAllApplicationSubscriptions()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getAllApplicationSubscriptions";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<Map<String,ApplicationSubscription>>() {}.getType();
          Map<String,ApplicationSubscription> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all the applications connected to you.
     * @return AvailableApplications
     */

     public AvailableApplications getAllApplications()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getAllApplications";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<AvailableApplications>() {}.getType();
          AvailableApplications object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch the settings for a given id.
     * @param id
     * @return ApplicationSettings
     * @throws ErrorException 
     */

     public ApplicationSettings getApplication(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<ApplicationSettings>() {}.getType();
          ApplicationSettings object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all application that has been marked for synchronization.
     * When this method is called all objects related to this will unqueued.
     * @return List<ApplicationSynchronization>
     * @throws ErrorException 
     */

     public List<ApplicationSynchronization> getSyncApplications()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getSyncApplications";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<ApplicationSynchronization>>() {}.getType();
          List<ApplicationSynchronization> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all application that needs to be payed for.
     * @return List<ApplicationSubscription>
     * @throws ErrorException 
     */

     public List<ApplicationSubscription> getUnpayedSubscription()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.method = "getUnpayedSubscription";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<List<ApplicationSubscription>>() {}.getType();
          List<ApplicationSubscription> object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Check if the synchronization client is connected or not.
     * @return boolean
     * @throws ErrorException 
     */

     public boolean isSyncToolConnected()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
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

     public void saveApplication(ApplicationSettings settings)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
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

     public void setSyncApplication(String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new HashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "setSyncApplication";
          data.interfaceName = "core.appmanager.IAppManager";
          String result = transport.send(data);
     }

}
