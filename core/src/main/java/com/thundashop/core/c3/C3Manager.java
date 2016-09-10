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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class C3Manager extends ManagerBase implements IC3Manager {

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
    }    
    
    @Override
    public void saveWorkPackage(WorkPackage workPackage) {
        saveObject(workPackage);
        workPackages.put(workPackage.id, workPackage);
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
}
