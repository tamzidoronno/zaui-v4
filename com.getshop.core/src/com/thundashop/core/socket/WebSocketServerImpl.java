/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ErrorMessage;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.WebSocketReturnMessage;
import com.thundashop.core.websocket.WebSocketClient;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class WebSocketServerImpl extends WebSocketServer implements Runnable, ApplicationContextAware {
    private HashMap<WebSocket, WebSocketClient> clients = new HashMap();
    private ApplicationContext applicationContext;
    
    public WebSocketServerImpl() {
        super(new InetSocketAddress(31330));
    }

    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        try {
            WebSocketClient client = applicationContext.getBean(WebSocketClient.class);
            client.setWs(ws);
            clients.put(ws, client);
        } catch (BeansException ex) {
            System.out.println("Got a bean exception?");
        }
    }

    @Override
    public void onClose(WebSocket ws, int i, String string, boolean bln) {
        clients.remove(ws);
    }

    @Override
    public void onMessage(WebSocket ws, String message) {
        WebSocketClient client = clients.get(ws);
        if (client != null) {
            try {
                client.processMessage(message);
            } catch (ErrorException ex) {
                Gson gson = new GsonBuilder().serializeNulls().create();
                Type type = new TypeToken<JsonObject2>() {}.getType();
                JsonObject2 object = gson.fromJson(message, type);
                
                ErrorMessage msg = new ErrorMessage(ex);                
                WebSocketReturnMessage returnmessage = new WebSocketReturnMessage();
                returnmessage.messageId = object.messageId;
                returnmessage.object = msg;

                client.sendMessage(returnmessage);
            }
        }
    }

    @Override
    public void onError(WebSocket ws, Exception excptn) {
        excptn.printStackTrace();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
}
