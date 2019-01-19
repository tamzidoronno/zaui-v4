/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.department;

import com.getshop.scope.GetShopSession;
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
@GetShopSession
@Component
public class DepartmentManager extends ManagerBase implements IDepartmentManager {
    public HashMap<String, Department> departments = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream().forEach(da -> {
            if (da instanceof Department) {
                departments.put(da.id, (Department)da);
            }
            
            if (da instanceof Department) {
                departments.put(da.id, (Department)da);
            }
        });
    }
    
    
    @Override
    public void createDepartment(String departmentName) {
        Department department = new Department();
        department.name = departmentName;
        saveObject(department);
        departments.put(department.id, department);
    }

    @Override
    public void deleteDepartment(String departmentId) {
        Department dep = departments.remove(departmentId);
        if (dep != null) {
            deleteObject(dep);
        }
    }

    @Override
    public List<Department> getAllDepartments() {
        return new ArrayList(departments.values());
    }

    @Override
    public void saveDepartment(Department department) {
        saveObject(department);
        departments.put(department.id, department);
    }

    @Override
    public Department getDepartment(String departmentId) {
        return departments.get(departmentId);
    }
    
}
