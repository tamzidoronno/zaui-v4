package com.thundashop.core.pagemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.pagemanager.data.CarouselConfig;
import com.thundashop.core.pagemanager.data.FloatingData;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageCell;
import com.thundashop.core.pagemanager.data.PageLayout;
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
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Page createPage() throws ErrorException;
    
    /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public ApplicationInstance addApplication(String applicationId, String pageCellId);
    
    /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public void updateCellLayout(List<Integer> layout, String pageId, String cellId);
    
    /**
     * Add application
     * @param id
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public void setStylesOnCell(String pageId, String cellId, String styles, String innerStyles, Double width);
    
    /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException 
     */
    @Administrator
    public void clearPage(String pageId) throws ErrorException;
    
    /**
     * Remove all content on all page areas for this page.
     * @param pageId
     * @throws ErrorException 
     */
    @Administrator
    public void linkPageCell(String pageId, String cellId, String link) throws ErrorException;
    
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
    @Administrator
    public String addLayoutCell(String pageId, String incell, String beforecell, String direction, String area) throws ErrorException;
    
    @Administrator
    public Page dropCell(String pageId, String cellId) throws ErrorException;
    
    @Administrator
    public void createHeaderFooter(String type) throws ErrorException;
    
    /**
     * Set the page description.
     * @param description The description to add.
     * @param pageId The id of the page.
     * @throws ErrorException 
     */
    public void setPageDescription(String pageId, String description) throws ErrorException;
    
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
    public void saveApplicationConfiguration(ApplicationInstance config) throws ErrorException;
    
    /**
     * Remove an application
     * 
     * @param pageAreaId The id of the page area to remove.
     * @return
     * @throws ErrorException 
     */
    @Editor
    public Page removeAppFromCell(String pageId, String cellid) throws ErrorException;
    
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
    
    @Internal
    public HashMap<String, Setting> getSecuredSettingsInternal(String appName);
    
    /**
     * Get all applications from the applicationPool.
     * 
     * @return 
     */
    public List<ApplicationInstance> getApplications() throws ErrorException;
    
    
    /**
     * Fetch all application from the applicationPool (added applications) which has a given type.
     * @param type
     * @return
     * @throws ErrorException 
     */
    public List<ApplicationInstance> getApplicationsByType(String type) throws ErrorException;
    
    
    /**
     * Get all applications from the applicationPool. 
     * based on the specified ApplicationSettingsId
     * 
     * @return 
     */
    public List<ApplicationInstance> getApplicationsBasedOnApplicationSettingsId(String appSettingsId) throws ErrorException;

    /**
     * Get all applications from the applicationPool. 
     * based on the specified ApplicationSettingsId
     * 
     * @return 
     */
    public List<ApplicationInstance> getApplicationsByPageAreaAndSettingsId(String appSettingsId, String pageArea) throws ErrorException;

    
    /**
     * Get all applications that is needed to render a page.
     * 
     * @param pageId
     * @return
     * @throws ErrorException 
     */
    public List<ApplicationInstance> getApplicationsForPage(String pageId) throws ErrorException;
    
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
     * Create a new row to add application areas to for a given page.
     * @param pageId
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public String createNewRow(String pageId) throws ErrorException;
    
    /**
     * Move a cell either up or down.
     * @param pageId
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public void moveCell(String pageId, String cellId, boolean up) throws ErrorException;
    
    /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public void setCarouselConfig(String pageId, String cellId, CarouselConfig config) throws ErrorException;
    
    /**
     * Set the carousel configuration.
     * @param pageId
     * @return
     * @throws ErrorException 
     */
    @Administrator
    public void setCellName(String pageId, String cellId, String cellName) throws ErrorException;
    
    @Administrator
    public void setCellMode(String pageId, String cellId, String mode) throws ErrorException;
    
    @Administrator
    public void saveCellPosition(String pageId, String cellId, FloatingData data) throws ErrorException;
    
    @Administrator
    public void moveCellMobile(String pageId, String cellId, Boolean moveUp) throws ErrorException;
    
    @Administrator
    public void toggleHiddenOnMobile(String pageId, String cellId, Boolean hide) throws ErrorException;
    
    @Administrator
    public void togglePinArea(String pageId, String cellId) throws ErrorException;
    
    @Administrator
    public void setWidth(String pageId, String cellId, Integer outerWidth, Integer outerWidthWithMargins) throws ErrorException;
    
    @Administrator
    public PageCell getCell(String pageId, String cellId) throws ErrorException;
}
