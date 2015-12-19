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
public class Page extends DataCommon implements Cloneable {

    public Page parent;
    public String type;
    public int userLevel = 0;
    public String description = "";
    public PageLayout layout = new PageLayout();
    @Translation
    public String title;
    public String customCss = "";
    
    public String metaKeywords = "";
    public String metaTitle = "";
    public String overridePageTitle = "";

    public boolean leftSideBar = false;
    
    public String masterPageId = "";
    
    /**
     * Key 1 = CellID
     * Value 2 = ApplicationInstanceId
     */
    public HashMap<String, String> overrideApps = new HashMap();
    
    public Page() {
    }
    
    public void clear() {
        layout.clear();
    }

    public void deletePageAreas() {
        layout.setNewList(new ArrayList(), "body", false);
    }

    public void finalizePage(CommonPageData pagedata) {
        layout.setNewList(pagedata.header, "header", true);
        layout.setNewList(pagedata.footer, "footer", true);
        if (layout.areas.get("body") == null || layout.areas.get("body").isEmpty()) {
            layout.clear();
        }
        layout.finalizeLayout();
        layout.setNewList(pagedata.leftSideBar, "left_side_bar", true);
        layout.clearOnFinalizePage();
    }

    public PageCell getCell(String pageCellId) {
        return layout.getCell(pageCellId);
    }

    public List<PageCell> getCellsFlatList() {
        return layout.getCellsFlatList();
    }

//    public void dumpLayout() {
//        System.out.println("\n\n PAGE");
//        for (String area : layout.areas.keySet()) {
//            System.out.println("-----------------------------------------------");
//            System.out.println(area);
//            ArrayList<PageCell> rows = layout.areas.get(area);
//            dumpRows(rows, 1);
//            System.out.println("-----------------------------------------------\n");
//        }
//    }
//
//    private void dumpRows(ArrayList<PageCell> rows, int depth) {
//        for (PageCell row : rows) {
//            for (int i = 0; i < depth; i++) {
//                System.out.print("\t");
//            }
//            System.out.print(row.cellId);
//            System.out.print(" subcells: " + row.cells.size());
//            System.out.print(", mode: " + row.mode);
//            System.out.print(", type: " + row.type);
//            System.out.print(", styles: " + row.styles.replaceAll("\\\n", ""));
//            if (row.cellName != null && !row.cellName.isEmpty()) {
//                System.out.println("(" + row.cellName + ")");
//            }
//            System.out.println();
//            if (row.cells.size() > 0) {
//                dumpRows(row.cells, depth + 1);
//            }
//        }
//    }

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
            PageCell cell = getCell(cellId);
            cell.appId = instanceId;
        }
    }

    public boolean isASlavePage() {
        return masterPageId != null && !masterPageId.isEmpty();
    }

    public void removeApplicationsThatDoesNotExists() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public List<PageCell> getMobileList() {
         System.out.println("Mobile list for id: " + id);
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
