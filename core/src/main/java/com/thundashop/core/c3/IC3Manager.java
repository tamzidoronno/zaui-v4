/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
interface IC3Manager {
    @Administrator
    public WorkPackage saveWorkPackage(WorkPackage workPackage);
    
    @Administrator
    public void deleteWorkPackage(String workPackageId);
    
    public List<WorkPackage> getWorkPackages();
    
    public WorkPackage getWorkPackage(String id);
    
    @Administrator
    public C3Project saveProject(C3Project project);
    
    @Administrator
    public void deleteProject(String projectId);
    
    public List<C3Project> getProjects();
    
    public C3Project getProject(String id);

    @Administrator
    public List<C3Project> search(String searchText);
    
    @Administrator
    public void assignProjectToCompany(String companyId, String projectId);
    
    @Administrator
    public List<C3Project> getAllProjectsConnectedToCompany(String compnayId);
    
    @Administrator
    public void removeCompanyAccess(String projectId, String companyId);
    
    @Administrator
    public void setProjectAccess(String companyId, String projectId, String workPackageId, boolean value);
    
    @Administrator
    public void setProjectCust(String companyId, String projectId, String workPackageId, int year, int price);

    public List<UserProjectAccess> getAccessList();
    
    public int getPercentage(String companyId, String workPackageId, String projectId, int year);
    
    @Administrator
    public void saveGroupInfo(String groupId, String type, boolean value);

    @Administrator
    public void addTimeRate(String name, int rate);
            
    @Administrator
    public void deleteTimeRate(String id);
            
    public C3GroupInformation getGroupInformation(String groupId);
    
    public void addHour(C3Hour hour);
    
    public UserProjectAccess getAccessListByProjectId(String projectId);
    
    public List<C3TimeRate> getTimeRates();
    
    public C3Hour getHourById(String hourId);
    
    @Administrator
    public void saveRate(C3TimeRate rate);
    
    @Administrator
    public void setC3RoundSum(int year, int sum);
    
    public C3RoundSum getRoundSum(int year);
    
    @Administrator
    public void setRateToUser(String userId, String rateId);
    
    public C3TimeRate getTimeRate(String userId);
    
    @Administrator
    public void savePeriode(C3ProjectPeriode periode);
    
    public List<C3ProjectPeriode> getPeriodes();
    
    public C3ProjectPeriode getActivePeriode();
    
    @Administrator
    public void setActivePeriode(String periodeId);
    
    public String canAdd(ProjectCost hour);
    
    public List<C3ProjectPeriode> getPeriodesForProject(String projectId);
    
    public C3OtherCosts saveOtherCosts(C3OtherCosts otherCost);
    
    public List<ProjectCost> getProjectCostsForCurrentUser(String projectId, Date from, Date to);
    
    public C3OtherCosts getOtherCost(String otherCostId);
    
    public List<UserProjectAccess> getAcceListForUser(String userId);
    
    @Administrator
    public void addForskningsUserPeriode(C3ForskningsUserPeriode periode);
    
    @Administrator
    public List<C3ForskningsUserPeriode> getForskningsPeriodesForUser(String userId);
    
    @Administrator
    public void deleteForskningsUserPeriode(String periodeId);
}