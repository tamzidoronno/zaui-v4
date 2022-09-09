/**
 * This class is a part of the thundashop project.
 *
 * All rights reserved
 *
 *
 */
package com.thundashop.core.pagemanager.data;

import com.google.gson.Gson;
import org.mongodb.morphia.annotations.Transient;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.GetShopRemoteObject;
import com.thundashop.core.common.Translation;
import com.thundashop.core.listmanager.data.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
@GetShopRemoteObject
public class Page extends DataCommon implements Cloneable {

    public Page parent;
    public String type;
    public int userLevel = 0;
    @Translation
    public String description = "";
    public LinkedList<Long> layoutBackups = new LinkedList<>();
    public PageLayout layout = new PageLayout();
    @Translation
    public String title;
    public String customCss = "";
    
    public String metaKeywords = "";
    public String metaTitle = "";
    public String overridePageTitle = "";

    public String leftSideBarName = "left_side_bar";
    public boolean leftSideBar = false;
    public boolean pageScroll = false;
    
    public String masterPageId = "";
    
    /**
     * Key 1 = CellID
     * Value 2 = ApplicationInstanceId
     */
    public HashMap<String, String> overrideApps = new HashMap<>();
    
    public Page() {
    }
    
    public void clear() {
        layout.clear();
    }

    public void deletePageAreas() {
        layout.setNewList(new ArrayList<>(), "body", false);
    }

    public void finalizePage(CommonPageData pagedata) {
        layout.setNewList(pagedata.header, "header", true);
        layout.setNewList(pagedata.footer, "footer", true);
        layout.setNewList(pagedata.bodyFooter, "bodyfooter", true);
        if (layout.getAreas().get("body") == null || layout.getAreas().get("body").isEmpty()) {
            layout.clear();
        }
        
        layout.setNewList(pagedata.leftSideBars.get(leftSideBarName), leftSideBarName, true);
        
        for (String key : pagedata.modals.keySet()) {
            if (key.equals("header") || key.equals("footer") || key.equals("body") || key.equals("freearea")) {
                continue;
            }
            
            if (pagedata.modals.get(key) == null) {
                pagedata.modals.put(key, new ArrayList<>());
            }
            
            layout.setNewList(pagedata.modals.get(key), key, true);
        }
        
    }

    public PageCell getCell(String pageCellId) {
        return layout.getCell(pageCellId);
    }

    public List<PageCell> getCellsFlatList() {
        return layout.getCellsFlatList();
    }

    public Page jsonClone() {
        Page newPage = makeClone();
        newPage.id = UUID.randomUUID().toString();
        return newPage;
    }
    
    public Page makeClone() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        Page newPage = gson.fromJson(json, Page.class);
        return newPage;
    }

    public void finalizeSlavePage(Page masterPage) {
        if (masterPage != null) {
            layout = masterPage.layout.jsonClone();
            leftSideBar = masterPage.leftSideBar;
            leftSideBarName = masterPage.leftSideBarName;
            
            for (String cellId : overrideApps.keySet()) {
                String instanceId = overrideApps.get(cellId);
                if (getCell(cellId) != null) {
                    getCell(cellId).appId = instanceId;
                }
            }
        }
    }

    public void addApplication(String cellId, String instanceId) {
        if (isASlavePage()) {
            overrideApps.put(cellId, instanceId);
        } else {
            if (getCell(cellId) != null) {
                getCell(cellId).appId = instanceId;
            }
        }
    }

    public boolean isASlavePage() {
        return masterPageId != null && !masterPageId.isEmpty();
    }

    public void removeApplicationsThatDoesNotExists() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<PageCell> getMobileList() {
       return layout.getMobileBody();
    }

    public void resetMobileLayout() {
        layout.resetMobileList();
    }

    public void flattenMobileLayout() {
        layout.flattenMobileLayout();
    }

    public PageCell getParentCell(String cellId) {
        return layout.getParent(cellId);
    }

    public void fixedClonedCells(List<PageCell> allCells) {
        List<PageCell> cells = getCellsFlatList();
        for(PageCell cell : cells) {
            if(cell.settings != null && !cell.settings.cloneCellId.isEmpty()) {
                for(PageCell cell2 :allCells) {
                    if(cell2.settings != null && cell2.settings.cellName.equals(cell.settings.cloneCellId)) {
                        cell.cloneCell(cell2);
                    }
                }
            }
        }
    }

    public static class DefaultPages {

        public static String OrderOverviewPageId = "orderoverview";
        public static String CartPage = "cart";
        public static String Home = "home";
        public static String CheckOut = "checkout";
        public static String MyAccount = "myaccount";
        public static String Users = "users";
        public static String Settings = "settings";
        public static String Domain = "domain";
        public static String Callback = "callback";
        public static String MenuEditor = "menueditor";
    }

    public static class PageType {

        public static int Initialize = -2;
        public static int Introduction = -1;
        public static int Standard = 1;
        public static int Product = 2;

    }

    public static class LayoutType {

        public static int HeaderFooterLeftMiddleRight = 1;
        public static int HeaderLeftMiddleFooter = 2;
        public static int HeaderRightMiddleFooter = 3;
        public static int HeaderMiddleFooter = 4;
    }

    /**
     * This might not be set, only in just a few cases.
     */
    @Transient
    public Entry linkToListEntry;

}
