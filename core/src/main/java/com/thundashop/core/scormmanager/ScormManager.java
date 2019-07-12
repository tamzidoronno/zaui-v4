/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ScormManager extends ManagerBase implements IScormManager {
    private HashMap<String, Scorm> scorms = new HashMap();
    private HashMap<String, Scorm> cachedMap = new HashMap();
    private HashMap<String, ScormPackage> packages = new HashMap();
    private HashMap<String, ScormCertificateContent> certificateContents = new HashMap();
    
    @Autowired
    private UserManager userManager;

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
                cachedMap.put(scorm.scormId+"_"+scorm.userId, scorm);
            }
            
            if (data instanceof ScormCertificateContent) {
                ScormCertificateContent content = (ScormCertificateContent)data;
                certificateContents.put(content.id, content);
            }
        }
        
        if (scorms.size() > 0) {
            createScheduler("checkScormResults", "* * * * *", FetchScormResult.class);
        }
    }
    
    
    @Override
    public void saveSetup(ScormPackage scormPackage) {
        saveObject(scormPackage);
        packages.put(scormPackage.id, scormPackage);
        createScheduler("checkScormResults", "* * * * *", FetchScormResult.class);
    }
    
    @Override
    public List<Scorm> getMyScorm(String userId) {
        User user = null;
        if (userId == null) {
            user = getSession().currentUser;
        } else {
            user = userManager.getUserById(userId);
        }
        
        userManager.checkUserAccess(user);
        gsTiming("After checked access");
        
        List<Scorm> retList = new ArrayList();
        
        
        if (user.companyObject == null || user.companyObject.groupId == null || user.companyObject.groupId.isEmpty()) {
            return new ArrayList();
        }
        
        
        gsTiming("Starting checking of packages");
        for (ScormPackage scormPackage : packages.values()) {
            if (scormPackage.isGroupActive(user.companyObject.groupId) || alreadyCompletedOrStartedTest(scormPackage, userId)) {
                gsTiming("Before getting scorm package.");
                Scorm scorm = getScorm(user.id, scormPackage.id);
                gsTiming("Before finalize.");
                finalizeScorm(scorm);
                gsTiming("After finalize.");
                retList.add(scorm);
                
            }
        }
        
        gsTiming("Checking completed.");
        
        
        return retList;   
    }
    
    @Override
    public Scorm getScormForCurrentUser(String scormId, String userId) {
        User user = getSession().currentUser;
        if (user == null)
            throw new ErrorException(26);
        
        if (userId != null) {
            user = userManager.getUserById(userId);
            userManager.checkUserAccess(user);
        }
        
        if (user == null)
            return null;
        
        Scorm scorm= getScorm(user.id, scormId);
        
        updateDateOnGroup(scorm);
        
        finalizeScorm(scorm);
        return scorm;
    }

    private Scorm getScorm(String userId, String scormId) {
        Scorm scorm = new Scorm();
        
        scorm.userId = userId;
        scorm.scormId = scormId;
        
        Scorm cached = cachedMap.get(scormId+"_"+userId);
        if (cached != null) {
            return cached;
        }
        
        return scorm;
    }

    private void finalizeScorm(Scorm scorm) {
        ScormPackage scormPackage = getPackage(scorm.scormId);
        
        if (scormPackage == null) {
            scormPackage = createScormPackageFromMoodle(scorm.scormId);
        }
        
        if (scormPackage != null) {
            scorm.scormName = scormPackage.name;
            scorm.groupedScormPackage = !scormPackage.groupedScormPackages.isEmpty();
            scorm.groupedScormPackageIds = scormPackage.groupedScormPackages;
        }
        
        if (scorm.groupedScormPackageIds != null && !scorm.groupedScormPackageIds.isEmpty()) {
            scorm.passed = isAllTestPassed(scorm.userId, scorm.groupedScormPackageIds);
        }
    }

    @Override
    public List<ScormPackage> getAllPackages() {
        return new ArrayList(packages.values());
    }

    @Override
    public boolean needUpdate(String username, String scormid, boolean isCompleted, boolean isPassed, boolean isFailed) {
        Scorm scorm = getScorm(username, scormid);

        if (scorm.completed == isCompleted && scorm.passed == isPassed && isFailed == scorm.failed) {
            return false;
        }
        
        return true;
    }
    
    @Override
    public void updateResult(ScormResult result) {
        Scorm scorm = getScorm(result.username, result.scormid);

        if (scorm.completed == result.isCompleted() && scorm.passed == result.isPassed() && result.isFailed() == scorm.failed) {
            return;
        }

        scorm.completed = result.isCompleted();
        scorm.passed = result.isPassed();
        scorm.failed = result.isFailed();

        if (scorm.passed && scorm.passedDate == null) {
            scorm.passedDate = new Date();
        }

        try {
            scorm.score = Integer.parseInt(result.score);
        } catch (NumberFormatException ex) {
            scorm.score = 0;
        }

        saveObject(scorm);

        updateGroupedScormPackages(scorm, result.username);

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

    @Override
    public ScormPackage getPackage(String packageId) {
        return packages.get(packageId);
    }

    private boolean alreadyCompletedOrStartedTest(ScormPackage scormPackage, String userId) {
        Scorm cached = cachedMap.get(scormPackage.id + "_" + userId);
        if (cached != null) {
            if (!cached.isPartOfOtherGroupScormPackages(packages.values())) {
                return true;
            }
        }
        
        return false;
        
    }

    @Override
    public void saveScormCertificateContent(ScormCertificateContent content) {
        ScormCertificateContent oldd = getScormCertificateContent(content.scormId);
        if (oldd != null) {
            content.id = oldd.id;
        }
        
        saveObject(content);
        certificateContents.put(content.id, content);
    }

    @Override
    public ScormCertificateContent getScormCertificateContent(String id) {
        return certificateContents.values().stream().filter(o -> o.scormId.equals(id)).findFirst().orElse(null);
    }

    @Override
    public List<ScormPackage> getMandatoryPackages(String userId) {
        User user = userManager.getUserById(userId);
        
        List<ScormPackage> mandatory = packages.values()
                .stream()
                .filter(scormPackage -> user != null && user.companyObject != null && scormPackage.isGroupActive(user.companyObject.groupId))
                .filter(pack -> pack.isRequired)
                .collect(Collectors.toList());
        
        return mandatory;
    }
    
    private ArrayList<MoodlePackage> getMoodlePackages() {
        try {
            String url = "http://moodle.getshop.com/mod/scorm/scormlist.php";
            String out = new Scanner(new URL(url).openStream(), "UTF-8").useDelimiter("\\A").next();
            
            Type listType = new TypeToken<ArrayList<MoodlePackage>>(){}.getType();
            
            
            Gson gson = new Gson();
            ArrayList<MoodlePackage> packages = gson.fromJson(out, listType);
            return packages;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ScormManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ScormManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return new ArrayList();
    }

    private ScormPackage createScormPackageFromMoodle(String packageId) {
        
        ArrayList<MoodlePackage> packages = getMoodlePackages(); 
        MoodlePackage moodlePackage = packages.stream().filter(o -> o.id.equals(packageId)).findFirst().orElse(null);

        if (moodlePackage != null) {
            ScormPackage scormPackage = new ScormPackage();
            scormPackage.id = moodlePackage.id;
            scormPackage.name = moodlePackage.name;
            saveObject(scormPackage);
            return scormPackage;
        }

        return null;
    }

    @Override
    public void syncMoodle() {
        ArrayList<MoodlePackage> moodlePackages = getMoodlePackages();
        moodlePackages.stream().forEach(moodlePackage -> {
            ScormPackage scormPackage = getPackage(moodlePackage.id);
            if (scormPackage == null) {
                scormPackage = createScormPackageFromMoodle(moodlePackage.id);
            }
            
            if (scormPackage != null) {
                scormPackage.name = moodlePackage.name;
                saveObject(scormPackage);
            }
        });
    }
    
    private void updateDateOnGroup(Scorm scorm) {
        ScormPackage pck = getPackage(scorm.scormId);
        
        if (scorm.passedDate != null) {
            return;
        }
        
        if(pck == null) {
            return;
        }
        
        if (pck.groupedScormPackages.isEmpty()) {
            return;
        }
        
        List<Scorm> scormsForUser = new ArrayList();
        
        pck.groupedScormPackages.stream().forEach(id -> {
            Scorm subScorm = getScorm(scorm.userId, id);
            scormsForUser.add(subScorm);
        }); 
         
        long passedTestsCount = scormsForUser.stream().filter(s -> s.passed).count();
        
        if (passedTestsCount == scormsForUser.size()) {
            scorm.passedDate = getLastPassedDate(scormsForUser);
            saveObject(scorm);
        }
        
    }

    private void updateGroupedScormPackages(Scorm scorm, String userId) {
        List<ScormPackage> dbs = packages.values().stream()
                .filter(p -> p.groupedScormPackages.contains(scorm.scormId))
                .collect(Collectors.toList());
        
        List<Scorm> passedTests = new ArrayList();
        for (ScormPackage pck : dbs) {
            boolean allPassed = isAllPassed(pck, userId, passedTests);
            
            if (allPassed) {
                Scorm userscorm = getScormForCurrentUser(pck.id, userId);
                if (userscorm == null)
                    continue;
                
                scorm.passed = true;
                
                if (userscorm.passedDate == null) {
                    userscorm.passedDate = getLastPassedDate(passedTests);
                }
                
                saveObject(userscorm);

                scorms.put(userscorm.id, userscorm);
                cachedMap.put(userscorm.scormId+"_"+userscorm.userId, scorm);
            }
        }
    }

    private boolean isAllPassed(ScormPackage pck, String userId, List<Scorm> passedTests) {
        boolean allPassed = true;
        for (String scormId : pck.groupedScormPackages) {
            Scorm userscorm = getScormForCurrentUser(scormId, userId);
            if (userscorm == null) {
                continue;
            }
            if (!userscorm.passed) {
                allPassed = false;
            } else {
                passedTests.add(userscorm);
            }
        }
        return allPassed;
    }

    private Date getLastPassedDate(List<Scorm> passedTests) {
        Date latest = null;
        
        for (Scorm scorm : passedTests) {
            if (latest == null) {
                latest = scorm.passedDate;
                continue;
            }
            
            if (scorm.passedDate.after(latest)) {
                latest = scorm.passedDate;
            }
        }
        
        return latest;
    }

    private boolean isAllTestPassed(String userId, List<String> groupedScormPackageIds) {
        for (String scormPackageId : groupedScormPackageIds ) {
            Scorm scorm = getScorm(userId, scormPackageId);
            if (!scorm.passed) {
                return false;
            }
        }
        
        if (groupedScormPackageIds.isEmpty())
            return false;
        
        return true;
    }
    
}
