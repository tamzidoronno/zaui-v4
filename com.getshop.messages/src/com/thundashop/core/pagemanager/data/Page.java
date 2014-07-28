/**
 * This class is a part of the thundashop project.
 * 
 * All rights reserved 
 *
 **/
package com.thundashop.core.pagemanager.data;

import com.google.code.morphia.annotations.Transient;
import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Translation;
import com.thundashop.core.listmanager.data.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class Page extends DataCommon implements Cloneable {
    public boolean hideHeader = false;
    public boolean hideFooter = false;
    public boolean needSaving = false;
    public boolean beenLoaded = false;
    
    public HashMap<String, PageArea> pageAreas = new HashMap<String, PageArea>();
    public Page parent;
    public int pageType =  PageType.Standard;
    //This is actually more a layout type... should be renamed to layouttype
    public int type;
    public String pageTag = "";
    public String pageTagGroup = "";
    public int userLevel = 0;
    public String description = "";
    public PageLayout layout = new PageLayout();
    @Translation
    public String title;
    public String customCss = "";
    
    public Page() {
    }

    public void clear() {
        for(RowLayout row : layout.rows) {
            for (PageArea pageArea : row.areas) {
                pageArea.clear();
            }
        }
    }

    public void moveApplicationUp(String appid) {
        for(RowLayout row : layout.rows) {
            for (PageArea pageArea : row.areas) {
                pageArea.moveApplicationUp(appid);
            }
        }
    }

    public void moveApplicationDown(String appid) {
        for(RowLayout row : layout.rows) {
            for (PageArea pageArea : row.areas) {   
                pageArea.moveApplicationDown(appid);
            }
        }
    }

    public PageArea getPageArea(String pageArea) throws ErrorException {
        return layout.getPageArea(pageArea);
    }

    public List<String> getAllPageAreas() {
        return layout.getAllAreas();
    }

    public void moveApplicationToArea(String fromarea, String toarea) {
        
    }

    public void removeApplicationOnArea(String area) {
        
    }

    public void switchApplications(String fromArea, String toArea) {
        
    }

    public void finalizePageLayoutRows() {
        for(RowLayout row : layout.rows) {
            if(row.rowId.isEmpty()) {
                row.rowId = UUID.randomUUID().toString();
                needSaving = true;
            }
        }
    }

    public void deletePageAreas() {
        layout.rows = new LinkedList();
    }

    public List<String> getApplicationIds() {
        List<String> ids = new ArrayList<String>();
        
        ids.addAll(layout.getApplicationIds());
        return ids;
    }

    public RowLayout createApplicationRow() {
        RowLayout newRow = new RowLayout();
        newRow.rowId = UUID.randomUUID().toString();
        return newRow;
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
    

    public HashMap<String, AppConfiguration> getApplications() {
        return layout.buildAppConfigurationList();
    }
    
}
