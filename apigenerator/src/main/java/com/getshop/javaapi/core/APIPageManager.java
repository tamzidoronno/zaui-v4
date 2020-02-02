package com.getshop.javaapi.core;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import java.util.LinkedHashMap;
import com.google.gson.JsonElement;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.Communicator;
public class APIPageManager {

      public Communicator transport;

      public APIPageManager(Communicator transport){
           this.transport = transport;
      }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement accessDenied(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "accessDenied";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public JsonElement addApplication(Object applicationId, Object pageCellId, Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationId",new Gson().toJson(applicationId));
          gs_json_object_data.args.put("pageCellId",new Gson().toJson(pageCellId));
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "addApplication";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void addComment(Object pageComment)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageComment",new Gson().toJson(pageComment));
          gs_json_object_data.method = "addComment";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add an existing application to the application area
     *
     * @param pageId
     * @param appId
     * @param area
     * @throws ErrorException
     */
     public void addExistingApplicationToPageArea(Object pageId, Object appId, Object area)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("appId",new Gson().toJson(appId));
          gs_json_object_data.args.put("area",new Gson().toJson(area));
          gs_json_object_data.method = "addExistingApplicationToPageArea";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add an cell to an specific earea.
     * @param pageId
     * @param incell
     * @param beforecell
     * @param direction
     * @param area header/footer/body if nothing set it will default to body.
     * @return
     * @throws ErrorException
     */
     public JsonElement addLayoutCell(Object pageId, Object incell, Object beforecell, Object direction, Object area)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("incell",new Gson().toJson(incell));
          gs_json_object_data.args.put("beforecell",new Gson().toJson(beforecell));
          gs_json_object_data.args.put("direction",new Gson().toJson(direction));
          gs_json_object_data.args.put("area",new Gson().toJson(area));
          gs_json_object_data.method = "addLayoutCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void changeModule(Object moduleId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("moduleId",new Gson().toJson(moduleId));
          gs_json_object_data.method = "changeModule";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Change the userlevel for a given page. Make it accessible for only administrators / editors / customers.<br>
     * Everyone with a higher userlevel will allways gain access to the userlevels below.
     * @param pageId The id of the page to change.
     * @param userLevel The userlevel to set ADMINISTRATOR = 100, EDITOR = 50, CUSTOMER = 10
     * @return
     * @throws ErrorException
     */
     public JsonElement changePageUserLevel(Object pageId, Object userLevel)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("userLevel",new Gson().toJson(userLevel));
          gs_json_object_data.method = "changePageUserLevel";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException
     */
     public void clearPage(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "clearPage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Remove all applications for specified page area at specified page.
     *
     * @param pageId
     * @throws ErrorException
     */
     public void clearPageArea(Object pageId, Object pageArea)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("pageArea",new Gson().toJson(pageArea));
          gs_json_object_data.method = "clearPageArea";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add an cell to an specific earea.
     * @param pageId
     * @param incell
     * @param beforecell
     * @param direction
     * @param area header/footer/body if nothing set it will default to body.
     * @return
     * @throws ErrorException
     */
     public void createHeaderFooter(Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "createHeaderFooter";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void createModal(Object modalName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("modalName",new Gson().toJson(modalName));
          gs_json_object_data.method = "createModal";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Create a new row to add application areas to for a given page.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement createNewRow(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "createNewRow";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Create a new page.
     * This page can be used to stick applications to it.
     *
     * @return
     * @throws ErrorException
     */
     public JsonElement createPage()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "createPage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Delete an application from the store
     * removes all references where it has been used.
     *
     * Suitable for singleton applications
     *
     * @param id
     * @throws ErrorException
     */
     public void deleteApplication(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deleteApplication";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void deleteComment(Object commentId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("commentId",new Gson().toJson(commentId));
          gs_json_object_data.method = "deleteComment";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Delete the page with the id.
     *
     * @param id
     */
     public void deletePage(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "deletePage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add an cell to an specific earea.
     * @param pageId
     * @param incell
     * @param beforecell
     * @param direction
     * @param area header/footer/body if nothing set it will default to body.
     * @return
     * @throws ErrorException
     */
     public JsonElement dropCell(Object pageId, Object cellId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.method = "dropCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all pages that a application instance is connected to..
     *
     * @param appIds A list of application ids to resolve pages for.
     * @throws ErrorException
     */
     public void flattenMobileLayout(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "flattenMobileLayout";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Get all applications from the applicationPool.
     *
     * @return
     */
     public JsonElement getApplications()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getApplications";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get all applications from the applicationPool.
     * based on the specified ApplicationSettingsId
     *
     * @return
     */
     public JsonElement getApplicationsBasedOnApplicationSettingsId(Object appSettingsId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appSettingsId",new Gson().toJson(appSettingsId));
          gs_json_object_data.method = "getApplicationsBasedOnApplicationSettingsId";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get all applications from the applicationPool.
     * based on the specified ApplicationSettingsId
     *
     * @return
     */
     public JsonElement getApplicationsByPageAreaAndSettingsId(Object appSettingsId, Object pageArea)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appSettingsId",new Gson().toJson(appSettingsId));
          gs_json_object_data.args.put("pageArea",new Gson().toJson(pageArea));
          gs_json_object_data.method = "getApplicationsByPageAreaAndSettingsId";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Fetch all application from the applicationPool (added applications) which has a given type.
     * @param type
     * @return
     * @throws ErrorException
     */
     public JsonElement getApplicationsByType(Object type)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("type",new Gson().toJson(type));
          gs_json_object_data.method = "getApplicationsByType";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get all applications that is needed to render a page.
     *
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getApplicationsForPage(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getApplicationsForPage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getCell(Object pageId, Object cellId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.method = "getCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getComments(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getComments";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getLeftSideBarNames()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getLeftSideBarNames";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Change the userlevel for a given page. Make it accessible for only administrators / editors / customers.<br>
     * Everyone with a higher userlevel will allways gain access to the userlevels below.
     * @param pageId The id of the page to change.
     * @param userLevel The userlevel to set ADMINISTRATOR = 100, EDITOR = 50, CUSTOMER = 10
     * @return
     * @throws ErrorException
     */
     public JsonElement getLooseCell(Object pageId, Object cellId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.method = "getLooseCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all pages that a application instance is connected to..
     *
     * @param appIds A list of application ids to resolve pages for.
     * @throws ErrorException
     */
     public JsonElement getMobileBody(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "getMobileBody";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getMobileLink()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getMobileLink";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getModalNames()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getModalNames";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public JsonElement getModules()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "getModules";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * fetch an existing page.
     * @param id The id for the page to fetch.
     * @return
     * @throws ErrorException
     */
     public JsonElement getPage(Object id)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("id",new Gson().toJson(id));
          gs_json_object_data.method = "getPage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all pages that a application instance is connected to..
     *
     * @param appIds A list of application ids to resolve pages for.
     * @throws ErrorException
     */
     public JsonElement getPagesForApplication(Object appId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appId",new Gson().toJson(appId));
          gs_json_object_data.method = "getPagesForApplication";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Get secured settings
     */
     public JsonElement getSecuredSettings(Object applicationInstanceId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          gs_json_object_data.method = "getSecuredSettings";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Change the userlevel for a given page. Make it accessible for only administrators / editors / customers.<br>
     * Everyone with a higher userlevel will allways gain access to the userlevels below.
     * @param pageId The id of the page to change.
     * @param userLevel The userlevel to set ADMINISTRATOR = 100, EDITOR = 50, CUSTOMER = 10
     * @return
     * @throws ErrorException
     */
     public JsonElement getSecuredSettingsInternal(Object appName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("appName",new Gson().toJson(appName));
          gs_json_object_data.method = "getSecuredSettingsInternal";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException
     */
     public JsonElement hasAccessToModule(Object moduleName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("moduleName",new Gson().toJson(moduleName));
          gs_json_object_data.method = "hasAccessToModule";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException
     */
     public void linkPageCell(Object pageId, Object cellId, Object link)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("link",new Gson().toJson(link));
          gs_json_object_data.method = "linkPageCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Move a cell either up or down.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void moveCell(Object pageId, Object cellId, Object up)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("up",new Gson().toJson(up));
          gs_json_object_data.method = "moveCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void moveCellMobile(Object pageId, Object cellId, Object moveUp)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("moveUp",new Gson().toJson(moveUp));
          gs_json_object_data.method = "moveCellMobile";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Remove an application
     *
     * @param pageAreaId The id of the page area to remove.
     * @return
     * @throws ErrorException
     */
     public JsonElement removeAppFromCell(Object pageId, Object cellid)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellid",new Gson().toJson(cellid));
          gs_json_object_data.method = "removeAppFromCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Find all pages that a application instance is connected to..
     *
     * @param appIds A list of application ids to resolve pages for.
     * @throws ErrorException
     */
     public void resetMobileLayout(Object pageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.method = "resetMobileLayout";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add an cell to an specific earea.
     * @param pageId
     * @param incell
     * @param beforecell
     * @param direction
     * @param area header/footer/body if nothing set it will default to body.
     * @return
     * @throws ErrorException
     */
     public void restoreLayout(Object pageId, Object fromTime)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("fromTime",new Gson().toJson(fromTime));
          gs_json_object_data.method = "restoreLayout";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * For each instance of the application, there is an configuration object attached.<br>
     * Modify this object to set an application sticky, inheritable etc.
     * @param config The appconfiguration object to update / save.
     * @throws ErrorException
     */
     public void saveApplicationConfiguration(Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "saveApplicationConfiguration";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void saveCell(Object pageId, Object cell)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cell",new Gson().toJson(cell));
          gs_json_object_data.method = "saveCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void saveCellPosition(Object pageId, Object cellId, Object data)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("data",new Gson().toJson(data));
          gs_json_object_data.method = "saveCellPosition";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void saveMobileLink(Object link)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("link",new Gson().toJson(link));
          gs_json_object_data.method = "saveMobileLink";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Save the page
     */
     public void savePage(Object page)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("page",new Gson().toJson(page));
          gs_json_object_data.method = "savePage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set Group access to cell.
     *
     * @param pageId
     * @param cellId
     * @param settings
     */
     public void savePageCellGroupAccess(Object pageId, Object cellId, Object groupAccess)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("groupAccess",new Gson().toJson(groupAccess));
          gs_json_object_data.method = "savePageCellGroupAccess";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public void savePageCellSettings(Object pageId, Object cellId, Object settings)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("settings",new Gson().toJson(settings));
          gs_json_object_data.method = "savePageCellSettings";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void setCarouselConfig(Object pageId, Object cellId, Object config)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("config",new Gson().toJson(config));
          gs_json_object_data.method = "setCarouselConfig";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void setCellMode(Object pageId, Object cellId, Object mode)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("mode",new Gson().toJson(mode));
          gs_json_object_data.method = "setCellMode";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void setCellName(Object pageId, Object cellId, Object cellName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("cellName",new Gson().toJson(cellName));
          gs_json_object_data.method = "setCellName";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the page description.
     * @param description The description to add.
     * @param pageId The id of the page.
     * @throws ErrorException
     */
     public void setPageDescription(Object pageId, Object description)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("description",new Gson().toJson(description));
          gs_json_object_data.method = "setPageDescription";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Update a page and give it a parent page. <br>
     * This is used to figure out a hiarcy for the menues.<br>
     * @param pageId The page to have a parent page.
     * @param parentPageId The id of the page to be set as the parent page.
     * @throws ErrorException
     */
     public void setParentPage(Object pageId, Object parentPageId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("parentPageId",new Gson().toJson(parentPageId));
          gs_json_object_data.method = "setParentPage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public void setStylesOnCell(Object pageId, Object cellId, Object styles, Object innerStyles, Object width)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("styles",new Gson().toJson(styles));
          gs_json_object_data.args.put("innerStyles",new Gson().toJson(innerStyles));
          gs_json_object_data.args.put("width",new Gson().toJson(width));
          gs_json_object_data.method = "setStylesOnCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void setWidth(Object pageId, Object cellId, Object outerWidth, Object outerWidthWithMargins)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("outerWidth",new Gson().toJson(outerWidth));
          gs_json_object_data.args.put("outerWidthWithMargins",new Gson().toJson(outerWidthWithMargins));
          gs_json_object_data.method = "setWidth";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void startLoadPage()  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.method = "startLoadPage";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public void swapAppWithCell(Object pageId, Object fromCellId, Object toCellId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("fromCellId",new Gson().toJson(fromCellId));
          gs_json_object_data.args.put("toCellId",new Gson().toJson(toCellId));
          gs_json_object_data.method = "swapAppWithCell";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void toggleHiddenOnMobile(Object pageId, Object cellId, Object hide)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.args.put("hide",new Gson().toJson(hide));
          gs_json_object_data.method = "toggleHiddenOnMobile";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void toggleLeftSideBar(Object pageId, Object columnName)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("columnName",new Gson().toJson(columnName));
          gs_json_object_data.method = "toggleLeftSideBar";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void togglePinArea(Object pageId, Object cellId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.method = "togglePinArea";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

     /**
     * Need to translate a set of page ids?
     * @param pages A list (array) of page ids to translate.
     * @return A list of human readable strings, the key is the page id.
     * @throws ErrorException
     */
     public JsonElement translatePages(Object pages)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("pages",new Gson().toJson(pages));
          gs_json_object_data.method = "translatePages";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          JsonElement object = gson.fromJson(result, JsonElement.class);
          return object;
     }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public void updateCellLayout(Object layout, Object pageId, Object cellId)  throws Exception  {
          JsonObject2 gs_json_object_data = new JsonObject2();
          gs_json_object_data.args = new LinkedHashMap();
          gs_json_object_data.args.put("layout",new Gson().toJson(layout));
          gs_json_object_data.args.put("pageId",new Gson().toJson(pageId));
          gs_json_object_data.args.put("cellId",new Gson().toJson(cellId));
          gs_json_object_data.method = "updateCellLayout";
          gs_json_object_data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(gs_json_object_data);
     }

}
