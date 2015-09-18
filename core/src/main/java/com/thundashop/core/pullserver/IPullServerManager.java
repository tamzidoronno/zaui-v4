package com.thundashop.core.pullserver;


import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.pullserver.data.PullMessage;
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
interface IPullServerManager {
    public void savePullMessage(PullMessage pullMessage);
    
    public List<PullMessage> getPullMessages(String keyId, String storeId);
    
    public void markMessageAsReceived(String messageId, String storeId);
    
}
