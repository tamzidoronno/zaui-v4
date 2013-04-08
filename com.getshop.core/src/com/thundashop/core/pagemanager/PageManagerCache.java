package com.thundashop.core.pagemanager;

import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Setting;
import com.thundashop.core.common.Settings;
import com.thundashop.core.pagemanager.data.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class PageManagerCache implements IPageManager {
    PageManager manager;
    String addr;
    
    public PageManagerCache(PageManager manager, String addr) {
        this.manager = manager;
        this.addr = addr;
    }
    
    @Override
    public Page createPage(int layout, String parentId) throws ErrorException {
        return null;
    }

    @Override
    public Page getPage(String id) throws ErrorException {
        Page result = manager.getPage(id);
        if(result.userLevel > 0) {
            return result;
        }
        
        CachingKey key = new CachingKey();
        
        LinkedHashMap<String,Object> keys = new LinkedHashMap();
        keys.put("id", id);
        key.args = keys;
        key.interfaceName = this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
        key.sessionId = "";
        key.method = "getPage";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        return null;
    }

    @Override
    public void saveApplicationConfiguration(AppConfiguration config) throws ErrorException {
        HashMap<CachingKey, Object> object = manager.getCacheManager().getAllCachedObjects(manager.storeId);
        
    }

    @Override
    public Page removeApplication(String applicationId, String pageid) throws ErrorException {
        getApplications();
        getPage(pageid);
        return null;
    }

    @Override
    public Page reorderApplication(String pageId, String appId, Boolean moveUp) throws ErrorException {
        getPage(pageId);
        return null;
    }

    @Override
    public void setApplicationSettings(Settings settings) throws ErrorException {
        getApplications();
    }

    @Override
    public void setApplicationSticky(String appId, int toggle) throws ErrorException {
        getApplications();
    }

    @Override
    public Page changePageUserLevel(String pageId, int userLevel) throws ErrorException {
        getPage(pageId);
        return null;
    }

    @Override
    public void changePageLayout(String pageId, int layout) throws ErrorException {
        getPage(pageId);
    }

    @Override
    public HashMap<String, Setting> getApplicationSettings(String name) throws ErrorException {
        return null;
    }

    @Override
    public HashMap<String, Setting> getSecuredSettings(String appName) {
        return null;
    }

    @Override
    public List<AppConfiguration> getApplications() throws ErrorException {
        List<AppConfiguration> result = manager.getApplications();
        
        ArrayList<Object> keys = new ArrayList();
        CachingKey key = new CachingKey();
        key.args = keys;
        key.interfaceName = this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
        key.sessionId = "";
        key.method = "getApplications";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        return null;
    }

    @Override
    public void deleteApplication(String id) throws ErrorException {
        getApplications();
        for (CachingKey key : manager.getCacheManager().getAllCachedObjects(manager.storeId).keySet()) {
            String interfaceName = this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
            if (key.method.equals("getPage") && interfaceName.equals(key.interfaceName)) {
                manager.getCacheManager().removeFromCache(key, manager.storeId, addr);
            }
        }
    }

    @Override
    public Page createPageWithId(int layout, String parentId, String id) {
        return null;
    }

    @Override
    public void deletePage(String id) throws ErrorException {
    }

    @Override
    public void addExistingApplicationToPageArea(String pageId, String appId, String area) throws ErrorException {
        getApplications();
        getPage(pageId);
    }

    @Override
    public void setParentPage(String pageId, String parentPageId) throws ErrorException {
        getPage(pageId);
    }

    @Override
    public HashMap<String, List<String>> getPagesForApplications(List<String> appIds) throws ErrorException {
        return null;
    }

    @Override
    public HashMap<String, String> translatePages(List<String> pages) throws ErrorException {
        return null;
    }

    @Override
    public AppConfiguration addApplicationToPage(String pageId, String applicationSettingId, String pageArea) throws ErrorException {
        getApplications();
        getPage(pageId);
        return null;
    }

    @Override
    public AppConfiguration addApplication(String applicationSettingId) throws ErrorException {
        getApplications();
        return null;
    }

    @Override
    public void swapApplication(String fromAppId, String toAppId) throws ErrorException {
        getApplications();
        for (CachingKey key : manager.getCacheManager().getAllCachedObjects(manager.storeId).keySet()) {
            String interfaceName = this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
            if (key.method.equals("getPage") && interfaceName.equals(key.interfaceName)) {
                manager.getCacheManager().removeFromCache(key, manager.storeId, addr);
            }
        }
    }

    @Override
    public void clearPageArea(String pageId, String pageArea) throws ErrorException {
        getApplications();
        getPage(pageId);
    }

}
