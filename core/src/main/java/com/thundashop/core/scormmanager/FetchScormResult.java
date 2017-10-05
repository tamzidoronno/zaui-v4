/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.getshop.scope.GetShopSchedulerBase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.omg.CORBA.Current;

/**
 *
 * @author ktonder
 */
public class FetchScormResult extends GetShopSchedulerBase {
    public HashMap<String, ScormResult> scorms = new HashMap();
    
    public FetchScormResult(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }
    
    @Override
    public void execute() throws Exception {
        String result = getText("http://moodle.getshop.com/mod/scorm/scormstatus.php");
        
        Type listType = new TypeToken<ArrayList<ScormResult>>(){}.getType();
        Gson gson = new Gson();
        List<ScormResult> resultList = gson.fromJson(result, listType);
        for (ScormResult res : resultList) {
            long time = System.currentTimeMillis();
            
            if (!localCheckNeedUpdate(res)) {
                continue;
            }
            
            boolean needUpdate = getApi().getScormManager().needUpdate(res.username, res.scormid, res.isCompleted(), res.isPassed(), res.isFailed());
            
            if (needUpdate) {
                getApi().getScormManager().updateResult(res);
            }
            
            scorms.put(res.scormid+"_"+res.username, res);
        }
//        
    }
    
    private String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    private boolean localCheckNeedUpdate(ScormResult res) {
        ScormResult inMemory = scorms.get(res.scormid+"_"+res.username);
        if (inMemory == null)
            return true;
        
        if (inMemory.isCompleted() == res.isCompleted() && inMemory.isPassed() == res.isPassed() && inMemory.isFailed() == res.isFailed()) {
            return false;
        }
        
        return true;
    }
}
