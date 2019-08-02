/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.Customer;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Internal;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGdsManager {
    @Administrator
    public void saveDevice(GetShopDevice device);
    
    @Administrator
    public GetShopDevice getDevice(String deviceId);
    
    @Administrator
    public void deleteDevice(String deviceId);
    
    @ForceAsync
    public List<GetShopDeviceMessage> getMessages(String tokenId);
    
    @Customer
    @ForceAsync
    public List<Serializable> getMessageForUser();
    
    @Internal
    public void sendMessageToDevice(String deviceId, GetShopDeviceMessage message);
    
    @Administrator
    public List<DeviceMessageQueue> getQueues();
    
    @Administrator
    public List<GetShopDevice> getDevices();
    
    public void setGdsConfig(String tokenId, String key, String config);
    
    @Administrator
    public List<GetShopDeviceUnit> getAllUnits(GetShopDeviceUnitFilter filter);
    
        
    public IotDeviceInformation getIotDeviceInformation(IotDeviceInformation information);
    
    @Administrator
    public List<IotDeviceInformation> getAllNewIotDevices();
    
    @Administrator
    public void updateIotDevice(IotDeviceInformation device);

}
