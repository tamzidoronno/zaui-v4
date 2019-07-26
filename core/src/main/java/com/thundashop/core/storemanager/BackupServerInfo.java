/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.storemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.getshoplocksystem.LockServerBase;
import com.thundashop.core.gsd.GetShopDevice;

/**
 *
 * @author boggi
 */
public class BackupServerInfo {
    public String id = "";
    public String ip = "";
    public String name = "";
    public String zwayusername = "";
    public String zwaypassword = "";
    public String wifiusername = "";
    public String wifipassword = "";
    public String token = "";
    public String webaddr = "";
    public String storeId = "";

    BackupServerInfo() {
        
    }

    void setData(LockServerBase base) {
        id = base.id;
        ip = base.hostname;
        name = base.givenName;
        zwayusername = base.username;
        zwaypassword = base.password;
        token = base.token;
    }

    void setData(GetShopDevice dev) {
        name = dev.name;
        token = dev.token;
        ip = dev.ip;
        id = dev.id;
    }
}
