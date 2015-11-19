/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshop;

import com.getshop.pullserver.GetShopApiPullserver;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;
import com.getshop.pullserver.PullMessage;

/**
 *
 * @author ktonder
 */
@Component
public class GetShopPullService {

    private String serverAddress = "pullserver.getshop.com";
    private String sessionId = UUID.randomUUID().toString();
    
    public List<PullMessage> getMessages(String key, String storeId) throws Exception {
        GetShopApiPullserver pullserver = new GetShopApiPullserver(25554, serverAddress, sessionId, "pullserver.getshop.com");
        return pullserver.getPullServerManager().getPullMessages(key, storeId);
    }
    
    public void markMessageAsReceived(String messageId, String storeId) throws Exception {
        GetShopApiPullserver pullserver = new GetShopApiPullserver(25554, serverAddress, sessionId, "pullserver.getshop.com");
        pullserver.getPullServerManager().markMessageAsReceived(messageId, storeId);
    }
}
