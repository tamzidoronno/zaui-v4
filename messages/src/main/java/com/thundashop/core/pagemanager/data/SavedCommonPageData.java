
package com.thundashop.core.pagemanager.data;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class SavedCommonPageData extends DataCommon {
    public LinkedHashMap<Long, CommonPageData> datas = new LinkedHashMap();
    private HashMap<String, LinkedHashMap<Long, PageLayout>> layoutBackups = new LinkedHashMap();

    public void saveData(CommonPageData data) {
        int i = 0;
        
        List<Long> toRemove = new ArrayList();
        for(Long date : datas.keySet()) {
            if(i > 15) {
                toRemove.add(date);
            }
            i++;
        }
        
        for(Long date : toRemove) {
            datas.remove(date);
        }
        
        datas.put(new Date().getTime(), data);
    }

    public void backupCurrentLayout(String pageId, PageLayout layout) {
        LinkedHashMap<Long, PageLayout> backups = layoutBackups.get(pageId);
        if(backups == null) {
            backups = new LinkedHashMap();
        }
        
        int i = 0;
        List<Long> toRemove = new ArrayList();
        for(Long date : backups.keySet()) {
            if(i > 200) {
                toRemove.add(date);
            }
            i++;
        }
        
        for(Long date : toRemove) {
            backups.remove(date);
        }
        
        PageLayout layoutCopy = copyLayout(layout);
        
        LinkedHashMap<Long, PageLayout> newMap = new LinkedHashMap();
        long time = new Date().getTime();
        newMap.put(time, layoutCopy);
        newMap.putAll(backups);
        layoutBackups.put(pageId, newMap);
    }
    
    
    public CommonPageData getClosestLayout(Long fromTime) {
        long diff = -1;
        long toUse = 0;
        for(Long time : datas.keySet()) {
            long tmpdiff = fromTime - time;
            if(tmpdiff < 0) {
                tmpdiff = tmpdiff * -1;
            }
            if(diff < 0 || tmpdiff < diff) {
                diff = tmpdiff;
                toUse = time;
            }
        }
        
        return datas.get(toUse);
    }

    public PageLayout getSavedLayout(String pageId, Long fromTime) {
        PageLayout layout = layoutBackups.get(pageId).get(fromTime);
        return copyLayout(layout);
    }

    public Collection<? extends Long> getSavedLayouts(String id) {
        LinkedList<Long> result = new LinkedList();
        LinkedHashMap<Long, PageLayout> backups = layoutBackups.get(id);
        if(backups != null) {
            result.addAll(backups.keySet());
        }
        
        return result;
    }

    private PageLayout copyLayout(PageLayout layout) {
        Gson gson = new Gson();
        String text = gson.toJson(layout);
        return gson.fromJson(text, PageLayout.class);
    }
}
