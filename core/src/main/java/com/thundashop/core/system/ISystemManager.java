/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.system;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface ISystemManager {
    @Administrator
    public GetShopSystem createSystem(String systemName, String companyId);
    
    @Administrator
    public void deleteSystem(String systemId);
    
    @Administrator
    public GetShopSystem getSystem(String systemId);
    
    @Administrator
    public void saveSystem(GetShopSystem system);
    
    @Administrator
    public List<GetShopSystem> getSystemsForCompany(String companyId);
}
