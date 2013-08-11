/**
 * This class is a part of the thundashop project.
 * 
 * All rights reserved 
 *
 **/
package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.DataCommon;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author ktonder
 */
public class Page extends DataCommon implements Cloneable {

    public Page() {
        addAllPageAreas();
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
    
    public static class DefaultPages {
        public static String OrderOverviewPageId = "orderoverview";
        public static String CartPage = "cart";
        public static String Home = "home";
        public static String CheckOut = "checkout";
        public static String MyAccount = "myaccount";
        public static String Users = "users";
        public static String Settings = "settings";
        public static String Domain = "domain";
    }

    public void addAllPageAreas() {
        for (Field field : PageArea.Type.class.getFields()) {
            try {
                String type = (String) field.get(null);
                if (this.pageAreas.get(type) == null) {
                    PageArea pageArea = new PageArea(this);
                    pageArea.type = (String)type;
                    this.pageAreas.put(pageArea.type, pageArea);
                }
            } catch (IllegalAccessException ex) {
                ex.printStackTrace();
            } catch (IllegalArgumentException ex) {
                ex.printStackTrace();
            } 
        }
    }
    
    public static class PageType {
        public static int HeaderFooterLeftMiddleRight = 1;
        public static int HeaderLeftMiddleFooter = 2;
        public static int HeaderRightMiddleFooter = 3;
        public static int HeaderMiddleFooter = 4;
    }
    
    public Page parent;
    public int type;
    public int userLevel = 0;
    public String description = "";
    private HashMap<String, PageArea> pageAreas = new HashMap<String, PageArea>();
    
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
