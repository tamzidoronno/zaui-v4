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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
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
    public Map<Integer, ChatMessage> messages = new TreeMap();
    
    @Autowired
    public ChatManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @PostConstruct
    public void loadTestData() {
        ChatMessage message = new ChatMessage();
        message.message = "This is a message 1";
        message.rowCreatedDate = new Date();
        message.numericValue = messages.size();
        messages.put(messages.size(), message);
        
        ChatMessage message2 = new ChatMessage();
        message2.message = "This is a message 2";
        message2.rowCreatedDate = new Date(0);
        message2.numericValue = messages.size();
        message2.operator = true;
        messages.put(messages.size(), message2);
        messages.put(messages.size(), message2);
        messages.put(messages.size(), message);
    }

    @Override
    public void sendMessage(String message) throws ErrorException {
        ChatMessage chatMessage = new ChatMessage();
        User user = this.getSession().currentUser;
        chatMessage.userId = (user == null) ? "guest" : user.id;
        chatMessage.message = message;
        chatMessage.storeId = storeId;
        databaseSaver.saveObject(chatMessage, credentials);
        messages.put(messages.size(), chatMessage);
    }

    @Override
    public List<ChatMessage> getMessages() {
        return new ArrayList(messages.values());
    }
}