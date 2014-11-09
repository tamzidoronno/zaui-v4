package com.thundashop.core.pagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.CarouselConfig;
import com.thundashop.core.pagemanager.data.CommonPageData;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageCell;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class PageManager extends ManagerBase implements IPageManager {

    HashMap<String, Page> pages = new HashMap();
    CommonPageData commonPageData = new CommonPageData();

    @Autowired
    private StoreApplicationInstancePool instancePool;

    @Override
    public Page createPage() throws ErrorException {
        Page page = new Page();
        if (pages.isEmpty()) {
            page.id = "home";
        }
        page.storeId = storeId;

        databaseSaver.saveObject(page, credentials);
        pages.put(page.id, page);
        return page;
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon obj : data.data) {
            if (obj instanceof Page) {
                Page page = (Page) obj;
                pages.put(page.id, page);
            }
            if (obj instanceof CommonPageData) {
                commonPageData = (CommonPageData) obj;
            }
        }
    }

    @Override
    public ApplicationInstance addApplication(String applicationId, String pageCellId) {
        ApplicationInstance instance = instancePool.createNewInstance(applicationId);
        List<Page> pagesWithCells = getPagesThatHasCell(pageCellId);

        for (Page page : pagesWithCells) {
            page.getCell(pageCellId).appId = instance.id;
        }

        pagesWithCells.stream().forEach(page -> savePage(page));
        return instance;
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
        Page page = pages.get(id);

        if (page == null) {
            return null;
        }

        page.finalizePage(commonPageData);
        return page;
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
    public Page removeAppFromCell(String pageId, String cellid) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.removeAppFromCell(cellid);
        savePage(page);
        return page;
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
        Page page = getPage(pageId);
		List<PageCell> cells = page.getCellsFlatList();
		
		return cells.stream()
				.filter(cell -> cell.appId != null)
				.map(cell -> instancePool.getApplicationInstance(cell.appId))
				.filter(appInstance -> appInstance != null)
				.collect(Collectors.toList());
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
    public void savePage(Page page) {
        pages.put(page.id, page);
        databaseSaver.saveObject(page, credentials);
        saveCommonAreas();
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
    public String addLayoutCell(String pageId, String incell, String beforecell, String direction, String area) throws ErrorException {
        Page page = getPage(pageId);
        String cell = page.layout.createCell(incell, beforecell, direction, area);
        savePage(page);
        return cell;
    }

    @Override
    public Page dropCell(String pageId, String cellId) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.deleteCell(cellId);
        savePage(page);
        return page;
    }

    private void saveCommonAreas() {
        commonPageData.storeId = storeId;
        databaseSaver.saveObject(commonPageData, credentials);
    }

    private List<Page> getPagesThatHasCell(String pageCellId) {
        return pages.values().stream()
                .filter(page -> page.getCell(pageCellId) != null)
                .collect(Collectors.toList());
    }

    @Override
    public void setStylesOnCell(String pageId, String cellId, String styles, String innerStyles, Double width) {
        Page page = getPage(pageId);
        page.layout.updateStyle(cellId, styles, width, innerStyles);
    }

    @Override
    public void moveCell(String pageId, String cellId, boolean up) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.moveCell(cellId, up);
        savePage(page);
    }

    @Override
    public void setCarouselConfig(String pageId, String cellId, CarouselConfig config) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.setCarouselConfig(cellId, config);
        savePage(page);
    }

}
