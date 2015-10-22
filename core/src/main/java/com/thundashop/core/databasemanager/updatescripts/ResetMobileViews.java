/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.pagemanager.data.Page;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 *
 * @author boggi2
 */
public class ResetMobileViews  extends UpgradeBase {
        public static void main(String[] args) throws UnknownHostException {
        ResetMobileViews up = new ResetMobileViews();
        up.start();
    }

    private void start() throws UnknownHostException {
        HashMap<String, HashMap<String, Page>> pages = getAllPages();
        for(String storeId : pages.keySet()) {
            HashMap<String,Page> storePages = pages.get(storeId);
            for(Page page : storePages.values()) {
                page.layout.resetMobileList();
                System.out.println("Resetting page: " + page.id + " collection: " + storeId);
                saveObject(page, "PageManager");
            }
        }
    }
}
