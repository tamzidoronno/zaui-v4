/**
 * This class is a part of the thundashop project.
 *
 * All rights reserved
 *
 *
 */
package com.thundashop.core.pagemanager.data;

import org.mongodb.morphia.annotations.Transient;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.Translation;
import com.thundashop.core.listmanager.data.Entry;
import java.util.ArrayList;
import java.util.List;

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
    }

    public PageCell getCell(String pageCellId) {
        return layout.getCell(pageCellId);
    }

    public List<PageCell> getCellsFlatList() {
        return layout.getCellsFlatList();
    }

    public void dumpLayout() {
        System.out.println("\n\n PAGE");
        for (String area : layout.areas.keySet()) {
            System.out.println("-----------------------------------------------");
            System.out.println(area);
            ArrayList<PageCell> rows = layout.areas.get(area);
            dumpRows(rows, 1);
            System.out.println("-----------------------------------------------\n");
        }
    }

    private void dumpRows(ArrayList<PageCell> rows, int depth) {
        for (PageCell row : rows) {
            for (int i = 0; i < depth; i++) {
                System.out.print("\t");
            }
            System.out.print(row.cellId);
            System.out.print(" subcells: " + row.cells.size());
            System.out.print(", mode: " + row.mode);
            System.out.print(", type: " + row.type);
            System.out.print(", styles: " + row.styles.replaceAll("\\\n", ""));
            if (row.cellName != null && !row.cellName.isEmpty()) {
                System.out.println("(" + row.cellName + ")");
            }
            System.out.println();
            if (row.cells.size() > 0) {
                dumpRows(row.cells, depth + 1);
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
