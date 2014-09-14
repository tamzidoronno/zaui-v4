/**
 * This class is a part of the thundashop project.
 * 
 * All rights reserved 
 *
 **/
package com.thundashop.core.pagemanager.data;

import org.mongodb.morphia.annotations.Transient;
import com.thundashop.core.common.AppConfiguration;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author ktonder
 */
public class PageArea implements Serializable {
    @Transient
    public TreeMap<String, AppConfiguration> applications = new TreeMap<>();
    
    private TreeMap<Integer, String> applicationsSequenceList = new TreeMap();
    
    @Transient
    public Map<String, String> extraApplicationList = new HashMap<>();
    
    public List<String> applicationsList = new ArrayList<>();
    
    
    @Transient
    public TreeMap<String, AppConfiguration> bottomApplications = new TreeMap<>();
    
    public String bottomLeftApplicationId = "";
    public String bottomMiddleApplicationId = "";
    public String bottomRightApplicationId = "";
    
    public String type;
    public String pageId;
    
    public boolean bottomAreaActivated = false;
            
    public PageArea() {
    }

    public PageArea(Page page) {
        pageId = page.id;
    }

    public void populateApplications(Map<String, AppConfiguration> applicationCollection, boolean onlyExtraApplications) {
        applications.clear();
        
        for (String applicationId : applicationsList) {

            AppConfiguration appConfig = applicationCollection.get(applicationId);
            if (appConfig != null) {

                appConfig.originalPageId = pageId;
                if (!onlyExtraApplications || appConfig.appName.equals("LeftMenu")) {
                    applications.put(appConfig.id, appConfig);
                }
            }
        }
        
        AppConfiguration appConfig = applicationCollection.get(bottomLeftApplicationId);
        if (appConfig != null) {
            bottomApplications.put(appConfig.id, appConfig);
        }
        appConfig = applicationCollection.get(bottomRightApplicationId);
        if (appConfig != null) {
            bottomApplications.put(appConfig.id, appConfig);
        }
        appConfig = applicationCollection.get(bottomMiddleApplicationId);
        if (appConfig != null) {
            bottomApplications.put(appConfig.id, appConfig);
        }

        populateExtraApplications(applicationCollection);
    }
    
     private void populateExtraApplications(Map<String, AppConfiguration> applicationCollection) {
        for (String applicationId : extraApplicationList.keySet()) {
            AppConfiguration appConfig = applicationCollection.get(applicationId);
            if (appConfig != null) {
                appConfig.originalPageId = extraApplicationList.get(applicationId);
                applications.put(appConfig.id, appConfig);
            }
        }
     }

    public void clear() {
        extraApplicationList.clear();
        applications.clear();
    }

    void moveApplicationUp(String appid) {
        removeGaps();
        
        boolean skipNext = false;
        
        int i = 0;
        TreeMap<Integer, String> applicationsList2 = new TreeMap();
        for (Integer pos : applicationsSequenceList.keySet()) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
            
            String applicationId = applicationsSequenceList.get(pos);
            String nextApp = applicationsSequenceList.get(pos+1);
            
            if (nextApp != null && nextApp.equals(appid)) {
                if (nextApp != null) {
                    applicationsList2.put(i, nextApp);
                    i++;
                    skipNext=true;
                }
            } 
            
            applicationsList2.put(i, applicationId);
            i++;
        }
     
        applicationsSequenceList = applicationsList2;
    }

    void moveApplicationDown(String appid) {
        removeGaps();
        
        boolean skipNext = false;
        
        int i = 0;
        TreeMap<Integer, String> applicationsList2 = new TreeMap();
        for (Integer pos : applicationsSequenceList.keySet()) {
            if (skipNext) {
                skipNext = false;
                continue;
            }
            
            String applicationId = applicationsSequenceList.get(pos);
            if (applicationId.equals(appid)) {
                String nextApp = applicationsSequenceList.get(pos+1);
                if (nextApp != null) {
                    applicationsList2.put(i, nextApp);
                    i++;
                    skipNext=true;
                }
            } 
            
            applicationsList2.put(i, applicationId);
            i++;
        }
     
        applicationsSequenceList = applicationsList2;
    }

    public void addApplication(AppConfiguration app) {
        applicationsList.add(app.id);
    }

    Map<? extends String, ? extends AppConfiguration> bottomApplications() {
        return (Map<String, AppConfiguration>) bottomApplications.clone();
    }
    
    public void removeFromBottom(String applicationId) {
        if (bottomLeftApplicationId.equals(applicationId)) {
            bottomLeftApplicationId = "";
        }
        if (bottomMiddleApplicationId.equals(applicationId)) {
            bottomMiddleApplicationId = "";
        }
        if (bottomRightApplicationId.equals(applicationId)) {
            bottomRightApplicationId = "";
        }
    }
    
    public static class Type {
        public static String TOP = "header";
        public static String LEFT = "left_1";
        public static String MIDDLE = "main_1";
        public static String RIGHT = "right_1";
        public static String BOTTOM = "footer";
        public static String SUBHEADER = "subheader";
    }
    
    private void addApplicationsWithNoSequence() {
        for (String appId : applications.keySet()) {
            if (!applicationsSequenceList.containsValue(appId)) {
                int size = applicationsSequenceList.size();
                applicationsSequenceList.put(size, appId);
            }
        }
    }
    
    private void removeGaps() {
        int i = 0;
        
        TreeMap<Integer, String> newMap = new TreeMap();
        for (String application : applicationsSequenceList.values()) {
            if (application == null)
                continue;
            
            if (!applications.containsKey(application))
                continue;
            
            newMap.put(i, application);
            i++;
        }
        
        applicationsSequenceList  = newMap;
    }
    
    public void sortApplications() {
        addApplicationsWithNoSequence();
        removeGaps();
        
        for(Integer sequence : applicationsSequenceList.keySet()) {
            String appid = applicationsSequenceList.get(sequence);
            applications.get(appid).sequence = sequence;
        }
    }
    
    public Map<String, AppConfiguration> applications() {
        return (Map<String, AppConfiguration>) applications.clone();
    }
}