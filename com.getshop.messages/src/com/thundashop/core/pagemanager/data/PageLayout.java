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
    
    public PageArea header;
    public PageArea footer;
    
    public List<PageArea> commonPageAreas = new ArrayList();
    
    public LinkedList<PageArea> leftSideBarAreas = new LinkedList();
    public LinkedList<PageArea> rightSideBarAreas = new LinkedList();
    public LinkedList<String> sortedRows = new LinkedList();
    public LinkedList<RowLayout> rows = new LinkedList();

    public List<String> getAllApplications() {
        List<String> allApps = new ArrayList();
        allApps.addAll(getApps(header));
        allApps.addAll(getApps(footer));
        for(RowLayout row : rows) {
            for(PageArea area : row.areas) {
                allApps.addAll(getApps(area));
            }
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
        
        for(PageArea area : leftSideBarAreas) {
            applications.putAll(area.applications());
        }
        for(PageArea area : rightSideBarAreas) {
            applications.putAll(area.applications());
        }
        
        applications.putAll(header.applications());
        applications.putAll(footer.applications());        
        return applications;
    }

    public void populateApplications(Map<String, AppConfiguration> applications, boolean onlyExtraApplications) {
        for(RowLayout row : rows) {
            for (PageArea pageArea : row.areas) {
                pageArea.populateApplications(applications, onlyExtraApplications);
            }
        }
        
        for(PageArea area : leftSideBarAreas) {
            area.populateApplications(applications, onlyExtraApplications);
        }
        for(PageArea area : rightSideBarAreas) {
            area.populateApplications(applications, onlyExtraApplications);
        }
        
        header.populateApplications(applications, onlyExtraApplications);
        footer.populateApplications(applications, onlyExtraApplications);
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
        
        if(footer != null)
            ids.addAll(footer.applicationsList);
            
        if(header != null)
            ids.addAll(header.applicationsList);
        
        for(PageArea area : leftSideBarAreas) {
            ids.addAll(area.applicationsList);
        }
        for(PageArea area : rightSideBarAreas) {
            ids.addAll(area.applicationsList);
        }
        return ids;
    }

    PageArea getPageArea(String pageArea) throws ErrorException {
        if(pageArea.equals("header")) {
            return header;
        }
        if(pageArea.equals("footer")) {
            return footer;
        }
        
        for(RowLayout row : rows) {
            for (PageArea area : row.areas) {
                if(area.type.equals(pageArea)) {
                    return area;
                }
            }
        }
        
        for(PageArea area : leftSideBarAreas) {
            if(area.type.equals(pageArea)) {
                return area;
            }
        }
        
        for(PageArea area : rightSideBarAreas) {
            if(area.type.equals(pageArea)) {
                return area;
            }
        }
        
        throw new ErrorException(1028);
    }

    List<String> getAllAreas() {
        List<String> areas = new ArrayList();
        for(RowLayout row : rows) {
            for (PageArea area : row.areas) {
                areas.add(area.type);
            }
        }
        for(PageArea area : leftSideBarAreas) {
            areas.add(area.type);
        }
        for(PageArea area : rightSideBarAreas) {
            areas.add(area.type);
        }
        
        areas.add("header");
        areas.add("footer");
        return areas;
    }
}
