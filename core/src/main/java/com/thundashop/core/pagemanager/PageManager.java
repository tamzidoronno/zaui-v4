package com.thundashop.core.pagemanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.applications.StoreApplicationInstancePool;
import com.thundashop.core.common.*;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.pagemanager.data.CarouselConfig;
import com.thundashop.core.pagemanager.data.CellGroupAccess;
import com.thundashop.core.pagemanager.data.CommonPageData;
import com.thundashop.core.pagemanager.data.FloatingData;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageCell;
import com.thundashop.core.pagemanager.data.PageCellSettings;
import com.thundashop.core.pagemanager.data.PageComment;
import com.thundashop.core.pagemanager.data.SavedCommonPageData;
import com.thundashop.core.productmanager.ProductManager;
import com.thundashop.core.productmanager.data.Product;
import com.thundashop.core.productmanager.data.ProductConfiguration;
import com.thundashop.core.usermanager.UserManager;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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
    HashMap<String, PageComment> comments = new HashMap();
    CommonPageData commonPageData = new CommonPageData();
    SavedCommonPageData savedCommonPageData = new SavedCommonPageData();

    @Autowired
    private StoreApplicationInstancePool instancePool;

    @Autowired
    private ProductManager productManager;

    @Autowired
    private StoreApplicationInstancePool storeApplicationPool;

    @Autowired
    private UserManager userManager;
    
    @Autowired
    private ListManager listManager;

    @Autowired
    private GrafanaManager grafanaManager;

    public Page createPage() throws ErrorException {
        return createPage(null);
    }

    public Page createPageWithName(String pageId) {
        return createPage(pageId);
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

        saveObject(page);
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
            if (obj instanceof PageComment) {
                comments.put(obj.id, (PageComment)obj);
            }
            if (obj instanceof SavedCommonPageData) {
                savedCommonPageData = (SavedCommonPageData) obj;
            }
        }
        createDefaultPages();
    }

    @Override
    public ApplicationInstance addApplication(String applicationId, String pageCellId, String pageId ) {
        ApplicationInstance instance = instancePool.createNewInstance(applicationId);
        Page page = getPage(pageId);
        
        if (page != null && page.getCell(pageCellId) != null) {
            page.addApplication(pageCellId, instance.id); 
            savePage(page);
        } else {
            logPrint("Could not find cell to add app to");
        }
        
        backupPage(page);
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

        if (page == null && userManager.getLoggedOnUser() != null && userManager.getLoggedOnUser().isAdministrator()) {
            page = createPage(id);
        }
        
        if (page == null) {
            return null;
        }
        
        page = finalizePage(page);

        return page;
    }

    private Page finalizePage(Page page) {
        page.finalizePage(commonPageData);
        
        Entry entry = listManager.findEntryByPageId(page.id);
        if(entry == null) {
            entry = listManager.getListEntry(page.id);
        }
        if(entry != null) {
            page.title = entry.name;
            page.pageScroll = entry.pageScroll;
        }
        
        List<PageCell> cellsWithoutIncrementalId = page.getCellsFlatList().stream()
                .filter(cell -> cell.incrementalCellId == null)
                .collect(Collectors.toList());


        if (cellsWithoutIncrementalId.size() > 0) {
            cellsWithoutIncrementalId.stream().forEach(cell -> cell.incrementalCellId = getNextCellId());
            savePage(page);
        }
        
//        page.layout.clearMobileList();
//  This is a failed attempt on mobile implementation.
//        if(page.layout.needMobileList()) {
//            page.layout.updateMobileList();
//            savePage(page);
//        }

        if (page.isASlavePage()) {
            page.finalizeSlavePage(getPage(page.masterPageId));
            page.getCellsFlatList().stream()
                .filter(pageCell -> instancePool.getApplicationInstance(pageCell.appId) != null)
                .filter(pageCell -> instancePool.getApplicationInstance(pageCell.appId).appSettingsId != null)
                .filter(pageCell -> instancePool.getApplicationInstance(pageCell.appId).appSettingsId.equals("fb19a166-5465-4ae7-9377-779a8edb848e"))
                .forEach(o -> updateCreateApplications(o, page));
            page.finalizeSlavePage(getPage(page.masterPageId));
        }
        
        page.layoutBackups = new LinkedList();
        page.layoutBackups.addAll(savedCommonPageData.getSavedLayouts(page.id));
        
        List<PageCell> cells = getAllCells();
        page.fixedClonedCells(cells);
        
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
        if (page.isASlavePage()) {
            page.overrideApps.remove(cellid);
        } else {
            page.layout.removeAppFromCell(cellid);
        }
        
        savePage(page);
        backupPage(page);
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
        if(page == null) {
            return null;
        }
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
    public List<String> getPagesForApplication(String appId) {
        List<String> retPages = new ArrayList();
        for (Page page : pages.values()) {
            for (PageCell cell : page.getCellsFlatList()) {
                if (cell.appId != null && cell.appId.equals(appId)) {
                    retPages.add(page.id);
                }
                
                if (page.overrideApps.values().contains(appId)) {
                    retPages.add(page.id);
                }
            }
        }
        
        return retPages;
    }

    public List<String> getPagesForApplicationOnlyBody(String appId) {
        List<String> retPages = new ArrayList();
        for (Page page : pages.values()) {
            // We do not care about the slave pages as they follow the same rights as as the masterpage.
            if (page.isASlavePage()) {
                continue;
            }
            for (PageCell cell : page.layout.getCellsInBodyFlatList()) {
                if (cell.appId != null && cell.appId.equals(appId)) {
                    retPages.add(page.id);
                }
                
                if (page.overrideApps.values().contains(appId)) {
                    retPages.add(page.id);
                }
            }
        }
        
        return retPages;
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
        saveObject(page);
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
    
    public PageCell getLooseCell(String pageId, String cellId) {
        Page page = getPage(pageId);
        PageCell celltoReturn = page.getCell(cellId);
        if(celltoReturn == null) {
            String tmpcellid = page.layout.createCell("", "", PageCell.CellMode.row, "freeArea");
            celltoReturn = page.getCell(tmpcellid);
            celltoReturn.cellId = cellId;
            savePage(page);
        }
        return celltoReturn;
    }

    @Override
    public String addLayoutCell(String pageId, String incell, String beforecell, String mode, String area) throws ErrorException {
        Page page = getPage(pageId);
        
        if (page.isASlavePage()) {
            throw new NullPointerException("Tried to modifify layout for a slave page, not possible, modify the template page: " + page.masterPageId);
        }
        
        boolean onlyMobile = false;
        if(mode != null && mode.equals("rowmobile")) {
            mode = PageCell.CellMode.row;
            onlyMobile = true;
        }
        
        String cell = page.layout.createCell(incell, beforecell, mode, area);
        
        if(incell != null && !incell.isEmpty()) {
            if(page.getCell(incell).mode.equals(PageCell.CellMode.rotating)) {
                page.layout.getCell(cell).hideOnDesktop = false;
                page.layout.getCell(cell).hideOnMobile = true;
            }
        }
        
        if(onlyMobile) {
            page.layout.getCell(cell).hideOnDesktop = true;
            page.layout.getCell(cell).hideOnMobile = false;
        }
        
        page.layout.checkAndFixDoubles();

        backupPage(page);
        
        savePage(page);
        return cell;
    }

    @Override
    public Page dropCell(String pageId, String cellId) throws ErrorException {
        Page page = getPage(pageId);
        page.layout.deleteCell(cellId);
        
        backupPage(page);
        
        savePage(page);
        return page;
    }

    private void saveCommonAreas() {
        commonPageData.storeId = storeId;
        saveObject(commonPageData);
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
        
        backupPage(page);
        
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
            if(instance != null) {
                cell.appId = instance.id;
            }
        }
    }

    @Override
    public PageCell getCell(String pageId, String cellId) throws ErrorException {
        return getPage(pageId).getCell(cellId);
    }
    
    public PageCell getParentCell(String pageId, String cellId) {
        return getPage(pageId).getParentCell(cellId);
    }

    @Override
    public void linkPageCell(String pageId, String cellId, String link) throws ErrorException {
        getPage(pageId).getCell(cellId).link = link;
    }

    @Override
    public void createHeaderFooter(String type) throws ErrorException {
        PageCell cell = new PageCell();
        cell.mode = PageCell.CellMode.row;
        if(type.equals("FOOTER")) {
            commonPageData.footer.add(cell);
            saveObject(commonPageData);
        } else {
            commonPageData.header.add(cell);            
        }
    }

    @Override
    public void updateCellLayout(List<Integer> layout, String pageId, String cellId) {
        Page page = getPage(pageId);
        PageCell cell = page.getCell(cellId);
        for(Integer count : layout) {
            String row = cellId;
            if(layout.size() > 1 || page.getParentCell(cellId) != null) {
                row = page.layout.createCell(cellId, "", PageCell.CellMode.row, "body");
            } else {
                count++; 
            }
            if(count > 1) {
                for(int i = 0; i < count-1; i++) {
                    page.layout.createCell(row, "", PageCell.CellMode.column, "body");
                }
            }
        }
        cell.cells.remove(0);
        backupPage(page);
    }

    @Override
    public void swapAppWithCell(String pageId, String fromCellId, String toCellId) {
        Page page = getPage(pageId);
        PageCell fromCell = page.getCell(fromCellId);
        PageCell toCell = page.getCell(toCellId);
        
        String tmpAppId = fromCell.appId;
        fromCell.appId = toCell.appId;
        toCell.appId = tmpAppId;
        
        saveObject(page);
    }

    @Override
    public void saveCell(String pageId, PageCell cell) throws ErrorException {
        Page page = getPage(pageId);
        PageCell cellToChange = page.getCell(cell.cellId);
        cellToChange.overWrite(cell);
        
        backupPage(page);

        saveObject(page);
    }

    @Override
    public void savePageCellSettings(String pageId, String cellId, PageCellSettings settings) {
        Page page = getPage(pageId);
        PageCell cell = page.getCell(cellId);
        cell.settings = settings;
        checkIfNeedToFlip(page, cellId, settings);
        
        backupPage(page);
        
        saveObject(page);
    }

    public Page createPageFromTemplatePage(String pageId) {
        Page page = getPage(pageId);
        
        if (page == null) {
            page = createPage(pageId);
        }
        
        if (page != null) {
            Page newPage = page.jsonClone();
            newPage.masterPageId = page.id;
            savePage(newPage);
            return newPage;
        }
        
        return null;
    }

    private void updateCreateApplications(PageCell o, Page page) {
        ApplicationInstance oldApplication = instancePool.getApplicationInstance(o.appId);
        Setting appIdSetting = oldApplication.settings.get("appId");
        
        if (appIdSetting != null && page.overrideApps.get(o.cellId) == null) {
            ApplicationInstance newInstance = instancePool.createNewInstance(appIdSetting.value);
            page.addApplication(o.cellId, newInstance.id);
            savePage(page);
        }
    }

    private void removeApplicationsThatHasAccessDenied(Page page) {
        for (PageCell cell : page.getCellsFlatList()) {
            ApplicationInstance instace = instancePool.getApplicationInstance(cell.appId);
            if (instace != null && instace.appSettingsId != null && instace.appSettingsId.equals("access_denied")) {
                logPrint("Removing: " + cell.appId);
                cell.appId = null;
            }
        }
    }

    @Override
    public Object preProcessMessage(Object object, Method executeMethod) {
        if (object instanceof Page) {
            Page page = ((Page)object).makeClone();
            removeApplicationsThatHasAccessDenied(page);
            return page;
        }
        
        return super.preProcessMessage(object, executeMethod);
    }

    @Override
    public List<PageCell> getMobileBody(String pageId) {
        return getPage(pageId).getMobileList();
    }

    @Override
    public void resetMobileLayout(String pageId) {
        Page page = getPage(pageId);
        page.resetMobileLayout();
        savePage(page);
    }

    @Override
    public void flattenMobileLayout(String pageId) {
        Page page = getPage(pageId);
        page.flattenMobileLayout();
        savePage(page);
    }

    @Override
    public void toggleLeftSideBar(String pageId, String columnName) throws ErrorException {
        Page page = pages.get(pageId);
        if (page != null) {
            if (!page.leftSideBarName.equals(columnName)) {
                page.leftSideBar = true;
            } else {
                page.leftSideBar = !page.leftSideBar;
            }
            
            page.leftSideBarName = columnName;
            
            if (commonPageData.leftSideBars.get(columnName) == null) {
                commonPageData.leftSideBars.put(columnName, new ArrayList());
                saveObject(page);
            }
            
            saveObject(page);
        }
    }

    private void checkIfNeedToFlip(Page page, String cellId, PageCellSettings settings) {
        PageCell cell = page.getCell(cellId);
        cell.settings = settings;
        
        if(settings.isFlipping != null && !settings.isFlipping.isEmpty()) {
            setCellMode(page.id, cellId, PageCell.CellMode.flip);
        } else {
            if(!cell.cells.isEmpty() && cell.cells.get(0).mode.equals(PageCell.CellMode.flip)) {
                cell.appId = cell.cells.get(0).cells.get(0).appId;
                cell.cells.clear();
            }
        }
    }

    @Override
    public void restoreLayout(String pageId, Long fromTime) throws ErrorException {
        Page page = getPage(pageId);
        page.layout = savedCommonPageData.getSavedLayout(pageId, fromTime);
        CommonPageData newArea = savedCommonPageData.getClosestLayout(fromTime);
        if(newArea != null) {
            commonPageData = newArea;
        }
        savePage(page);
    }

    private String getCellAfter(String cellId, String pageId, String area) {
        Page page = getPage(pageId);
        HashMap<String, ArrayList<PageCell>> areas = page.layout.getAreas();
        boolean found = false;
        for(PageCell cell : areas.get(area)) {
            if(found) {
                return cell.cellId;
            }
            if(cell.cellId.equals(cellId)) {
                found = true;
            }
        }
        return null;
    }

    private void backupPage(Page page) {
        savedCommonPageData.backupCurrentLayout(page.id, page.layout);
        savedCommonPageData.saveData(commonPageData);
        saveObject(savedCommonPageData);
    }

    @Override
    public List<String> getLeftSideBarNames() {
        Set<String> names = new TreeSet();
        pages.values().stream()
                .forEach( o -> names.add(o.leftSideBarName));
        
        return new ArrayList(names);
    }

    @Override
    public void createModal(String modalName) {
        if (commonPageData.modals.containsKey(modalName)) {
            return;
        }
        
        commonPageData.modals.put(modalName, new ArrayList());
        saveObject(commonPageData);
    }   

    @Override
    public List<String> getModalNames() {
        return new ArrayList<String>(commonPageData.modals.keySet());
    }

    @Override
    public void saveMobileLink(String link) {
        commonPageData.mobileLink = link;
        saveObject(commonPageData);
    }

    @Override
    public String getMobileLink() {
        return commonPageData.mobileLink;
    }

    public void changeTemplateForPage(String pageId, String selectedProductTemplate) {
        Page page = getPage(selectedProductTemplate);
        
        if (page == null) {
            page = createPage(pageId);
        }
        
        if (page != null) {
            Page newPage = page.jsonClone();
            newPage.masterPageId = page.id;
            newPage.id = pageId;
            savePage(newPage);
        }
    }

    private List<PageCell> getAllCells() {
        List<PageCell> allCells = new ArrayList();
        for(Page p : pages.values()) {
            allCells.addAll(p.getCellsFlatList());
        }
        return allCells;
    }

    @Override
    public void savePageCellGroupAccess(String pageId, String cellId, CellGroupAccess groupAccess) {
        Page page = getPage(pageId);
        PageCell cell = page.getCell(cellId);
        cell.groupAccess = groupAccess;
        
        backupPage(page);
        saveObject(page);
    }

    @Override
    public void startLoadPage() {
        HashMap<String, Object> toAdd = new HashMap();
        grafanaManager.addPoint("webdata", "pageload", toAdd);
    }

    @Override
    public void addComment(PageComment pageComment) {
        if (getSession() != null && getSession().currentUser != null)
            pageComment.addedByUserId = getSession().currentUser.id;
        
        saveObject(pageComment);
        comments.put(pageComment.id, pageComment);
    }

    @Override
    public void deleteComment(String commentId) {
        PageComment comment = comments.remove(commentId);
        if (comment != null) {
            deleteObject(comment);
        }
    }

    @Override
    public List<PageComment> getComments(String pageId) {
        return comments.values().stream()
                .filter(comment -> comment.pageId.equals(pageId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean accessDenied(String pageId) {
        Page page = getPage(pageId);
        int removed = 0;
        if (page != null) {
            LinkedList<PageCell> bodyArea = page.layout.getCellsInBodyFlatList();
                    ;
            for (PageCell cell : bodyArea) {
                ApplicationInstance instace = instancePool.getApplicationInstance(cell.appId);
                if (instace != null && instace.appSettingsId != null && instace.appSettingsId.equals("access_denied")) {
                    removed++;
                }
            }
            
            long cellsWithApps = bodyArea.stream().filter(b -> b.appId != null && !b.appId.isEmpty()).count();
            
            if (removed == cellsWithApps && removed != 0) {
                return true;
            }
        }
        
        
        return false;
    }
}