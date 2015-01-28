package com.thundashop.core.pagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.pagemanager.data.CarouselConfig;
import com.thundashop.core.pagemanager.data.CommonPageData;
import com.thundashop.core.pagemanager.data.FloatingData;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.Page.PageType;
import com.thundashop.core.pagemanager.data.PageCell;
import com.thundashop.core.pagemanager.data.PageLayout;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductConfiguration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        addProductAppIfNeeded(page);
        addProductDetailsIfNeeded(page);
        
        List<PageCell> cellsWithoutIncrementalId = page.getCellsFlatList().stream()
                .filter(cell -> cell.incrementalCellId == null)
                .collect(Collectors.toList());


        if (cellsWithoutIncrementalId.size() > 0) {
            cellsWithoutIncrementalId.stream().forEach(cell -> cell.incrementalCellId = getNextCellId());
            savePage(page);
        }
        
//        page.layout.clearMobileList();
        if(page.layout.needMobileList()) {
            page.layout.updateMobileList();
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
        
        boolean onlyMobile = false;
        if(mode != null && mode.equals("rowmobile")) {
            mode = PageCell.CellMode.row;
            onlyMobile = true;
        }
        
        String cell = page.layout.createCell(incell, beforecell, mode, area);
        
        if(onlyMobile) {
            page.layout.getCell(cell).hideOnDesktop = true;
            page.layout.getCell(cell).hideOnMobile = false;
        }
        
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
        savePage(page);
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
        createDefaultPage("cart");
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
                page.layout.createCell("", "", PageCell.CellMode.row, "body");
                ApplicationInstance instance = storeApplicationPool.createNewInstance(productApplicationId);
                page.layout.addApplicationToFirstFreeBodyCell(instance.id);
                savePage(page);
            }
        }
    }

    @Override
    public void moveCellMobile(String pageId, String cellId, Boolean moveUp) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.moveCellMobile(cellId, moveUp);
        savePage(page);
    }

    @Override
    public void toggleHiddenOnMobile(String pageId, String cellId, Boolean hidden) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.toggleHiddenOnMobile(cellId, hidden);
        savePage(page);
    }

    @Override
    public void togglePinArea(String pageId, String cellId) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.togglePinArea(cellId);
        savePage(page);
    }

    @Override
    public void setWidth(String pageId, String cellId, Integer outerWidth, Integer outerWidthWithMargins) throws ErrorException {
        Page page = getPage(pageId);
        PageCell cell = page.layout.getCell(cellId);
        cell.outerWidth = outerWidth;
        cell.outerWidthWithMargins = outerWidthWithMargins;
        savePage(page);
    }

    private void addProductDetailsIfNeeded(Page page) {
        productManager.productConfiguration = new ProductConfiguration();
        productManager.productConfiguration.productTabs = new LinkedHashMap();
        productManager.productConfiguration.productTabs.put("info", false);
        productManager.productConfiguration.productTabs.put("relatedProducts", false);
        productManager.productConfiguration.productTabs.put("comments", false);
        productManager.productConfiguration.productTabs.put("attributes", true);
        
        boolean modified = false;
        if(page.type != null && page.type.equals("product")) {
            PageCell tabcell = null;
            List<PageCell> cells = page.layout.getCells("body");
            for(PageCell cell : cells) {
               if(cell.isTab()) {
                   tabcell = cell;
                   break;
               } 
            }
            
            if(tabcell == null) {
                String cell = page.layout.createCell("", "", PageCell.CellMode.tab, "body");
                tabcell = page.layout.getCell(cell);
                tabcell.cells = new ArrayList();
                modified=true;
            }
            
            //Add missing tabs.
            HashMap<String, Boolean> productattrs = productManager.productConfiguration.productTabs;
            for(String cellName : productattrs.keySet()) {
                boolean subcellfound = false;
                for(PageCell subcell : page.layout.getCellsFlatList()) {
                    if(subcell.systemCellName.equals(cellName)) {
                        subcellfound = true;
                        if(subcell.isHidden != productattrs.get(cellName)) {
                            subcell.isHidden = productattrs.get(cellName);
                            modified = true;
                        }
                    }
                }
                if(!subcellfound) {
                    //We need to add this tab.
                    String newCell = page.layout.createCell(tabcell.cellId, null, PageCell.CellMode.row, "body");
                    PageCell newCellObject = page.layout.getCell(newCell);
                    newCellObject.cellName = cellName;
                    newCellObject.systemCellName = cellName;
                    attachProductApplications(newCell, page);
                    modified = true;
                }
            }
            
            //Check if any areas has been removed.
            List<String> cellsToRemove = new ArrayList();
            for(PageCell subcell : tabcell.cells) {
                boolean subcellfound = false;
                for(String cellName : productattrs.keySet()) {
                    if(subcell.systemCellName.equals(cellName)) {
                        subcellfound = true;
                    }
                }
                if(!subcellfound && !subcell.systemCellName.isEmpty()) {
                    cellsToRemove.add(subcell.cellId);
                }
            }
            
            for(String id : cellsToRemove) {
                page.layout.deleteCell(id);
                modified = true;
            }
        }
        
        if(modified) {
            savePage(page);
        }
    }

    private void attachProductApplications(String newCell, Page page) {
        PageCell cell = page.layout.getCell(newCell);
        if(cell.cellName.equals("info")) {
            String contentManager = "320ada5b-a53a-46d2-99b2-9b0b26a7105a";
            ApplicationInstance instance = storeApplicationPool.createNewInstance(contentManager);
            cell.appId = instance.id;
        }
    }

    @Override
    public PageCell getCell(String pageId, String cellId) throws ErrorException {
        return getPage(pageId).getCell(cellId);
    }
}
