/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class C3ProjectWorkpackage {
    public String companyId = "";
    public List<String> hourListIds = new ArrayList();
    public List<String> projectPeriodeList = new ArrayList();
    
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

    void setProjectCost(String workPackageId, Date start, Date end, int price, String contractId) {
        CompanyProjectWorkPackageSettings wp = activeWorkPackaged.get(workPackageId);
        if (wp != null)
            wp.setProjectPrice(start, end, price, contractId);
    }
    
    void removeContract(String workPackageId, String contractId) {
        CompanyProjectWorkPackageSettings wp = activeWorkPackaged.get(workPackageId);
        if (wp != null)
            wp.removeContract(contractId);
    }

    public double getPercentage(String workPackageId, String companyId, Date start, Date end, C3Project project) {
        if (activeWorkPackaged.get(workPackageId) == null)
            return 0;
        
        int total = activeWorkPackaged.values().stream()
                .mapToInt(active -> active.getCost(start, end))
                .sum();
        
        if (total == 0 && activeWorkPackaged.size() == 1)
            return 100;
       
        if (activeWorkPackaged.get(workPackageId) == null)
            return 0;
        
        if (total == 0 && activeWorkPackaged.size() != 1)
            throw new RuntimeException(getErrorMessage(activeWorkPackaged, companyId, project));
        
        
        double totalForPackage = activeWorkPackaged.get(workPackageId).getCost(start, end);
        
        if (totalForPackage == 0)
            return 100;
        
        return (totalForPackage*100 / total); 
        
    }

    void addHours(C3Hour hour) {
        hourListIds.add(hour.id);
    }
    
    void addUserProjectPeriode(C3UserProjectPeriode periode) {
        projectPeriodeList.add(periode.id);
    }

    void checkWps(List<String> workPackages) {
        activeWorkPackaged.keySet().removeIf(key -> !workPackages.contains(key));
    }

    private String getErrorMessage(HashMap<String, CompanyProjectWorkPackageSettings> activeWorkPackaged, String companyId, C3Project project) {
        String errorMessage = "Its not possible to get the percentage for the project: " + project.name + ", workpackages: ";
        for (String test : activeWorkPackaged.keySet()) {
            errorMessage += "\n wpid: " + test;
        }
        
        errorMessage += "\ncompanyid: " + companyId;
        
        return errorMessage;
        
    }


    public void removeCost(String costId) {
        hourListIds.remove(costId);
        projectPeriodeList.remove(costId);
    }    
}
