
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageArea;
import com.thundashop.core.pagemanager.data.PageLayout;
import com.thundashop.core.pagemanager.data.RowLayout;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ConvertPageLayouts  extends UpgradeBase {
    public static void main(String[] args) throws UnknownHostException, ErrorException {
        ConvertPageLayouts cnpl = new ConvertPageLayouts();
        cnpl.convert();
        
    }

    private void convert() throws UnknownHostException, ErrorException {
        HashMap<String, HashMap<String, Page>> pageCollections = getAllPages();
        for(HashMap<String, Page> pages : pageCollections.values()) {
            for(String pageId : pages.keySet()) {
                Page page = pages.get(pageId);
                PageLayout layout = page.layout;
                int colcount = 1;
                int maincount = 1;
                LinkedList<String> loopStrings = new LinkedList();
                List<String> areasAdded = new ArrayList();
                if(layout.rows.size() > 0) {
                    LinkedList newRows = new LinkedList();
                    if(layout.sortedRows.size() > 0) {
                        loopStrings = layout.sortedRows;
                    } else {
                        for(RowLayout row : layout.rows) {
                            loopStrings.add(row.rowId);
                        }
                    }
                    for(String rowId : loopStrings) {
                        RowLayout row = findRow(layout,rowId);
                        row.areas = new LinkedList();
                        if(row.numberOfCells == 1) {
                            PageArea area = page.pageAreas.get("main_"+maincount);
                            if(area == null) {
                                System.out.println("this is bullshit" + " main_"+maincount + " page: " + page.id);
                            } else {
                                row.areas.add(area);
                                areasAdded.add(area.type);
                                maincount++;
                            }
                        } else {
                            for(int i = 1;i <= row.numberOfCells; i++) {
                                PageArea area = page.pageAreas.get("col_"+colcount);
                                if(area == null) {
                                    System.out.println("this is wrong");
                                } else {
                                    row.areas.add(area);
                                    areasAdded.add(area.type);
                                    colcount++;
                                }
                            }
                        }
                        newRows.add(row);
                    }
                    
                    layout.footer = page.pageAreas.get("footer");
                    areasAdded.add("footer");
                    layout.header = page.pageAreas.get("header");
                    areasAdded.add("header");
                    
                    for(String added : page.pageAreas.keySet()) {
                        if(areasAdded.contains(added)) {
                            layout.commonPageAreas.add(page.pageAreas.get(added));
                        }
                    }
                    layout.rows = newRows;
                    page.pageAreas = new HashMap();
                    saveObject(page, "PageManager");
                }
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
}
