/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.certego;

import com.thundashop.core.certego.data.CertegoSystem;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.usermanager.data.Group;
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
    
    public List<CertegoSystem> getSystemsForGroup(Group group);
    
    @Administrator
    public List<CertegoSystem> search(String searchWord);
}
