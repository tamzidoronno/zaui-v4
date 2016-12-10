/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class C3Project extends DataCommon {

    public String name = "";
    public String projectNumber = "";
    public String projectOwner = "";
    public List<String> workPackages = new ArrayList();
   
    @Transient
    public C3ProjectPeriode currentProjectPeriode;
    
    /**
     * Key = companyid
     * Value = Project
     */
    private HashMap<String, C3ProjectWorkpackage> activatedCompanies = new HashMap();

    void addCompany(String companyId) {
        if (activatedCompanies.containsKey(companyId))
            return;
        
        C3ProjectWorkpackage activated = new C3ProjectWorkpackage();
        activated.companyId = companyId;
        
        activatedCompanies.put(companyId, activated);
    }

    boolean isCompanyActivated(String compnayId) {
        return activatedCompanies.containsKey(compnayId);
    }

    void setCompanyAccess(String companyId, String workPackageId, boolean value) {
        C3ProjectWorkpackage wp = activatedCompanies.get(companyId);
        if (wp != null)
            wp.setCompanyAccess(workPackageId, value);
    }

    void removeCompanyAccess(String companyId) {
        activatedCompanies.remove(companyId);
    }

    void setProjectCost(String companyId, String workPackageId, Date start, Date end, int price, String contractId) {
        C3ProjectWorkpackage wp = activatedCompanies.get(companyId);
        if (wp != null)
            wp.setProjectCost(workPackageId, start, end, price, contractId);
    }
    
    void removeContract(String companyId, String workPackageId, String contractId) {
        C3ProjectWorkpackage wp = activatedCompanies.get(companyId);
        if (wp != null)
            wp.removeContract(workPackageId, contractId);
    }

    public List<String> getWorkPackagesForUser(User user) {
        if (user.companyObject == null)
            return new ArrayList();
        
        if (activatedCompanies.get(user.companyObject.id) == null)
            return new ArrayList();
        
        return new ArrayList(activatedCompanies.get(user.companyObject.id).activeWorkPackaged.keySet());
    }

    public double getPercentage(String workPackageId, String companyId, Date date) {
        C3ProjectWorkpackage wp = activatedCompanies.get(companyId);
        if (wp == null)
            return 0;
        
        return wp.getPercentage(workPackageId, companyId, date, this);
    }

    public void addHour(String companyId, C3Hour hour) {
        activatedCompanies.get(companyId).addHours(hour);
    }
    
    public void addUserProjectPeriode(String companyId, C3UserProjectPeriode periode) {
        activatedCompanies.get(companyId).addUserProjectPeriode(periode);
    }

    public List<String> getCompanyIds() {
        return new ArrayList(activatedCompanies.keySet());
    }
    
    public void finalize() {
        activatedCompanies.values().stream()
                .forEach(c3prowp -> c3prowp.checkWps(workPackages));
    }
    
    static Comparator<? super C3Project> comperatorByProjectNumber() {
        
        return (C3Project o1, C3Project o2) -> {
            
            {
                Integer projectnumber1 = 0;
                Integer projectnumber2 = 0;

                try {
                    projectnumber1 = Integer.parseInt(o1.projectNumber);
                } catch (Exception ex) {
                    return -1;
                }

                try {
                    projectnumber2 = Integer.parseInt(o2.projectNumber);
                } catch (Exception ex) {
                    return 1;
                }

                return projectnumber1.compareTo(projectnumber2);
            }

        };
    }

    public void removeCost(String costId) {
        activatedCompanies.values().stream().forEach(test -> test.removeCost(costId));
        
    }
}