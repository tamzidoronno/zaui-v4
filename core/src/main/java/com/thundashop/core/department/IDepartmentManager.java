/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.department;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IDepartmentManager {
    @Administrator
    public void createDepartment(String departmentName);
    
    @Administrator
    public void deleteDepartment(String departmentId);
    
    @Administrator
    public List<Department> getAllDepartments();
    
    @Administrator
    public void saveDepartment(Department department);
    
    @Administrator
    public Department getDepartment(String departmentId);
}
