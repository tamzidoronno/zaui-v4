/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.Customer;
import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
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
    
    @Editor
    public MecaCar getCar(String id);
    
    @Editor
    public List<MecaCar> getCarsPKKList();
    
    @Customer
    public MecaCar getCarByPageId(String pageId);
        
    @Editor
    public MecaFleet getFleetByCar(MecaCar car);
    
    @Editor
    public List<MecaCar> getCarsServiceList();
    
    public List<MecaCar> getCarsByCellphone(String cellPhone);
    
    public void callMe(String cellPhone);
    
    public void sendEmail(String cellPhone, String message);
    
    public void sendKilometers(String cellPhone, int kilometers);
    
    public void registerDeviceToCar(String deviceId, String cellPhone);
    
    @Editor
    public void notifyByPush(String phoneNumber, String message);
    
    @Editor
    public void sendInvite(String mecaCarId);
    
    @Editor
    public void requestNextService(String carId, Date date);
    
    public MecaCar answerServiceRequest(String carId, boolean answer);
    
    public void resetServiceInterval(String carId, Date date, int kilometers);
}