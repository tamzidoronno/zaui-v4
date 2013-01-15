/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.chat;

import com.thundashop.core.chatmanager.ChatMessage;
import com.thundashop.core.chatmanager.Chatter;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class ChatManager extends ManagerBase implements IChatManager, Runnable {
    private int guestNumber = 15182; 
    
    /**
     * Key = session id
     */
    private Map<String, Chatter> chatters = new ConcurrentHashMap();
    
    @Autowired
    public ChatManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof Chatter) {
                Chatter chatter = (Chatter)dataCommon;
                if (!chatter.closed) {
                    chatters.put(chatter.sessionId, chatter);
                }
            }
        }
    }
   
    @PostConstruct
    public void startCheckTimer() {
        new Thread(this).start();
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
        chatter.lastActive = new Date();
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

    @Override
    public void closeChat(String id) throws ErrorException {
        Chatter chatter = getChatterById(id);
        if (chatter != null) {
            System.out.println("Closing chatter: " + chatter.username);
            chatter.closed = true;
            databaseSaver.saveObject(chatter, credentials);
            chatters.remove(chatter.sessionId);
        }
    }

    @Override
    public void run() {
        while(true) {
            List<String> closeChatters = new ArrayList();
            Date oldestDate = new Date(System.currentTimeMillis()-20000);

            for (Chatter chatter : chatters.values()) {
                if (chatter.lastActive.before(oldestDate) && chatter.messages.size() == 0) {
                    closeChatters.add(chatter.id);
                }
            }

            for (String chatterId : closeChatters) {
                try {
                    closeChat(chatterId);
                } catch (ErrorException ex) {
                    ex.printStackTrace();
                }
            }
            try { Thread.sleep(2000); } catch (InterruptedException ex) { }
        }
    }
}