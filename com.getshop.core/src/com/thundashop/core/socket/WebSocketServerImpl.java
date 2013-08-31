/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.socket;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.websocket.WebSocketClient;
import java.net.InetSocketAddress;
import java.util.HashMap;
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
        WebSocketClient client = applicationContext.getBean(WebSocketClient.class);
        client.setWs(ws);
        clients.put(ws, client);
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
                // SEND BACK TO API.
                ex.printStackTrace();
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
