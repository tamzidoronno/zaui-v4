/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.websocket;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.storemanager.StorePool;
import com.thundashop.core.storemanager.data.Store;

import java.util.UUID;
import javax.annotation.PostConstruct;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
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
    private String storeId = null;
    
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
    
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    
    public void processMessage(String message) throws ErrorException {
        if (message.length() > 8 && message.substring(0,9).equals("initstore")) {    
            storePool.initialize(message.substring(10), this.sessionId);
        } else if (message.length() > 8 && message.substring(0,9).equals("sessionid")) {    
            this.sessionId = message.substring(10);
        } else {
            String addr = ws.getRemoteSocketAddress().getAddress().toString();
            Object result = AppContext.storePool.ExecuteMethod(message, addr, sessionId);
            sendMessage(result);
        }
    }
    
    public void sendMessage(Object result) {
        try {
            String jsonResult = gson.toJson(result);
            ws.send(jsonResult);
        }catch(Exception e) {
            //Ignore this error, they will be pretty obvious anyway.
            //This is done so that we do not spam log.
            GetShopLogHandler.logPrintStatic(e.getMessage(), null);
        }
    }
}
