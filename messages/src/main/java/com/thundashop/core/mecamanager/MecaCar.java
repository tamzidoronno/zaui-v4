/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mecamanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.pagemanager.data.Page;
import java.util.Date;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class MecaCar extends DataCommon {
    public String licensePlate = "";
    public Date nextControll = null;
    public int kilometers = 0;
    public String cellPhone = "";

    public String pageId;
        
    @Transient
    public Date nextEventDate = new Date();
    

}
