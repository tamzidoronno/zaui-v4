/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.certego.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.usermanager.data.Group;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class CertegoSystem extends DataCommon {
    public String number ;
    public String name;
    public String phoneNumber;
    public String email;
    
    public String groupId;
    
    @Transient
    public Group group;
}
