/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.websocket;

import com.google.gson.Gson;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;

import java.util.UUID;
import javax.annotation.PostConstruct;
import org.java_websocket.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class WebSocketClient {
    private WebSocket ws;
    private String sessionId;
    private Store store;
    
    @Autowired
    private StorePool storePool;
    private Gson gson;
    
    @PostConstruct
    public void setSessionId() {
        this.gson = new Gson();
        this.sessionId = UUID.randomUUID().toString();
    }

    public void setWs(WebSocket ws) {
        this.ws = ws;
    }
    
    public void processMessage(String message) throws ErrorException {
        
        if (message.length() > 8 && message.substring(0,9).equals("initstore")) {    
            storePool.initialize(message.substring(10), this.sessionId);
        } else {
            String addr = ws.getRemoteSocketAddress().getAddress().toString();
            Object result = AppContext.storePool.ExecuteMethod(message, addr, sessionId);
            sendMessage(result);
        }
    }

    public void sendMessage(Object result) {
        String jsonResult = gson.toJson(result);
        ws.send(jsonResult);
    }
}
