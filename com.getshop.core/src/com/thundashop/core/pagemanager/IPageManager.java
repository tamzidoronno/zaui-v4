package com.thundashop.core.pagemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.pagemanager.data.Page;
import java.util.HashMap;
import java.util.List;

/**
 * The pagemanager is the glue helding applications and pages together.<br>
 * With this manager you will be able to create new pages, add application to it<br>
 * remove application, and also build a hierarchy with pages which can be used for breadcrumbs.<br> 
 * Also settings for applications is put into the pagemanager.<br>
 */
@GetShopApi
public interface IPageManager {
    /**
     * Create a new page.
     * This page can be used to stick applications to it.
     * 
     * Layout parameters<br>
     * Header footer left middle right = 1;<br>
     * Header left middle footer = 2;<br>
     * Header right middle footer = 3;<br>
     * Header middle footer = 4;<br>
     * 
     * @param layout See above, integer 1 to 4
     * @param parentId The parent page. From what page are this page being created?
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Page createPage(int layout, String parentId) throws ErrorException;
    
    /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public AppConfiguration addApplication(String applicationSettingId) throws ErrorException;
    
    /**
     * Switch application areas between two applications areas on a given page.
     */
    @Administrator
    public void switchApplicationAreas(String pageId, String fromArea, String toArea) throws ErrorException;
    
    /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException 
     */
    @Administrator
    public void clearPage(String pageId) throws ErrorException;
    
    /**
     * Set the page description.
     * @param description The description to add.
     * @param pageId The id of the page.
     * @throws ErrorException 
     */
    public void setPageDescription(String pageId, String description) throws ErrorException;
    
    /**
     * Create a new page with the specified id.
     * For layouts available, see layouts for createPage function
     * 
     * @param id
     * @return 
     */
    @Administrator
    public Page createPageWithId(int layout, String parentId, String id)  throws ErrorException;
    
    /**
     * fetch an existing page.
     * @param id The id for the page to fetch.
     * @return
     * @throws ErrorException 
     */
    public Page getPage(String id) throws ErrorException;
    
    /**
     * Delete an application from the store
     * removes all references where it has been used.
     * 
     * Suitable for singleton applications
     * 
     * @param id
     * @throws ErrorException 
     */
    @Administrator
    public void deleteApplication(String id) throws ErrorException;
    
    /**
     * For each instance of the application, there is an configuration object attached.<br>
     * Modify this object to set an application sticky, inheritable etc.
     * @param config The appconfiguration object to update / save.
     * @throws ErrorException 
     */
    @Administrator
    public void saveApplicationConfiguration(AppConfiguration config) throws ErrorException;
    
    /**
     * If you know the id of the application you want to add, we strongly recommend to use this call.
     * This function 
     * @param pageId The id of the page to add the application to
     * @param settingsId The settings id which identify what applications is being added.
     * @param pageArea The area this application should be added to.
     * @return AppConfiguration
     * @throws ErrorException 
     */
    @Editor
    public AppConfiguration addApplicationToPage(String pageId, String applicationSettingId, String pageArea) throws ErrorException;
   
    /**
     * Remove an application
     * 
     * @param applicationId The id to the application.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Page removeApplication(String applicationId, String pageid) throws ErrorException;
    
    /**
     * Remove instances of applications added for a specific page id.
     * @param appSettingsId The id of the application row
     * @throws ErrorException 
     */
    public void removeAllApplications(String appSettingsId) throws ErrorException;
    
    /**
     * Rearrange a given application for a given page.
     * @param pageId The id of the page where the application is located.
     * @param appId The id of application id to rearrange.
     * @param moveUp If set to true the application is moved up, otherwhise it is set to false.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Page reorderApplication(String pageId, String appId, Boolean moveUp) throws ErrorException;
    
    /**
     * Set a given set of settings to a given application.
     * @param settings The settings for the application.
     * @throws ErrorException 
     */
    @Editor
    public void setApplicationSettings(Settings settings) throws ErrorException;
    
    /**
     * Stick an application. This means that the application will be visible on all the pages.<br>
     * This is especially useful for top menu application, footer applications, and other application<br>
     * that is supposed to be displayed all the time.
     * <br>
     * <br> 1 = sticked
     * <br> 0 = not sticked
     * @param appId The id of the application to stick.
     * @param toggle True makes the application sticky, false disabled the stickyness.
     * @throws ErrorException 
     */
    @Editor
    public void setApplicationSticky(String appId, int toggle) throws ErrorException;
    
