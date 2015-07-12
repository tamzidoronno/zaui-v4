/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.certego;

import com.thundashop.core.certego.data.CertegoSystem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ICertegoManager {
    @Administrator
    public CertegoSystem saveSystem(CertegoSystem system);
    
    @Administrator
    public List<CertegoSystem> getSystems();
    
    @Administrator
    public void deleteSystem(String systemId);
}
