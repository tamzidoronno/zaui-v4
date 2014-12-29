package com.thundashop.core.pagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.CarouselConfig;
import com.thundashop.core.pagemanager.data.CommonPageData;
import com.thundashop.core.pagemanager.data.FloatingData;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageCell;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
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

    @Autowired
    private ProductManager productManager;

    @Autowired
    private StoreApplicationInstancePool storeApplicationPool;

    @Override
    public Page createPage() throws ErrorException {
        return createPage(null);
    }

    private Page createPage(String pageId) {
        Page page = new Page();
        if (pages.isEmpty()) {
            page.id = "home";
        }
        page.storeId = storeId;

        if (pageId != null) {
            page.id = pageId;
        }

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
        createDefaultPages();
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
        createDefaultPages();
        Page page = pages.get(id);

        if (page == null) {
            return null;
        }

//        page.dumpLayout();        
        return finalizePage(page);

    }

    private Page finalizePage(Page page) {
        page.finalizePage(commonPageData);
        List<PageCell> cellsWithoutIncrementalId = page.getCellsFlatList().stream()
                .filter(cell -> cell.incrementalCellId == null)
                .collect(Collectors.toList());

        addProductAppIfNeeded(page);

        if (cellsWithoutIncrementalId.size() > 0) {
            cellsWithoutIncrementalId.stream().forEach(cell -> cell.incrementalCellId = getNextCellId());
            savePage(page);
        }

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
    public String addLayoutCell(String pageId, String incell, String beforecell, String mode, String area) throws ErrorException {
        Page page = getPage(pageId);
        String cell = page.layout.createCell(incell, beforecell, mode, area);
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
        saveCommonAreas();
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

    @Override
    public void setCellName(String pageId, String cellId, String cellName) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.cellName(cellId, cellName);
        savePage(page);
    }

    @Override
    public void setCellMode(String pageId, String cellId, String mode) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.switchMode(cellId, mode);
        savePage(page);
    }

    private int getNextCellId() {
        String cellIds = getManagerSetting("cell_id_counter");
        int cellCount = 0;

        if (cellIds != null) {
            cellCount = Integer.valueOf(cellIds);
        }

        cellCount++;

        setManagerSetting("cell_id_counter", "" + cellCount);
        return cellCount;
    }

    @Override
    public void saveCellPosition(String pageId, String cellId, FloatingData data) throws ErrorException {
        Page page = getPage(pageId);
        page.getCell(cellId).floatingData = data;
        savePage(page);
    }

    private void createDefaultPages() {
        createDefaultPage("home");
        createDefaultPage("productsearch");
        createDefaultPage("login");
    }

    private void createDefaultPage(String pageId) {
        Page page = pages.get(pageId);
        if (page == null) {
            createPage(pageId);
        }
    }

    private void addProductAppIfNeeded(Page page) {
        Product product = productManager.findProductByPage(page.id);

        if (product != null) {
            String productApplicationId = "06f9d235-9dd3-4971-9b91-88231ae0436b";
            page.type = "product";
            
            long productAppsCount = page.getCellsFlatList().stream()
                    .filter(cell -> cell.appId != null)
                    .map(cell -> storeApplicationPool.getApplicationInstance(cell.appId))
                    .filter(app -> app != null && app.appSettingsId.equals(productApplicationId))
                    .count();

            if (productAppsCount == 0) {
                ApplicationInstance instance = storeApplicationPool.createNewInstance(productApplicationId);
                page.layout.addApplicationToFirstFreeBodyCell(instance.id);
                savePage(page);
                System.out.println("Should have an app");
            }
        }
    }
}
