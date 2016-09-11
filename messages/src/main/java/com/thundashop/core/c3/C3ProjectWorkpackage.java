/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author ktonder
 */
public class C3ProjectWorkpackage {
    public String companyId = "";
    
    /**
     * Key = workpackageid
     */
    public HashMap<String, CompanyProjectWorkPackageSettings> activeWorkPackaged = new HashMap();

    void setCompanyAccess(String workPackageId, boolean value) {
        if (!value) {
            activeWorkPackaged.remove(workPackageId);
        }
        
        if (value && !activeWorkPackaged.containsKey(workPackageId)) {
            CompanyProjectWorkPackageSettings settings = new CompanyProjectWorkPackageSettings();
            activeWorkPackaged.put(workPackageId, settings);
        }
    }

    void setProjectCost(String workPackageId, int year, int price) {
        CompanyProjectWorkPackageSettings wp = activeWorkPackaged.get(workPackageId);
        if (wp != null)
            wp.setProjectPrice(year, price);
    }

    int getPercentage(String workPackageId, String companyId, int year) {
        if (activeWorkPackaged.get(workPackageId) == null)
            return 100;
        
        int total = activeWorkPackaged.values().stream()
                .mapToInt(active -> active.getCost(year))
                .sum();
        
        if (total == 0)
            return 100;
        
        if (activeWorkPackaged.get(workPackageId) == null)
            return 0;
        
        
        int totalForPackage = activeWorkPackaged.get(workPackageId).getCost(year);
        
        if (totalForPackage == 0)
            return 0;
        
        return (totalForPackage*100 / total); 
        
    }
}
