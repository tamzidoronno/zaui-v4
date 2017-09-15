/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import java.util.Date;

/**
 *
 * @author ktonder
 */
public class GetShopDeviceLog extends DataCommon {
    
    public int deviceId;
    
    /**
     * 2 = open.
     * 10 = close.
     * 27 = usercode changed.
     */
    public int event;
    
    /**
     * User slot Id
     */
    public int uId;
    
    public Date timestamp = new Date();
    
    public String serverSource;

    @Administrator
    public String code;
    
    public String getShopDeviceId;
    
    boolean isSame(GetShopDeviceLog log) {
        return (log.timestamp.equals(timestamp) && uId == log.uId && serverSource.equals(log.serverSource));
    }

    public boolean isOpen() {
        return event == 2;
    }
}
