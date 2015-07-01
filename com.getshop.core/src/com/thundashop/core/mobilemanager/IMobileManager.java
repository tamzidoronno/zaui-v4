/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.Writing;
import com.thundashop.core.mobilemanager.data.Token;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IMobileManager {
    /**
     * Register a token to the system.
     * This token is later on used for sending messages
     * back to the unit.
     * s
     * @param token
     */
    public void registerToken(Token token) throws ErrorException;
    
    /**
     * Sends the message to all registered units.
     * 
     * @param message 
     */
    @Administrator
    @Writing
    public void sendMessageToAll(String message);
    
    /**
     * Sends the message to all units that are registered as test units.
     * 
     * @param message 
     */
    public void sendMessageToAllTestUnits(String message);
    
    /**
     * Clears the badged number.
     * 
     * @param tokenId 
     */
    public void clearBadged(String tokenId);
    
    public void registerTokenToUserId(String tokenId) throws ErrorException;
}
