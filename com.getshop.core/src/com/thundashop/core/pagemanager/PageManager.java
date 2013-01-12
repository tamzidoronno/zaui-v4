/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.Page;
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
        credentials = new Credentials();
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
        return new ArrayList(applicationPool.getApplications().values());
    }

    @Override
    public AppConfiguration addApplication(String appName) throws ErrorException {
        return applicationPool.createNewApplication(appName);
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
}