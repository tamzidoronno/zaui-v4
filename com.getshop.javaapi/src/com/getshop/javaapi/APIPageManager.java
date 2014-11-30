package com.getshop.javaapi;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashMap;
import com.thundashop.core.common.JsonObject2;
public class APIPageManager {

      public Transporter transport;

      public APIPageManager(Transporter transport){
           this.transport = transport;
      }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.common.ApplicationInstance addApplication(java.lang.String applicationId, java.lang.String pageCellId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("applicationId",new Gson().toJson(applicationId));
          data.args.put("pageCellId",new Gson().toJson(pageCellId));
          data.method = "addApplication";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.common.ApplicationInstance>() {}.getType();
          com.thundashop.core.common.ApplicationInstance object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Add an existing application to the application area
     *
     * @param pageId
     * @param appId
     * @param area
     * @throws ErrorException
     */
     public void addExistingApplicationToPageArea(java.lang.String pageId, java.lang.String appId, java.lang.String area)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("appId",new Gson().toJson(appId));
          data.args.put("area",new Gson().toJson(area));
          data.method = "addExistingApplicationToPageArea";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
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
     public java.lang.String addLayoutCell(java.lang.String pageId, java.lang.String incell, java.lang.String beforecell, java.lang.String direction, java.lang.String area)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("incell",new Gson().toJson(incell));
          data.args.put("beforecell",new Gson().toJson(beforecell));
          data.args.put("direction",new Gson().toJson(direction));
          data.args.put("area",new Gson().toJson(area));
          data.method = "addLayoutCell";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
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
     public com.thundashop.core.pagemanager.data.Page changePageUserLevel(java.lang.String pageId, int userLevel)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("userLevel",new Gson().toJson(userLevel));
          data.method = "changePageUserLevel";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.pagemanager.data.Page>() {}.getType();
          com.thundashop.core.pagemanager.data.Page object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException
     */
     public void clearPage(java.lang.String pageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.method = "clearPage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Remove all applications for specified page area at specified page.
     *
     * @param pageId
     * @throws ErrorException
     */
     public void clearPageArea(java.lang.String pageId, java.lang.String pageArea)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("pageArea",new Gson().toJson(pageArea));
          data.method = "clearPageArea";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Create a new row to add application areas to for a given page.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public java.lang.String createNewRow(java.lang.String pageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.method = "createNewRow";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.lang.String>() {}.getType();
          java.lang.String object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Create a new page.
     * This page can be used to stick applications to it.
     *
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.pagemanager.data.Page createPage()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "createPage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.pagemanager.data.Page>() {}.getType();
          com.thundashop.core.pagemanager.data.Page object = gson.fromJson(result, typeJson_3323322222_autogenerated);
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
     public void deleteApplication(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "deleteApplication";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Delete the page with the id.
     *
     * @param id
     */
     public void deletePage(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "deletePage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
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
     public com.thundashop.core.pagemanager.data.Page dropCell(java.lang.String pageId, java.lang.String cellId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellId",new Gson().toJson(cellId));
          data.method = "dropCell";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.pagemanager.data.Page>() {}.getType();
          com.thundashop.core.pagemanager.data.Page object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all applications from the applicationPool.
     *
     * @return
     */
     public java.util.List getApplications()  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.method = "getApplications";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.common.ApplicationInstance>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all applications from the applicationPool.
     * based on the specified ApplicationSettingsId
     *
     * @return
     */
     public java.util.List getApplicationsBasedOnApplicationSettingsId(java.lang.String appSettingsId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appSettingsId",new Gson().toJson(appSettingsId));
          data.method = "getApplicationsBasedOnApplicationSettingsId";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.common.ApplicationInstance>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all applications from the applicationPool.
     * based on the specified ApplicationSettingsId
     *
     * @return
     */
     public java.util.List getApplicationsByPageAreaAndSettingsId(java.lang.String appSettingsId, java.lang.String pageArea)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appSettingsId",new Gson().toJson(appSettingsId));
          data.args.put("pageArea",new Gson().toJson(pageArea));
          data.method = "getApplicationsByPageAreaAndSettingsId";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.common.ApplicationInstance>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch all application from the applicationPool (added applications) which has a given type.
     * @param type
     * @return
     * @throws ErrorException
     */
     public java.util.List getApplicationsByType(java.lang.String type)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("type",new Gson().toJson(type));
          data.method = "getApplicationsByType";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.common.ApplicationInstance>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get all applications that is needed to render a page.
     *
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public java.util.List getApplicationsForPage(java.lang.String pageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.method = "getApplicationsForPage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.List<com.thundashop.core.common.ApplicationInstance>>() {}.getType();
          java.util.List object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * fetch an existing page.
     * @param id The id for the page to fetch.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.pagemanager.data.Page getPage(java.lang.String id)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("id",new Gson().toJson(id));
          data.method = "getPage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.pagemanager.data.Page>() {}.getType();
          com.thundashop.core.pagemanager.data.Page object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Fetch a list of all pages found for a list of applications.<br>
     * The key is the application id, the list combined with the key a list of page ids found for the specified applications.
     * @param appIds A list of application ids to resolve pages for.
     * @throws ErrorException
     */
     public java.util.HashMap getPagesForApplications(java.util.List appIds)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appIds",new Gson().toJson(appIds));
          data.method = "getPagesForApplications";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,java.util.List<java.lang.String>>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Get secured settings
     */
     public java.util.HashMap getSecuredSettings(java.lang.String applicationInstanceId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("applicationInstanceId",new Gson().toJson(applicationInstanceId));
          data.method = "getSecuredSettings";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,com.thundashop.core.common.Setting>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
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
     public java.util.HashMap getSecuredSettingsInternal(java.lang.String appName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("appName",new Gson().toJson(appName));
          data.method = "getSecuredSettingsInternal";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,com.thundashop.core.common.Setting>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * Move a cell either up or down.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void moveCell(java.lang.String pageId, java.lang.String cellId, boolean up)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellId",new Gson().toJson(cellId));
          data.args.put("up",new Gson().toJson(up));
          data.method = "moveCell";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Remove an application
     *
     * @param pageAreaId The id of the page area to remove.
     * @return
     * @throws ErrorException
     */
     public com.thundashop.core.pagemanager.data.Page removeAppFromCell(java.lang.String pageId, java.lang.String cellid)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellid",new Gson().toJson(cellid));
          data.method = "removeAppFromCell";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<com.thundashop.core.pagemanager.data.Page>() {}.getType();
          com.thundashop.core.pagemanager.data.Page object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

     /**
     * For each instance of the application, there is an configuration object attached.<br>
     * Modify this object to set an application sticky, inheritable etc.
     * @param config The appconfiguration object to update / save.
     * @throws ErrorException
     */
     public void saveApplicationConfiguration(com.thundashop.core.common.ApplicationInstance config)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("config",new Gson().toJson(config));
          data.method = "saveApplicationConfiguration";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Save the page
     */
     public void savePage(com.thundashop.core.pagemanager.data.Page page)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("page",new Gson().toJson(page));
          data.method = "savePage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
     public void setCarouselConfig(java.lang.String pageId, java.lang.String cellId, com.thundashop.core.pagemanager.data.CarouselConfig config)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellId",new Gson().toJson(cellId));
          data.args.put("config",new Gson().toJson(config));
          data.method = "setCarouselConfig";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
<<<<<<< HEAD
=======
     public void setCellMode(java.lang.String pageId, java.lang.String cellId, java.lang.String mode)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellId",new Gson().toJson(cellId));
          data.args.put("mode",new Gson().toJson(mode));
          data.method = "setCellMode";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException
     */
>>>>>>> ffb271abea94719c7aa1b424de77856b58789f58
     public void setCellName(java.lang.String pageId, java.lang.String cellId, java.lang.String cellName)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellId",new Gson().toJson(cellId));
          data.args.put("cellName",new Gson().toJson(cellName));
          data.method = "setCellName";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Set the page description.
     * @param description The description to add.
     * @param pageId The id of the page.
     * @throws ErrorException
     */
     public void setPageDescription(java.lang.String pageId, java.lang.String description)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("description",new Gson().toJson(description));
          data.method = "setPageDescription";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Update a page and give it a parent page. <br>
     * This is used to figure out a hiarcy for the menues.<br>
     * @param pageId The page to have a parent page.
     * @param parentPageId The id of the page to be set as the parent page.
     * @throws ErrorException
     */
     public void setParentPage(java.lang.String pageId, java.lang.String parentPageId)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("parentPageId",new Gson().toJson(parentPageId));
          data.method = "setParentPage";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException
     */
     public void setStylesOnCell(java.lang.String pageId, java.lang.String cellId, java.lang.String styles, java.lang.String innerStyles, java.lang.Double width)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pageId",new Gson().toJson(pageId));
          data.args.put("cellId",new Gson().toJson(cellId));
          data.args.put("styles",new Gson().toJson(styles));
          data.args.put("innerStyles",new Gson().toJson(innerStyles));
          data.args.put("width",new Gson().toJson(width));
          data.method = "setStylesOnCell";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
     }

     /**
     * Need to translate a set of page ids?
     * @param pages A list (array) of page ids to translate.
     * @return A list of human readable strings, the key is the page id.
     * @throws ErrorException
     */
     public java.util.HashMap translatePages(java.util.List pages)  throws Exception  {
          JsonObject2 data = new JsonObject2();
          data.args = new LinkedHashMap();
          data.args.put("pages",new Gson().toJson(pages));
          data.method = "translatePages";
          data.interfaceName = "core.pagemanager.IPageManager";
          String result = transport.send(data);
          Gson gson = new GsonBuilder().serializeNulls().create();
          Type typeJson_3323322222_autogenerated = new TypeToken<java.util.HashMap<java.lang.String,java.lang.String>>() {}.getType();
          java.util.HashMap object = gson.fromJson(result, typeJson_3323322222_autogenerated);
          return object;
     }

}
