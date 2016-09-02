/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.asanamanager;

import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.thundashop.core.applications.StoreApplicationPool;
import com.thundashop.core.appmanager.data.Application;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.start.Runner;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class AsanaManager extends ManagerBase implements IAsanaManager {
//    private String token = "0/6e1f7bac035d585e97bd5df34817f7d6";
    private HashMap<Long, AsanaTask> cachedTasks = new HashMap();
    
    @Autowired
    private StoreApplicationPool storeAppPool;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {        
    }
    
    public static String getText(String url) throws Exception {
        URL myURL = new URL(url);
        HttpURLConnection myURLConnection = (HttpURLConnection)myURL.openConnection();
        myURLConnection.setRequestProperty ("Authorization", "Bearer 0/6e1f7bac035d585e97bd5df34817f7d6");
        myURLConnection.setRequestMethod("POST");
        myURLConnection.setRequestProperty("Content-Type", "application/json");

        myURLConnection.setUseCaches(false);
        myURLConnection.setDoInput(true);
        myURLConnection.setDoOutput(true);
        
        return "";
    }

    @Override
    public List<AsanaTask> getTasks(long projectId, int year, int month) throws Exception {

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, (month));
        cal.set(Calendar.DAY_OF_MONTH, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        Date end = cal.getTime();
        cal.set(Calendar.MONTH, (month-1));
        
        Date start = cal.getTime();
        
        List<AsanaTask> retTasks = new ArrayList();
        List<AsanaTask> tasks = getTasks(projectId);
        
        for (AsanaTask task : tasks) {
            if (task.completed_at != null && task.completed_at.after(start) && task.completed_at.before(end)) {
                retTasks.add(task);
            }   
        }
        
        return retTasks;
    }

    @Override
    public AsanaProjects getProjects() throws UnirestException {
        HttpResponse<AsanaProjects> result = Unirest.get("https://app.asana.com/api/1.0/projects")
                .header("Authorization", "Bearer " + getToken())
                .asObject(AsanaProjects.class);
        AsanaProjects projects = result.getBody();
        return projects;
    }

    private String getToken() {
        Application asanaProjects = storeAppPool.getApplicationWithSecuredSettings("b78a42d9-2713-4c89-bb9e-48c3dd363a40");
        if (asanaProjects != null) {
            return asanaProjects.getSetting("token");
        }
        
        return "";
    }

    private List<AsanaTask> getTasks(long projectId) throws UnirestException {
        HttpResponse<AsanaTasks> result = Unirest.get("https://app.asana.com/api/1.0/projects/"+projectId+"/tasks")
                .header("Authorization", "Bearer " + getToken())
                .asObject(AsanaTasks.class);
     
        List<AsanaTask> retTasks = new ArrayList();
        for (AsanaTask task : result.getBody().data) {
            
            if (cachedTasks.containsKey(task.id)) {
                retTasks.add(cachedTasks.get(task.id));
                continue;
            }
            
            HttpResponse<AsanaTaskWrapper> result2 = Unirest.get("https://app.asana.com/api/1.0/tasks/" + task.id)
                .header("Authorization", "Bearer " + getToken())
                .asObject(AsanaTaskWrapper.class);
            
                AsanaTask taskToAdd = result2.getBody().data;
                taskToAdd.projectId = projectId;
                retTasks.add(taskToAdd);
                
            if (taskToAdd.completed_at != null ) {
                cachedTasks.put(taskToAdd.id, taskToAdd);
            }
        }
        
        return retTasks;
    }
    
}
