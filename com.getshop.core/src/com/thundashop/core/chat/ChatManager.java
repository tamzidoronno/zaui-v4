/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.chat;

import com.thundashop.core.chatmanager.ChatMessage;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.usermanager.data.User;
import java.util.Set;
import java.util.TreeSet;
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
    public Set<ChatMessage> messages = new TreeSet();
    
    @Autowired
    public ChatManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void sendMessage(String message) throws ErrorException {
        ChatMessage chatMessage = new ChatMessage();
        User user = this.getSession().currentUser;
        chatMessage.userId = (user == null) ? "guest" : user.id;
        chatMessage.message = message;
        databaseSaver.saveObject(chatMessage, credentials);
        messages.add(chatMessage);
    }

    @Override
    public Set<ChatMessage> getMessages() {
        return messages;
    }
}