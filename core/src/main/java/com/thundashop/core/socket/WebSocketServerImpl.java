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
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.common.WebSocketReturnMessage;
import com.thundashop.core.common.WebSocketWrappedMessage;
import com.thundashop.core.websocket.WebSocketClient;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;
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

//      Starting of SSL implementation.
//        try {
//            initSll();
//        } catch (Exception ex) {
//            Logger.getLogger(WebSocketServerImpl.class.getName()).log(Level.SEVERE, null, ex);
//        } 
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
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void sendMessage(Object data) {
        WebSocketWrappedMessage wrapped = new WebSocketWrappedMessage();
        wrapped.payLoad = data;
        wrapped.coninicalName = data.getClass().getCanonicalName();
        
        Gson gson = new Gson();
        String string = gson.toJson(wrapped);
        
        for (WebSocketClient client : clients.values()) {
            client.sendMessage(string);
        }
    }

    private void initSll() throws FileNotFoundException, CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        InputStream is = new FileInputStream("/etc/nginx/ssl/getshop_com.crt");
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        X509Certificate caCert = (X509Certificate)cf.generateCertificate(is);

        TrustManagerFactory tmf = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null); // You don't need the KeyStore instance to come from a file.
//        ks.setCertificateEntry("caCert", caCert);

        tmf.init(ks);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
    }
    
}
