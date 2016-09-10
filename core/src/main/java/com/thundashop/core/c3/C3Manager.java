/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class C3Manager extends ManagerBase implements IC3Manager {
    
    public HashMap<String, C3GroupInformation> groupInfos = new HashMap();
    public HashMap<String, WorkPackage> workPackages = new HashMap();
    public HashMap<String, C3Project> projects = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(idata -> addData(idata));
    }
    
    private void addData(DataCommon data) {
        if (data instanceof C3Project)
            projects.put(data.id, ((C3Project)data));
        
        if (data instanceof WorkPackage)
            workPackages.put(data.id, ((WorkPackage)data));
        
        if (data instanceof C3GroupInformation)
            groupInfos.put(data.id, ((C3GroupInformation)data));
    }    
    
    @Override
    public WorkPackage saveWorkPackage(WorkPackage workPackage) {
        saveObject(workPackage);
        workPackages.put(workPackage.id, workPackage);
        finalizeWorkPackage(workPackage);
        return workPackage;
    }

    @Override
    public void deleteWorkPackage(String workPackageId) {
        WorkPackage workPackage = workPackages.remove(workPackageId);
        if (workPackage != null)
            deleteObject(workPackage);
    }

    @Override
    public List<WorkPackage> getWorkPackages() {
        workPackages.values().stream().forEach(wp -> finalizeWorkPackage(wp));
        return new ArrayList(workPackages.values()); 
    }

    @Override
    public WorkPackage getWorkPackage(String id) {
        WorkPackage workPackage = workPackages.get(id);
        if (workPackage == null)
            return null;
        
        finalizeWorkPackage(workPackage);
        
        return workPackage;
    }

    private void finalizeWorkPackage(WorkPackage workPackage) {
        // TODO, finalize?
    }

    @Override
    public C3Project saveProject(C3Project project) {
        saveObject(project);
        projects.put(project.id, project);
        
        finalizeProject(project);
        return project;
    }

    @Override
    public void deleteProject(String projectId) {
        C3Project project = projects.remove(projectId);
        if (project != null)
            deleteObject(project);
    }

    @Override
    public List<C3Project> getProjects() {
        projects.values().stream().forEach(project -> finalizeProject(project));
        return new ArrayList(projects.values());
    }

    @Override
    public C3Project getProject(String id) {
        C3Project project = projects.get(id);
        finalizeProject(project);
        return project;
    }

    private void finalizeProject(C3Project project) {
        
    }

    @Override
    public List<C3Project> search(String searchText) {
        List<C3Project> retProjects = projects.values().stream()
                .filter(project -> project.name.toLowerCase().contains(searchText))
                .collect(Collectors.toList());
        
        retProjects.stream().forEach(project -> finalizeProject(project));
        
        return retProjects;
    }

    @Override
    public void assignProjectToCompany(String companyId, String projectId) {
        C3Project project = projects.get(projectId);
        if (project != null) {
            project.addCompany(companyId);
            saveObject(project);
        }
    }

    @Override
    public List<C3Project> getAllProjectsConnectedToCompany(String compnayId) {
        List<C3Project> retPorjects = projects.values()
                .stream()
                .filter(project -> project.isCompanyActivated(compnayId))
                .collect(Collectors.toList());
        
        retPorjects.forEach(project -> finalizeProject(project));
        
        return retPorjects;
    }

    @Override
    public void setProjectAccess(String companyId, String projectId, String workPackageId, boolean value) {

        C3Project project = getProject(projectId);
        if (project != null) {
            project.setCompanyAccess(companyId, workPackageId, value);
            saveObject(project);
        }
    }

    @Override
    public void removeCompanyAccess(String projectId, String companyId) {
        C3Project project = getProject(projectId);
        if (project != null) {
            project.removeCompanyAccess(companyId);
            saveObject(project);
        }
    }

    @Override
    public void setProjectCust(String companyId, String projectId, String workPackageId, int year, int price) {
        C3Project project = getProject(projectId);
        if (project != null) {
            project.setProjectCost(companyId, workPackageId, year, price);
            saveObject(project);
        }
    }

    @Override
    public List<UserProjectAccess> getAccessList() {
        if (getSession() == null || getSession().currentUser == null)
            return null;
        
        User user = getSession().currentUser;
        
        ArrayList<UserProjectAccess> retList = new ArrayList();
        
        for (C3Project project : projects.values()) {
            if (doesUserHaveAccess(project, user)) {
                UserProjectAccess access = new UserProjectAccess();
                access.projectId = project.id;
                access.workPackageIds = project.getWorkPackagesForUser(user);
                retList.add(access);
            }
        }
        
        return retList;
    }

    private boolean doesUserHaveAccess(C3Project project, User user) {
        if (user.companyObject == null)
            return false;
        
        return project.isCompanyActivated(user.companyObject.id);
    }

    @Override
    public int getPercentage(String companyId, String workPackageId, String projectId, int year) {
        C3Project project = getProject(projectId);
        if (project == null)
            return 100;
        
        return project.getPercentage(workPackageId, companyId, year);
    }

    @Override
    public void saveGroupInfo(String groupId, String type, boolean value) {
        C3GroupInformation info = getGroupInformation(groupId);
        
        if (type.equals("egen")) {
            info.financalEgen = value;
        }
        
        if (type.equals("forskning")) {
            info.financalForskning = value;
        }
        
        saveObject(info);
    }

    @Override
    public C3GroupInformation getGroupInformation(String groupId) {
        C3GroupInformation info = groupInfos.get(groupId);
        if (info == null) {
            info = new C3GroupInformation();
            info.id = groupId;
            saveObject(info);
            groupInfos.put(info.id, info);
        }
        
        return groupInfos.get(groupId);
    }
}
