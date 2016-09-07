/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.c3;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
interface IC3Manager {
    @Administrator
    public void saveWorkPackage(WorkPackage workPackage);
    
    @Administrator
    public void deleteWorkPackage(String workPackageId);
    
    public List<WorkPackage> getWorkPackages();
    
    public WorkPackage getWorkPackage(String id);
    
    @Administrator
    public void saveProject(C3Project project);
    
    @Administrator
    public void deleteProject(String projectId);
    
    public List<C3Project> getProjects();
    
    public C3Project getProject(String id);
    
}
