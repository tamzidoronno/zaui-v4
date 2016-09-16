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
    
    public C3GroupInformation getGroupInformation(String groupId);
    
    public void addHour(C3Hour hour);
    
    public UserProjectAccess getAccessListByProjectId(String projectId);
    
    public List<C3Hour> getHoursForCurrentUser(String projectId, Date from, Date to);
    
    public C3Hour getHourById(String hourId);
}
