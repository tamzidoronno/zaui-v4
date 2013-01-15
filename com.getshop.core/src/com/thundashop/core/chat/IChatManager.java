/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.chat;

import com.thundashop.core.chatmanager.ChatMessage;
import com.thundashop.core.chatmanager.Chatter;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IChatManager {
    /**
     * Send a message to administrator
     * 
     * @param message
     * @throws ErrorException 
     */
    public void sendMessage(String message) throws ErrorException;
    
    /**
     * Get all your chat messages
     * 
     * @return 
     */
    public List<ChatMessage> getMessages();
    
    
    /**
     * Returns a list of available poeple to chat with.
     * @return 
     */
    @Administrator
    public List<Chatter> getChatters();
    
    /**
     * Reply to a chat message.
     * 
     * @param id - Chatters id
     * @param message
     */
    @Administrator
    public void replyToChat(String id, String message) throws ErrorException;
    
    @Administrator
    public void closeChat(String id) throws ErrorException;
}
