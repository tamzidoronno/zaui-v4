/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.pagemanager.data.Page;
import com.thundashop.core.pagemanager.data.PageCell;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author boggi
 */
public class UpgradeSlider extends UpgradeBase {
    public static void main(String[] args) throws UnknownHostException {
        UpgradeSlider up = new UpgradeSlider();
        up.start();
    }

    void start() throws UnknownHostException {
        HashMap<String, HashMap<String, Page>> pages = getAllPages();
        for(String test : pages.keySet()) {
            HashMap<String, Page> collection = pages.get(test);
            for(String storekey : collection.keySet()) {
                Page page = collection.get(storekey);
                if(updateSliders(page)) {
                    saveObject(page, "PageManager");
                }
            }
            System.out.println(test);
        }
    }

    private boolean updateSliders(Page page) {
        boolean modified = false;
        ArrayList<PageCell> flatlist = page.layout.getCellsFlatList();
        if(flatlist.size() > 0) {
            System.out.println(flatlist.size());
            for(PageCell cell : flatlist) {
                if(cell.isRotating()) {
                    for(PageCell innercell : cell.cells) {
                        if(!innercell.hideOnMobile && !innercell.hideOnDesktop) {
                            innercell.hideOnMobile = true;
                            modified = true;
                        }
                    }
                }
            }
        }
        return modified;
    }
}
