package com.thundashop.core.pagemanager.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
}
