/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author emil
 */
@GetShopApi
public interface IMecaManager {
    

    @Editor
    public MecaFleet createFleet(MecaFleet fleet);
    
    @Editor
    public List<MecaFleet> getFleets();
    
    @Customer
    public MecaFleet getFleetPageId(String pageId);
    
    @Editor
    public void saveFleetCar(String pageId, MecaCar car);
    
    @Customer
    public List<MecaCar> getCarsForMecaFleet(String pageId);
    
    @Editor
    public void deleteCar(String carId);
    
    @Customer
    public MecaCar getCarByPageId(String pageId);
}