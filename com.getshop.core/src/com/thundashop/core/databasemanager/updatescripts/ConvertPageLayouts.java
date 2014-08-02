
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pagemanager.data.CommonPageData;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.pagemanager.data.PageLayout;
import com.thundashop.core.pagemanager.data.RowLayout;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ConvertPageLayouts  extends UpgradeBase {
    private int colcount = 1;
    
    public static void main(String[] args) throws UnknownHostException, ErrorException {
        ConvertPageLayouts cnpl = new ConvertPageLayouts();
        cnpl.convert();
        
    }

    private void convert() throws UnknownHostException, ErrorException {
        HashMap<String, HashMap<String, Page>> pageCollections = getAllPages();
        for(String collection : pageCollections.keySet()) {
            HashMap<String, Page> pages  = pageCollections.get(collection);
            boolean foundHeader = false;
            for(String pageId : pages.keySet()) {
                Page page = pages.get(pageId);
                PageLayout layout = page.layout;
                int colcount = 1;
                int maincount = 1;
                LinkedList<String> loopStrings = new LinkedList();
                List<String> areasAdded = new ArrayList();
                if(page.type > 0) {
                    convertToNewType(page);
                }
                
                PageArea header = page.pageAreas.get("header");
                if(header != null && (header.extraApplicationList.size() > 0 || header.applicationsList.size() > 0 || header.applications.size() > 0)) {
                    if(foundHeader) {
                        System.out.println("Duplicate header entry found for store: " + page.storeId);
                    }
                    CommonPageData data = new CommonPageData();
                    data.storeId = page.storeId;
                    data.header = header;
                    foundHeader = true;
                    data.footer = page.pageAreas.get("footer");
                    saveObject(data, "PageManager");
                }

                
                LinkedList newRows = new LinkedList();
                for(RowLayout row : layout.rows) {
                    loopStrings.add(row.rowId);
                }
                
                for(String rowId : loopStrings) {
                    RowLayout row = findRow(layout,rowId);
                    row.areas = new LinkedList();
                    if(row.numberOfCells == 1) {
                        PageArea area = page.pageAreas.get("main_"+maincount);
                        if(area == null) {
                            area = new PageArea();
                            area.type = "main_"+maincount;
                        }
                        row.areas.add(area);
                        areasAdded.add(area.type);
                        maincount++;
                    } else {
                        for(int i = 1;i <= row.numberOfCells; i++) {
                            PageArea area = page.pageAreas.get("col_"+colcount);
                            if(area == null) {
                                area = new PageArea();
                                area.type = "col_"+colcount;
                            }
                            row.areas.add(area);
                            areasAdded.add(area.type);
                            colcount++;
                        }
                    }
                    newRows.add(row);
                }

                
                for(String added : page.pageAreas.keySet()) {
                    if(!areasAdded.contains(added)) {
                        PageArea area = page.pageAreas.get(added);
                        if(area.type != null) {
                            if(area.type.startsWith("main_")) {
                            } else if(area.type.startsWith("col_")) {
                            } else {
                                layout.otherAreas.put(area.type, area);
                            }
                        }
                    }
                }
                
                layout.rows = newRows;
                
                saveObject(page, "PageManager");
            }
            if(!foundHeader) {
//                System.out.println("No header found for collection: " + collection );
            } else {
//                System.out.println("Header actually sfound for collection: " + collection );
            }
        }
    }

    private RowLayout findRow(PageLayout layout, String rowId) {
        for(RowLayout row : layout.rows) {
            if(row.rowId.equals(rowId)) {
                return row;
            }
        }
        System.out.println("Unable to find row");
        System.exit(0);
        return null;
    }

    private RowLayout createRow(Page page, Integer appareas) {
        RowLayout layout = new RowLayout();
        layout.numberOfCells = appareas;
        layout.rowId = UUID.randomUUID().toString();
        for(int i = 0; i < appareas; i++) {
            PageArea area = new PageArea();
            area.type = "col_" + colcount;
            layout.areas.add(area);
            colcount++;
        }
        return layout;
    }
    
    private void convertToNewType(Page page) {
        page.layout.rows = new LinkedList();
        switch (page.type) {
            case 1:
                createLayout(page,  1, 1);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                break;
            case 2:
                createLayout(page,  1, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                break;
            case 3:
                createLayout(page,  0, 1);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                break;
            case 4:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                break;
            case 7:
                createLayout(page,  0, 3);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 8:
                createLayout(page,  0, 2);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 9:
                createLayout(page,  0, 1);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 10:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 11:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 12:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 13:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            
            case 14:
                createLayout(page,  0, 2);
                page.layout.rightSideBarWidth = 48;
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 15:
                createLayout(page,  0, 0);
                page.layout.leftSideBarWidth = 48;
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 16:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 17:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 18:
                createLayout(page,  1, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 19:
                createLayout(page,  1, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 20:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 21:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 22:
                createLayout(page,  1, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 23:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 24:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 25:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 26:
//                createLayout(page,  1, 0);
//                page.layout.rows = new LinkedList();
//                page.layout.rows.add(createRow(page, 1);
//                page.layout.rows.add(createRow(page, 2);
//                page.layout.rows.add(createRow(page, 1);
                break;
            case 27:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 28:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 29:
                createLayout(page,  0, 0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            
            //Suggestions for the customer.
            case 30:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                break;
            case 31:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                break;
            case 32:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                break;
            
            //Two rows
            case 33:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 34:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 35:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 36:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 37:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 38:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 39:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 40:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 41:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                break;
            //three rows
            case 42:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 43:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 44:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 45:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 46:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 47:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 48:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 49:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 50:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 51:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 52:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 53:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 54:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 55:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 56:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 57:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 58:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 59:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 60:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 61:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 62:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 63:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 64:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 65:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                page.layout.rows.add(createRow(page, 3));
                break;
            case 66:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 1));
                break;
            case 67:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 2));
                break;
            case 68:
                createLayout(page,  0,0);
                page.layout.rows = new LinkedList();
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                page.layout.rows.add(createRow(page, 3));
                break;
        }
    }

    private void createLayout(Page page, int left_sidebar, int right_sidebar) {
        colcount = 0;
        page.layout.leftSideBar = left_sidebar;
        page.layout.rightSideBar = right_sidebar;
    }
}
