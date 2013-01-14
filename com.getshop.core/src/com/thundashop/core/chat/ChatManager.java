/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.chat;

import com.thundashop.core.chatmanager.ChatMessage;
import com.thundashop.core.chatmanager.Chatter;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class ChatManager extends ManagerBase implements IChatManager {
    private int guestNumber = 15182; 
    private Map<String, Chatter> chatters = new HashMap();
    
    @Autowired
    public ChatManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
   
    private void increaseGuestNumber() {
        guestNumber++;
        if (guestNumber > 84199102) {
            guestNumber = 15182;
        }
    }
    
    private Chatter getChatter() {
        String sessionId = getSession().id;
        Chatter chatter = chatters.get(sessionId);
        if (chatter == null) {
            User user = getSession().currentUser;
            increaseGuestNumber();
            
            chatter = new Chatter();
            chatter.username = (user == null) ? "guest"+guestNumber : user.fullName;
            chatter.sessionId = sessionId;
            chatter.storeId = storeId;
            chatter.id = UUID.randomUUID().toString();
            chatters.put(sessionId, chatter);
        }
        
        return chatter;
    }
    
    private Chatter getChatterById(String id) {
        for (Chatter chat : chatters.values()) {
            if (chat.id.equals(id)) {
                return chat;
            }
        }
        
        return null;
    }
    
    private ChatMessage createChateMessage(String message) {
        ChatMessage chatMessage = new ChatMessage();
        User user = this.getSession().currentUser;
        chatMessage.message = message;
        chatMessage.operator = (user != null && (user.isAdministrator() || user.isEditor())) ? true : false;
        return chatMessage;
    }
    
    @Override
    public void sendMessage(String message) throws ErrorException {
        Chatter chatter = getChatter();
        ChatMessage chatMessage = createChateMessage(message);
        chatter.messages.add(chatMessage);
        databaseSaver.saveObject(chatter, credentials);
    }

    @Override
    public List<ChatMessage> getMessages() {
        Chatter chatter = getChatter();
        return chatter.messages;
    }

    @Override
    public List<Chatter> getChatters() {
        return new ArrayList(chatters.values());
    }

    @Override
    public void replyToChat(String id, String message) throws ErrorException {
        Chatter chatter = getChatterById(id);
        ChatMessage chatMessage = createChateMessage(message);
        chatter.messages.add(chatMessage);
        databaseSaver.saveObject(chatter, credentials);
    }
}