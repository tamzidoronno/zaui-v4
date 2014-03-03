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
    
    public Page() {
    }

    public void clear() {
        for (PageArea pageArea : pageAreas.values()) {
            pageArea.clear();
        }
    }

    public void moveApplicationUp(String appid) {
        sortApplications();
        for (PageArea pageArea : pageAreas.values()) {
            pageArea.moveApplicationUp(appid);
        }
    }

    public void moveApplicationDown(String appid) {
        sortApplications();
        for (PageArea pageArea : pageAreas.values()) {
            pageArea.moveApplicationDown(appid);
        }
    }

    public PageArea getPageArea(String pageArea) {
        if(!pageAreas.containsKey(pageArea)) {
            PageArea newPageArea = new PageArea(this);
            newPageArea.type = pageArea;
            pageAreas.put(pageArea, newPageArea);
        }
        return pageAreas.get(pageArea);
    }

    public Set<String> getAllPageAreas() {
        return pageAreas.keySet();
    }

    public void moveApplicationToArea(String fromarea, String toarea) {
        if(pageAreas.containsKey(fromarea)) {
            PageArea area = pageAreas.get(fromarea);
            area.type = toarea;
            pageAreas.remove(fromarea);
            pageAreas.put(toarea, area);
        }
    }

    public void removeApplicationOnArea(String area) {
        pageAreas.remove(area);
    }

    public void switchApplications(String fromArea, String toArea) {
        PageArea firstArea = pageAreas.get(fromArea);
        PageArea secondArea = pageAreas.get(toArea);
        
        firstArea.type = toArea;
        secondArea.type = fromArea;
        
        pageAreas.put(toArea, firstArea);
        pageAreas.put(fromArea, secondArea);
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
        PageArea footer = pageAreas.get("footer");
        PageArea header = pageAreas.get("header");
        
        HashMap<String, PageArea> keep = new HashMap();
        for(String key : pageAreas.keySet()) {
            if(key.contains("left_") || key.contains("right_") || key.contains("product")) {
                keep.put(key, pageAreas.get(key));
            }
        }
        
        pageAreas = new HashMap();
        pageAreas.put("footer", footer);
        pageAreas.put("header", header);
        
        for(String key : keep.keySet()) {
            pageAreas.put(key, keep.get(key));
        }
    }

    public List<String> getApplicationIds() {
        List<String> ids = new ArrayList<String>();
        for (PageArea area : pageAreas.values()) {
            ids.addAll(area.applicationsList);
            ids.addAll(area.extraApplicationList.keySet());
        }
        return ids;
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
    
    public Page parent;
    public int pageType =  PageType.Standard;
    //This is actually more a layout type... should be renamed to layouttype
    public int type;
    public String pageTag = "";
    public String pageTagGroup = "";
    public int userLevel = 0;
    public String description = "";
    private HashMap<String, PageArea> pageAreas = new HashMap<String, PageArea>();
    public PageLayout layout = new PageLayout();
    public String title;
    public String customCss = "";
    
    /**
     * This might not be set, only in just a few cases.
     */
    @Transient
    public Entry linkToListEntry;
    
    public void populateApplications(Map<String, AppConfiguration> applications, boolean onlyExtraApplications) {
        for (PageArea pageArea : pageAreas.values()) {
            pageArea.populateApplications(applications, onlyExtraApplications);
        }
    }

    public HashMap<String, AppConfiguration> getApplications() {
        HashMap<String, AppConfiguration> applications = new HashMap();
        for (PageArea pageArea : pageAreas.values()) {
            applications.putAll(pageArea.applications());
        }
        return applications;
    }
    
    public void sortApplications() {
        for (PageArea pageArea : pageAreas.values()) 
            pageArea.sortApplications();
    }
}
