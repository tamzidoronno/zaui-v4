/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.apacmanager;

import com.thundashop.core.arx.Door;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.DataCommon;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class ApacAccess extends DataCommon {
    public String deviceId;
    
    @Administrator
    public String code = "";
    
    public String name;
    public String phoneNumber;
    public String prefix;
    public Integer slot;
    
    @Transient
    public Door door;
}
