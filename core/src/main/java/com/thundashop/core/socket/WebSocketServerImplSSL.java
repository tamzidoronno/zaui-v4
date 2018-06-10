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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.java_websocket.server.CustomSSLWebSocketServerFactory;

/**
 *
 * @author ktonder
 */
@Component
public class WebSocketServerImplSSL extends WebSocketServer implements Runnable, ApplicationContextAware {
    private HashMap<WebSocket, WebSocketClient> clients = new HashMap();
    private ApplicationContext applicationContext;
    
    public WebSocketServerImplSSL() {
        super(new InetSocketAddress(21330));
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
        System.out.println("Secure websocket server starting on port 21330");
    }

    public void useSSL() throws Exception {
        String KEYSTORE = "/home/boggi/KeyStore.jks";
        String STOREPASSWORD = "123456";
        String KEYPASSWORD = "123456";

        KeyStore ks = KeyStore.getInstance( KeyStore.getDefaultType() );
        File kf = new File( KEYSTORE );
        ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
        kmf.init( ks, KEYPASSWORD.toCharArray() );
        TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
        tmf.init( ks );
        
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        //Lets remove some ciphers and protocols
        SSLEngine engine = sslContext.createSSLEngine();
        List<String> ciphers = new ArrayList<String>( Arrays.asList(engine.getEnabledCipherSuites()));
        ciphers.remove("TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256");
        List<String> protocols = new ArrayList<String>( Arrays.asList(engine.getEnabledProtocols()));
        protocols.remove("SSLv3");
        CustomSSLWebSocketServerFactory factory = new CustomSSLWebSocketServerFactory(sslContext, protocols.toArray(new String[]{}), ciphers.toArray(new String[]{}));

        setWebSocketFactory(factory);
    }
}