    /**
     * Change the userlevel for a given page. Make it accessible for only administrators / editors / customers.<br>
     * Everyone with a higher userlevel will allways gain access to the userlevels below.
     * @param pageId The id of the page to change.
     * @param userLevel The userlevel to set ADMINISTRATOR = 100, EDITOR = 50, CUSTOMER = 10
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public Page changePageUserLevel(String pageId, int userLevel) throws ErrorException;
    
    /**
     * Change the page layout<br>
     * HeaderFooterLeftMiddleRight = 1<br>
     * HeaderLeftMiddleFooter = 2<br>
     * HeaderRightMiddleFooter = 3<br>
     * HeaderMiddleFooter = 4<br>
     * 
     * @param pageId
     * @param layout
     * @throws ErrorException 
     */
    @Administrator
    public void changePageLayout(String pageId, int layout) throws ErrorException;
    
    /**
     * Fetch all settings for a given application
     * @param name The php equivelent name of the application.
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public HashMap<String, Setting> getApplicationSettings(String name) throws ErrorException;
    
    @Internal
    public HashMap<String, Setting> getSecuredSettingsInternal(String appName);
    
    /**
     * Get all applications from the applicationPool.
     * 
     * @return 
     */
    public List<AppConfiguration> getApplications() throws ErrorException;
    
    
    /**
     * Fetch all application from the applicationPool (added applications) which has a given type.
     * @param type
     * @return
     * @throws ErrorException 
     */
    public List<AppConfiguration> getApplicationsByType(String type) throws ErrorException;
    
    
    /**
     * Get all applications from the applicationPool. 
     * based on the specified ApplicationSettingsId
     * 
     * @return 
     */
    public List<AppConfiguration> getApplicationsBasedOnApplicationSettingsId(String appSettingsId) throws ErrorException;

    /**
     * Get all applications from the applicationPool. 
     * based on the specified ApplicationSettingsId
     * 
     * @return 
     */
    public List<AppConfiguration> getApplicationsByPageAreaAndSettingsId(String appSettingsId, String pageArea) throws ErrorException;

    
    /**
     * Get all applications that is needed to render a page.
     * 
     * @param pageId
     * @return
     * @throws ErrorException 
     */
    public List<AppConfiguration> getApplicationsForPage(String pageId) throws ErrorException;
    
    /**
     * Delete the page with the id.
     * 
     * @param id 
     */
    @Administrator
    public void deletePage(String id) throws ErrorException;
    
    /**
     * Add an existing application to the application area
     * 
     * @param pageId
     * @param appId
     * @param area
     * @throws ErrorException 
     */
    @Administrator
    public void addExistingApplicationToPageArea(String pageId, String appId, String area) throws ErrorException;
    
    /**
     * Update a page and give it a parent page. <br>
     * This is used to figure out a hiarcy for the menues.<br>
     * @param pageId The page to have a parent page.
     * @param parentPageId The id of the page to be set as the parent page.
     * @throws ErrorException 
     */
    @Administrator
    public void setParentPage(String pageId, String parentPageId) throws ErrorException;
    
    
    /**
     * Fetch a list of all pages found for a list of applications.<br>
     * The key is the application id, the list combined with the key a list of page ids found for the specified applications.
     * @param appIds A list of application ids to resolve pages for.
     * @throws ErrorException 
     */
    public HashMap<String, List<String>> getPagesForApplications(List<String> appIds) throws ErrorException;
    
    /**
     * Need to translate a set of page ids?
     * @param pages A list (array) of page ids to translate.
     * @return A list of human readable strings, the key is the page id.
     * @throws ErrorException 
     */
    public HashMap<String, String> translatePages(List<String> pages) throws ErrorException;
    
    
    
    /**
     * If you clone an application, you would prefer to switch all already added applications
     * into an existing application.
     * @param fromAppId
     * @param toAppId
     * @throws ErrorException 
     */
    @Administrator
    public void swapApplication(String fromAppId, String toAppId) throws ErrorException;
    
    /**
     * Remove all applications for specified page area at specified page.
     * 
     * @param pageId
     * @throws ErrorException 
     */
    @Administrator
    public void clearPageArea(String pageId, String pageArea) throws ErrorException;

    /**
     * Save the page
     */
    @Administrator
    public void savePage(Page page) throws ErrorException;
    
    
    /**
     * Get secured settings
     */
    @Administrator
    public HashMap<String, Setting> getSecuredSettings(String applicationInstanceId);
    
    /**
     * Search for pages by name.
     */
    public List<Page> search(String search) throws ErrorException;
}
