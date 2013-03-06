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
import com.thundashop.core.productmanager.ProductManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        applicationPool.initialize(credentials, storeId);
        pagePool.initialize(credentials, storeId);
    }
    
    @Override
    public void dataFromDatabase(DataRetreived dataRetreived) {
        for (DataCommon data : dataRetreived.data) {
            if (data instanceof Page)
                pagePool.addFromDatabase((Page)data);
            
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
    public AppConfiguration addApplicationToPage(String pageId, String applicationName, String type) throws ErrorException {
        return pagePool.addApplicationToPage(pageId, applicationName, type);
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
    public List<AppConfiguration> getApplications() {
        List<AppConfiguration> result = new ArrayList(applicationPool.getApplications().values());
        return result;
    }

    @Override
    public AppConfiguration addApplication(String appName) throws ErrorException {
        return applicationPool.createNewApplication(appName);
    }

    @Override
    public AppConfiguration addApplicationBySettingsId(String id) throws ErrorException {
        AppManager manager = getManager(AppManager.class);
        AppConfiguration res = applicationPool.createNewApplication(manager.getApplication(id).appName);
        res.appSettingsId = id;
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
    public void deleteApplication(String id) throws ErrorException {
        applicationPool.deleteApplication(id);
        pagePool.removeApplication(storeId);
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
    public AppConfiguration addApplicationToPageBySettingsId(String pageId, String settingsId, String pageArea) throws ErrorException {
        AppManager app = getManager(AppManager.class);
        ApplicationSettings settings = app.getApplication(settingsId);
        return pagePool.addApplicationToPage(pageId, settings.appName, pageArea, settingsId);
    }
}