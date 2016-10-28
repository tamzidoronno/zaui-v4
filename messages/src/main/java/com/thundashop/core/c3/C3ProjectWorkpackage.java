/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class C3ProjectWorkpackage {
    public String companyId = "";
    public List<String> hourListIds = new ArrayList();
    
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

    public double getPercentage(String workPackageId, String companyId, int year) {
        if (activeWorkPackaged.get(workPackageId) == null)
            return 0;
        
        if (companyId.equals("cbbb8244-50fb-4e9c-bd2a-4ab21f42e298") && workPackageId.equals("01cf0d97-9440-4f78-8458-c65e40cf319a") ) {
            System.out.println("found");
        }
        
        int total = activeWorkPackaged.values().stream()
                .mapToInt(active -> active.getCost(year))
                .sum();
        
        if (total == 0 && activeWorkPackaged.size() == 1)
            return 100;
       
        if (activeWorkPackaged.get(workPackageId) == null)
            return 0;
        
        if (total == 0 && activeWorkPackaged.size() != 1)
            throw new RuntimeException("Its not possible to calculate a percentage for this as there is no sum set on one project and multiple workpackages");
        
        
        double totalForPackage = activeWorkPackaged.get(workPackageId).getCost(year);
        
        if (totalForPackage == 0)
            return 100;
        
        return (totalForPackage*100 / total); 
        
    }

    void addHours(C3Hour hour) {
        hourListIds.add(hour.id);
    }

    void checkWps(List<String> workPackages) {
        activeWorkPackaged.keySet().removeIf(key -> !workPackages.contains(key));
    }
}
