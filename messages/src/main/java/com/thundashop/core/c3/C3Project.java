/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
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
    public Date startDate = null;
    public Date endDate = null;
   
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

    void setProjectCost(String companyId, String workPackageId, int year, int price) {
        C3ProjectWorkpackage wp = activatedCompanies.get(companyId);
        if (wp != null)
            wp.setProjectCost(workPackageId, year, price);
    }

    public List<String> getWorkPackagesForUser(User user) {
        if (user.companyObject == null)
            return new ArrayList();
        
        if (activatedCompanies.get(user.companyObject.id) == null)
            return new ArrayList();
        
        return new ArrayList(activatedCompanies.get(user.companyObject.id).activeWorkPackaged.keySet());
    }

    public int getPercentage(String workPackageId, String companyId, int year) {
        C3ProjectWorkpackage wp = activatedCompanies.get(companyId);
        if (wp == null)
            return 100;
        
        return wp.getPercentage(workPackageId, companyId, year);
    }

    public void addHour(String companyId, C3Hour hour) {
        activatedCompanies.get(companyId).addHours(hour);
    }
}
