/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.thundashop.core.common.DataCommon;
import java.util.Date;
import java.util.HashMap;
import org.mongodb.morphia.annotations.Transient;

/**
 * A device that is connected to the cloud system.
 * 
 * Examples of devices: PGA, LockSystemBox, Kiosk, Cash AccessPoint, Etc.
* @author ktonder
 */
public class GetShopDevice extends DataCommon {
    public String name ="";
    public String token = "";
    public String type = "";
    public String ip = "";
    
    public HashMap<String, String> extraConfigs = new HashMap();
    
    @Transient
    public Date lastPulledRequest;
    
    public String getConfig(String key) {
        if (extraConfigs.get(key) == null) {
            return "";
        }
        
        return extraConfigs.get(key);
    }
}