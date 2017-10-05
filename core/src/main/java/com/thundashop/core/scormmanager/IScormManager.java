/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.scormmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IScormManager {
    
    @Administrator
    public void saveSetup(ScormPackage scormPackage);
    
    @Customer
    public List<Scorm> getMyScorm(String userId);
    
    @Administrator
    public List<ScormPackage> getAllPackages();
    
    @Administrator
    public void updateResult(ScormResult scorm);
    
    @Administrator
    public void deleteScormPackage(String packageId);
    
    public ScormPackage getPackage(String packageId);
    
    @Customer
    public Scorm getScormForCurrentUser(String scormId, String userId);
    
    @Administrator
    public void saveScormCertificateContent(ScormCertificateContent content);
    
    public ScormCertificateContent getScormCertificateContent(String id);
    
    public List<ScormPackage> getMandatoryPackages(String userId);
    
    @Administrator
    public void syncMoodle();
    
    @Administrator
    public boolean needUpdate(String username, String scormid, boolean isCompleted, boolean isPassed, boolean isFailed);
}
