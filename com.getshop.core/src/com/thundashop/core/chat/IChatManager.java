/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.chat;

import com.thundashop.core.chatmanager.ChatMessage;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;
import java.util.List;
import java.util.Set;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IChatManager {
    public void sendMessage(String message) throws ErrorException;
    public List<ChatMessage> getMessages();
}
