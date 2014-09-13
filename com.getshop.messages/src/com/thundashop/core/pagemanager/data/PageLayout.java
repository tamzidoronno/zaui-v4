package com.thundashop.core.pagemanager.data;

import com.thundashop.core.common.AppConfiguration;
import com.thundashop.core.common.ErrorException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PageLayout implements Serializable {
    public int leftSideBar = 0;
    public int marginLeftSideBar = 10;
    public int leftSideBarWidth = 20;
    public int rightSideBarWidth = 20;
    public int rightSideBar = 0;
    public int marginRightSideBar = 10;
    
    //This areas contains the left sidebar areas, and other areas, like product, and special areas.
    public HashMap<String, PageArea> otherAreas = new HashMap<String, PageArea>();
    public LinkedList<String> sortedRows = new LinkedList();
    public LinkedList<RowLayout> rows = new LinkedList();

    public List<String> getAllApplications() {
        List<String> allApps = new ArrayList();
        for(RowLayout row : rows) {
            for(PageArea area : row.areas) {
                allApps.addAll(getApps(area));
            }
        }
        for(PageArea area : otherAreas.values()) {
            allApps.addAll(getApps(area));
        }
        return allApps;
    }

    private List<String> getApps(PageArea area) {
        List<String> mhm = new ArrayList();
        mhm.addAll(area.applicationsList);
        mhm.addAll(area.extraApplicationList.keySet());
        return mhm;
    }

    public HashMap<String, AppConfiguration> buildAppConfigurationList() {
        HashMap<String, AppConfiguration> applications = new HashMap();
        for(RowLayout row : rows) {
            for (PageArea pageArea : row.areas) {
                applications.putAll(pageArea.applications());
                applications.putAll(pageArea.bottomApplications());
            }
        }
        
        for(PageArea area : otherAreas.values()) {
            applications.putAll(area.applications());
            applications.putAll(area.bottomApplications());
        }
        
        return applications;
    }

    public void populateApplications(Map<String, AppConfiguration> applications, boolean onlyExtraApplications) {
        for(RowLayout row : rows) {
            for (PageArea pageArea : row.areas) {
                pageArea.populateApplications(applications, onlyExtraApplications);
            }
        }
        
        for(PageArea area : otherAreas.values()) {
            if (area == null || area.type == null) {
                continue;
            }
            
            if(area.type.equals("header") || area.type.equals("footer")) {
                area.populateApplications(applications, false);
            } else {
                area.populateApplications(applications, onlyExtraApplications);
            }
        }
        
    }

    public List<String> getApplicationIds() {
        List<String> ids = new ArrayList();
        
        for(RowLayout row : rows) {
            for (PageArea area : row.areas) {
                ids.addAll(area.applicationsList);
                ids.addAll(area.extraApplicationList.keySet());
                ids.add(area.bottomLeftApplicationId);
                ids.add(area.bottomMiddleApplicationId);
                ids.add(area.bottomRightApplicationId);
            }
        }
            
        for(PageArea area : otherAreas.values()) {
            ids.addAll(area.applicationsList);
            ids.addAll(area.extraApplicationList.keySet());
        }
        return ids;
    }

    PageArea getPageArea(String pageArea) throws ErrorException {
  
        for(RowLayout row : rows) {
            for (PageArea area : row.areas) {
                if(area.type.equals(pageArea)) {
                    return area;
                }
            }
        }
        
        if(otherAreas.containsKey(pageArea)) {
            return otherAreas.get(pageArea);
        }
	
        PageArea area = new PageArea();
        area.type = pageArea;
        
        otherAreas.put(pageArea, area);
        
        return area;
    }

    List<String> getAllAreas() {
        List<String> areas = new ArrayList();
        for(RowLayout row : rows) {
            for (PageArea area : row.areas) {
                areas.add(area.type);
            }
        }
        for(PageArea area : otherAreas.values()) {
            areas.add(area.type);
        }
        return areas;
    }
}
