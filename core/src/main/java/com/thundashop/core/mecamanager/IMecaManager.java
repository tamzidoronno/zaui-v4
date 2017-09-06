/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.Administrator;
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
    
    @Customer
    public void saveFleet(MecaFleet fleet);
    
    @Editor
    public MecaCar saveFleetCar(String pageId, MecaCar car);
    
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
    public List<MecaCar> getCarsServiceList(boolean needService);
    
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

    public MecaCar answerControlRequest(String carId, boolean answer);
    
    public void resetServiceInterval(String carId, Date date, int kilometers);
    
    @Editor
    public void requestNextControl(String carId, Date date);
    
    @Editor
    public void markControlAsCompleted(String carId);
    
    @Editor
    public void sendKilometerRequest(String carId);
    
    public MecaCar suggestDate(String carId, Date date);
    
    @Editor
    public void noShowPkk(String carId);
    
    @Editor
    public void noShowService(String carId);
    
    @Administrator
    public void runNotificationCheck();
    
    @Administrator
    public void sendNotificationToStoreOwner();
    
    @Editor
    public void deleteFleet(String fleetId);
    
    @Editor
    public void saveMecaFleetSettings(MecaFleetSettings settings);
    
    @Editor
    public String getBase64ExcelReport(String pageId);
    
    @Editor
    public void setManuallyControlDate(String carId, Date date);
    
    @Editor
    public void setManuallyServiceDate(String carId, Date date);
}