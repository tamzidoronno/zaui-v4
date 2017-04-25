/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ScormManager extends ManagerBase implements IScormManager {
    private HashMap<String, Scorm> scorms = new HashMap();
    private HashMap<String, ScormPackage> packages = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived datas) {
        for (DataCommon data : datas.data) {
            if (data instanceof ScormPackage) {
                ScormPackage scormPackage = (ScormPackage)data;
                packages.put(scormPackage.id, scormPackage);
            }
            if (data instanceof Scorm) {
                Scorm scorm = (Scorm)data;
                scorms.put(scorm.id, scorm);
            }
        }
        
        if (scorms.size() > 0) {
            createScheduler("checkScormResults", "0 * * * *", FetchScormResult.class);
        }
    }
    
    
    @Override
    public void saveSetup(ScormPackage scormPackage) {
        saveObject(scormPackage);
        packages.put(scormPackage.id, scormPackage);
        createScheduler("checkScormResults", "0 * * * *", FetchScormResult.class);
    }
    
    public List<Scorm> getMyScorm() {
        User user = getSession().currentUser;
        
        List<Scorm> retList = new ArrayList();
        
        
        if (user.companyObject == null || user.companyObject.groupId == null || user.companyObject.groupId.isEmpty()) {
            return new ArrayList();
        }
        
        
        
        for (ScormPackage scormPackage : packages.values()) {
            if (scormPackage.isGroupActive(user.companyObject.groupId)) {
                Scorm scorm = getScorm(user.id, scormPackage.id);
                finalizeScorm(scorm);
                retList.add(scorm);
            }
        }
        
        
        return retList;   
    }

    private Scorm getScorm(String userId, String scormId) {
        Scorm scorm = new Scorm();
        
        scorm.userId = userId;
        scorm.scormId = scormId;
        
        return scorms.values().stream()
                .filter(o -> o.scormId.equals(scormId) && o.userId.equals(userId))
                .findFirst()
                .orElse(scorm);
    }

    private void finalizeScorm(Scorm scorm) {
        ScormPackage scormPackage = packages.get(scorm.scormId);
        scorm.scormName = scormPackage.name;
        scorm.groupedScormPackage = !scormPackage.groupedScormPackages.isEmpty();
        scorm.groupedScormPackageIds = scormPackage.groupedScormPackages;
    }

    @Override
    public List<ScormPackage> getAllPackages() {
        return new ArrayList(packages.values());
    }

    @Override
    public void updateResult(ScormResult result) {
        Scorm scorm = getScorm(result.username, result.scormid);
        scorm.completed = result.isCompleted();
        
        try {
            scorm.score = Integer.parseInt(result.score);
        } catch (NumberFormatException ex) {
            scorm.score = 0;
        }
        
        saveObject(scorm);
    }

    @Override
    public void deleteScormPackage(String packageId) {
        ScormPackage res = packages.remove(packageId);
        if (res != null) {
            List<Scorm> scormsToBeRemoved = scorms.values().stream().filter(o -> o.id.equals(packageId)).collect(Collectors.toList());
            scormsToBeRemoved.stream().forEach(o -> {
                scorms.remove(o);
                deleteObject(o);
            });
            deleteObject(res);
        }
    }
    
}
