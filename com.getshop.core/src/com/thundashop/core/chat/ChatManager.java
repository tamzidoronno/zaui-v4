package com.thundashop.core.chat;

import com.thundashop.core.appmanager.AppManager;
import com.thundashop.core.appmanager.data.ApplicationSettings;
import com.thundashop.core.appmanager.data.AvailableApplications;
import com.thundashop.core.chatmanager.ChatMessage;
import com.thundashop.core.chatmanager.Chatter;
import com.thundashop.core.chatmanager.SubscribedToAirgram;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.Setting;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.usermanager.data.User;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
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
public class ChatManager extends ManagerBase implements IChatManager, Runnable {

    private int guestNumber = 15182;
    private HashMap<String, Long> lastMobileActive = new HashMap();

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
                Chatter chatter = (Chatter) dataCommon;
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
            chatter.username = (user == null) ? "guest" + guestNumber : user.fullName;
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

        if(!isMobileChatIsActive(chatter.id)) {
            pushToAirGram(chatMessage.message, chatter.id);
        }
        

        chatter.messages.add(chatMessage);
        databaseSaver.saveObject(chatter, credentials);
    }

    public void pushToAirGram(String message, String chatterid) throws ErrorException {

        AppManager manager = getManager(AppManager.class);
        AvailableApplications apps = manager.getAllApplications();
        String address = this.getStore().webAddressPrimary;
        if (address == null) {
            address = this.getStore().webAddress;
        }
        String app = "";
        for (ApplicationSettings setting : apps.applications) {
            if (setting.appName.equals("Chat")) {
                app = setting.id;
            }
        }
        String gsurl = "http://" + address + "/mobile.php?app=" + app + "&id=" + chatterid;
        try {
            gsurl = URLEncoder.encode(gsurl, "UTF-8");
            message = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
        }
        MessageManager mgr = getManager(MessageManager.class);
        mgr.sendToAirgram(gsurl, message);
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
            chatter.closed = true;
            databaseSaver.saveObject(chatter, credentials);
            chatters.remove(chatter.sessionId);
        }
    }

    @Override
    public void run() {
        while (true) {
            List<String> closeChatters = new ArrayList();
            Date oldestDate = new Date(System.currentTimeMillis() - 20000);

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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
        }
    }
 
    @Override
    public void pingMobileChat(String chatterid) throws ErrorException {
        lastMobileActive.put(chatterid, new Date().getTime());
    }

    private boolean isMobileChatIsActive(String id) {
        if(lastMobileActive.get(id) == null) {
            return false;
        }
        long lastactive = lastMobileActive.get(id);
        long diff = new Date().getTime() - lastactive;
        if(diff > 5000) {
            return false;
        }
        return true;
   }
}
