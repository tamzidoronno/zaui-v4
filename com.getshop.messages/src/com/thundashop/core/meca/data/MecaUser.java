/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.meca.data;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;
import org.mongodb.morphia.annotations.Reference;

/**
 *
 * @author emil
 */
public class MecaUser extends DataCommon {
    
    private String userId;
    private List<Vehicle> vehicles = new ArrayList<Vehicle>();
    @Reference
    private Workshop workshop;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }
    
    
    
}
