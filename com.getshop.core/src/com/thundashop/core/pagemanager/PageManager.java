/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PageManager extends ManagerBase implements IPageManager {
    
    HashMap<String, Page> pages = new HashMap();
    
    @Override
    public Page createPage() throws ErrorException {
        Page page = new Page();
        if(pages.isEmpty()) {
            page.id = "home";
        }
        page.storeId = storeId;
        
        databaseSaver.saveObject(page, credentials);
        pages.put(page.id, page);
        return page;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if(obj instanceof Page) {
                Page page = (Page) obj;
                pages.put(page.id, page);
            }
        }
    }

    
    
    @Override
    public ApplicationInstance addApplication(String applicationSettingId, String appAreaId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearPage(String pageId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPageDescription(String pageId, String description) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Page getPage(String id) throws ErrorException {
        return pages.get(id);
    }

    @Override
    public void deleteApplication(String id) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void saveApplicationConfiguration(ApplicationInstance config) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Page removeApplication(String pageAreaId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Page changePageUserLevel(String pageId, int userLevel) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, Setting> getSecuredSettingsInternal(String appName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ApplicationInstance> getApplications() throws ErrorException {
        return new ArrayList();
    }

    @Override
    public List<ApplicationInstance> getApplicationsByType(String type) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ApplicationInstance> getApplicationsBasedOnApplicationSettingsId(String appSettingsId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ApplicationInstance> getApplicationsByPageAreaAndSettingsId(String appSettingsId, String pageArea) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ApplicationInstance> getApplicationsForPage(String pageId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deletePage(String id) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addExistingApplicationToPageArea(String pageId, String appId, String area) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setParentPage(String pageId, String parentPageId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, List<String>> getPagesForApplications(List<String> appIds) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, String> translatePages(List<String> pages) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public void clearPageArea(String pageId, String pageArea) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void savePage(Page page) throws ErrorException {
        pages.put(page.id, page);
        databaseSaver.saveObject(page, credentials);
    }

    @Override
    public HashMap<String, Setting> getSecuredSettings(String applicationInstanceId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    @Override
    public String createNewRow(String pageId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addLayoutCell(String pageId, String incell, String aftercell) throws ErrorException {
        Page page = pages.get(pageId);
        if(page == null) {
            throw new ErrorException(30);
        }
        
        page.createCell(incell, aftercell);
        savePage(page);
    }
    
}