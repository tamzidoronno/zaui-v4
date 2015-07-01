/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pkk;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.pkkcontrol.PkkControlData;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IPkkControlManager {
    public PkkControlData getPkkControlData(String licensePlate) throws ErrorException;
    public void registerPkkControl(PkkControlData regdata) throws ErrorException;
    
    @Administrator
    public List<PkkControlData> getPkkControls();
    
    @Administrator
    public void removePkkControl(String id) throws ErrorException;
}
