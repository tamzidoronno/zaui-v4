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
    public List<Scorm> getMyScorm();
    
    @Administrator
    public List<ScormPackage> getAllPackages();
    
    @Administrator
    public void updateResult(ScormResult inResult);
    
    @Administrator
    public void deleteScormPackage(String packageId);
}
