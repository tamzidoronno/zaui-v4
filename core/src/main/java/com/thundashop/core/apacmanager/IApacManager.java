package com.thundashop.core.apacmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IApacManager {
    
    @Administrator
    public ApacAccess grantAccess(ApacAccess apacAccess) throws Exception;
    
    @Administrator
    public List<ApacAccess> getAccessList();
    
    @Administrator
    public void removeAccess(String accessId);
    
    @Administrator
    public ApacAccess getApacAccess(String accessId);
    
    @Administrator
    public void sendSms(String accessId, String message);
}