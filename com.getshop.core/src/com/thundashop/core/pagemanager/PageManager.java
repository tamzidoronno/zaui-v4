/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.productmanager.ProductManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class PageManager extends ManagerBase implements IPageManager {
    
    @Autowired
    public ApplicationPoolImpl applicationPool;

    @Autowired
    public PagePoolImpl pagePool;
    
    @Autowired
    public PageManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        credentials = new Credentials(getClass());
        credentials.manangerName = this.getClass().getSimpleName();
    }
    
    @Override
    public void onReady() {
        pagePool.pageManager = this;
        pagePool.setApplicationPool(applicationPool);
        applicationPool.setPageManager(this);
        applicationPool.initialize(credentials, storeId);
        pagePool.initialize(credentials, storeId);
    }

    @Override
    public void dataFromDatabase(DataRetreived dataRetreived) {
        for (DataCommon data : dataRetreived.data) {
            if (data instanceof Page) {
                Page p = (Page) data;
                pagePool.addFromDatabase(p);
            }
            
            if (data instanceof AppConfiguration)
                applicationPool.addFromDatabase((AppConfiguration)data);
        }
    }

    @Override
    public Page getPage(String id) throws ErrorException {
        return pagePool.get(id);
    }

    @Override
    public void changePageLayout(String pageId, int layout) throws ErrorException {
        pagePool.changeLayout(pageId, layout);
    }

    @Override
    public Page createPage(int layout, String parentId) throws ErrorException {
        return pagePool.createNewPage(layout, parentId);
    }
    
    @Override
    public Page removeApplication(String applicationId, String pageid) throws ErrorException {
        Page page = pagePool.get(pageid);
        return pagePool.removeApplicationFromPage(page, applicationId);
    }

    @Override
    public Page reorderApplication(String pageId, String appId, Boolean moveUp) throws ErrorException {
        return pagePool.reOrderApplication(appId, pageId, moveUp);
    }

    @Override
    public void setApplicationSettings(Settings settings) throws ErrorException {
        applicationPool.saveSettings(settings);
    }

    @Override
    public void setApplicationSticky(String appId, int toggle) throws ErrorException {
        applicationPool.stickApplication(appId, toggle);
    }

    @Override
    public Page changePageUserLevel(String pageId, int userLevel) throws ErrorException {
        return pagePool.changePageUserLevel(pageId, userLevel);
    }

    @Override
    public HashMap<String, Setting> getApplicationSettings(String name) throws ErrorException {
        AppConfiguration app = applicationPool.getByName(name);
        if (app != null)
            return app.settings;
        
        return null;
    }

    @Override
    public List<AppConfiguration> getApplications() throws ErrorException {
        List<AppConfiguration> result = new ArrayList(applicationPool.getApplications().values());
        return result;
    }
    
    @Override
    public List<AppConfiguration> getApplicationsForPage(String pageId) throws ErrorException {
        Page page = pagePool.get(pageId);
        List<AppConfiguration> result = new ArrayList(page.getApplications().values());
        result.addAll(applicationPool.getThemeApplications());
        return result;
    }

    @Override
    public AppConfiguration addApplication(String applicationSettingId) throws ErrorException {
        AppConfiguration res = applicationPool.createNewApplication(applicationSettingId);
        applicationPool.setThemeSelectedToStoreConfiguration(res);
        databaseSaver.saveObject(res, credentials);
        return res;
    }

    @Override
    public void saveApplicationConfiguration(AppConfiguration config) throws ErrorException {
        applicationPool.saveApplicationConfiguration(config);
    }
    
    @Override
    public HashMap<String, Setting> getSecuredSettings(String appName) {
        AppConfiguration config = applicationPool.getSecured(appName);
        if (config != null)
            return config.settings;
        
        return new HashMap();
    }

    @Override
    public void deleteApplication(String instanceId) throws ErrorException {
        applicationPool.deleteApplication(instanceId);
        pagePool.removeApplication(instanceId);
    }

    @Override
    public Page createPageWithId(int layout, String parentId, String id) throws ErrorException {
        return pagePool.createNewPage(layout, parentId, id);
    }

    @Override
    public void deletePage(String id) throws ErrorException {
        pagePool.deletePage(id);
    }

    @Override
    public void addExistingApplicationToPageArea(String pageId, String appId, String area) throws ErrorException {
        pagePool.addExistingApplicationToArea(pageId, appId, area);
    }

    @Override
    public void setParentPage(String pageId, String parentPageId) throws ErrorException {
        Page page = pagePool.get(pageId);
        Page toBeParent = pagePool.get(parentPageId);
        
        page.parent = toBeParent;
        page.type = toBeParent.type;
        pagePool.savePage(page);
        
    }

    @Override
    public HashMap<String, List<String>> getPagesForApplications(List<String> appIds) throws ErrorException {
        if(appIds == null) {
            throw new ErrorException(1000012);
        }
        
        
        if(appIds.size() == 0) {
            throw new ErrorException(1000012);
        }
        
        HashMap<String, List<String>> retval = new HashMap();
        for(String appId : appIds) {
            List<String> pages = findPagesForApplication(appId);
            retval.put(appId, pages);
        }
        
        return retval;
    }

    private List<String> findPagesForApplication(String appId) throws ErrorException {
        List<String> pages = new ArrayList();
        for(Page page : pagePool.pages.values()) {
            Page finalizedPage = pagePool.get(page.id);
            HashMap<String, AppConfiguration> apps = finalizedPage.getApplications();
            for(String appIdOnPage : apps.keySet()) {
                if(appIdOnPage.equals(appId)) {
                    pages.add(page.id);
                    continue;
                }
            }
        }
        
        return pages;
    }

    @Override
    public HashMap<String, String> translatePages(List<String> pages) throws ErrorException {
        if(pages == null) {
            throw new ErrorException(1000013);
        }
        
        ListManager listManager = getManager(ListManager.class);
        ProductManager prodManager = getManager(ProductManager.class);
        
        HashMap<String, String> translated = listManager.translateEntries(pages);
        HashMap<String, String> translatedByProduct = prodManager.translateEntries(pages);
        
        HashMap<String, String> result = new HashMap();
        
        for(String pageId : pages) {
            String translationProduct = translatedByProduct.get(pageId);
            String translation = translated.get(pageId);
            
            if(translation != null && translation.trim().length() > 0) {
                result.put(pageId, translation);
            }
            
            if(translationProduct != null && translationProduct.trim().length() > 0) {
                result.put(pageId, translationProduct);
            }
        }
        
        return result;
    }

    @Override
    public AppConfiguration addApplicationToPage(String pageId, String applicationSettingId, String pageArea) throws ErrorException {
        return pagePool.addApplicationToPage(pageId, pageArea, applicationSettingId);
    }

    @Override
    public void swapApplication(String fromAppId, String toAppId) throws ErrorException {
        AppManager appman = getManager(AppManager.class);
        PageManager manager = getManager(PageManager.class);
        ApplicationSettings toApp = appman.getApplication(toAppId);
        
        ApplicationPoolImpl pool = manager.applicationPool;
        Map<String, AppConfiguration> allAddedApplications = pool.getApplications();
        for(String instanceId : allAddedApplications.keySet()) {
            AppConfiguration config = allAddedApplications.get(instanceId);
            if(config.appSettingsId != null && config.appSettingsId.equals(fromAppId)) {
                config.appSettingsId = toApp.id;
                pool.saveApplicationConfiguration(config);
            }
        }
    }

    @Override
    public void clearPageArea(String pageId, String pageAreaName) throws ErrorException {
        Page page = pagePool.get(pageId);
        PageArea pageArea = page.getPageArea(pageAreaName);
        
        if(pageArea == null) {
            return;
        }
        
        List<String> apps = new ArrayList();
        for (AppConfiguration app : pageArea.applications.values()) {
            apps.add(app.id);
        }
        
        for (String appid : apps) {
            removeApplication(appid, pageId);
        }
    }

    @Override
    public List<AppConfiguration> getApplicationsBasedOnApplicationSettingsId(String appSettingsId) throws ErrorException {
        return applicationPool.getApplications(appSettingsId);
    }

    @Override
    public void removeAllApplications(String appSettingsId) throws ErrorException {
        List<AppConfiguration> allApps = this.getApplications();
        
        for(AppConfiguration config : allApps) {
            if(config.appSettingsId != null && config.appSettingsId.equals(appSettingsId)) {
                deleteApplication(config.id);
            }
        }
        throwEvent(Events.ALL_APPS_REMOVED, appSettingsId);
    }

    @Override
    public List<AppConfiguration> getApplicationsByPageAreaAndSettingsId(String appSettingsId, String pageArea) throws ErrorException {
        List<AppConfiguration> applications = getApplicationsBasedOnApplicationSettingsId(appSettingsId);
        List<AppConfiguration> returnList = new ArrayList();
        for (AppConfiguration appConfig : applications) {
            if (pagePool.applicationExistsInArea(appConfig, pageArea)) {
                returnList.add(appConfig.secureClone());
            }
        }
        return returnList;
    }

    @Override
    public void setPageDescription(String pageId, String description) throws ErrorException {
        Page page = pagePool.get(pageId);
        page.description = description;
        pagePool.savePage(page);
    }

    @Override
    public List<AppConfiguration> getApplicationsByType(String type) throws ErrorException {
        AppManager manager = getManager(AppManager.class);
        List<AppConfiguration> apps = getApplications();
        List<AppConfiguration> toReturn = new ArrayList();
        for(AppConfiguration config : apps) {
            try {
                ApplicationSettings settings = manager.getApplication(config.appSettingsId);
                if(settings.type.equals(type)) {
                    toReturn.add(config);
                }
            }catch(ErrorException e) {
                //If the application does not exists, the ignore it.
                if(e.code != 18) {
                    throw e;
                }
            }
        }
        
        return toReturn;
    }

}