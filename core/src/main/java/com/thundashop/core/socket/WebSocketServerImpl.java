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
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.WebSocketReturnMessage;
import com.thundashop.core.common.WebSocketWrappedMessage;
import com.thundashop.core.websocket.WebSocketClient;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
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
    boolean started = false;
    
    public WebSocketServerImpl() {
        super(new InetSocketAddress(31330));

//      Starting of SSL implementation.
//        try {
//            initSll();
//        } catch (Exception ex) {
//            Logger.getLogger(WebSocketServerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        } 
    }

    public void run() {
        
        while(true) {
            if(canBindToPort()) {
                System.out.println("Websocket port is available");
                break;
            }
            try { Thread.sleep(5000); }catch(Exception e) {}
            System.out.println("Waiting for websocket port to become available for binding");
        }
        super.run();
    }
    
    public boolean canBindToPort() {
        try {
            ServerSocketChannel servertest = ServerSocketChannel.open();
            servertest.configureBlocking( false );
            ServerSocket socket = servertest.socket();
            socket.setReceiveBufferSize( WebSocketImpl.RCVBUF );
            socket.setReuseAddress( isReuseAddr() );
            socket.bind( new InetSocketAddress(31330) );
            socket.close();
            return true;
        }catch(Exception e) {

        }
        return false;
    }
    
    @Override
    public void onOpen(WebSocket ws, ClientHandshake ch) {
        try {
             WebSocketClient client = applicationContext.getBean(WebSocketClient.class);
            client.setWs(ws);
            clients.put(ws, client);
        } catch (BeansException ex) {
            GetShopLogHandler.logPrintStatic("Got a bean exception?", null);
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
//        clients.remove(ws);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void sendMessage(Object data) {
        if (data == null)
            return;
        
        WebSocketWrappedMessage wrapped = new WebSocketWrappedMessage();
        wrapped.payLoad = data;
        wrapped.coninicalName = data.getClass().getCanonicalName();
        
        Gson gson = new Gson();
        String string = gson.toJson(wrapped);
        
        for (WebSocketClient client : clients.values()) {
            client.sendMessage(string);
        }
    }

    @Override
    public void onStart() {
        System.out.println("Unsecure websocket startet on port 31330");
        started = true;
    }
    
}
