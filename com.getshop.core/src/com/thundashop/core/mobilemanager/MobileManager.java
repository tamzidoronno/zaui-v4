/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Sender;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.mobilemanager.data.Token;
import com.thundashop.core.mobilemanager.data.TokenType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class MobileManager extends ManagerBase implements IMobileManager {
    private Map<String, Integer> badges = new HashMap();
    public Map<String, Token> tokens = new HashMap();
    public Map<String, String> userTokens = new HashMap();
    
    @Autowired
    private FrameworkConfig frameworkConfig;
    
    @Autowired
    public MobileManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof Token) {
                Token token = (Token)dataCommon;
                tokens.put(token.tokenId, token);
            }
        }
    }

    @Override
    public void registerToken(Token token) throws ErrorException {
        System.out.println("Registering token: " + token.tokenId);
        token.storeId = storeId;
        databaseSaver.saveObject(token, credentials);
        tokens.put(token.tokenId, token);
    }

    @Override
    public void sendMessageToAll(String message) {
        for (Token token : tokens.values()) {
            if (token.type != null && token.type.equals(TokenType.IOS)) {
                sendIosMessage(token, message);
            }
            if (token.type != null && token.type.equals(TokenType.ANDROID)) {
                sendAndroidMessage(token, message);
            }
        }
    }

    @Override
    public void sendMessageToAllTestUnits(String message) {
        for (Token token : tokens.values()) {
            if (token.testMode) {
                if (token.type != null && token.type.equals(TokenType.IOS)) {
                    sendIosMessage(token, message);
                }
            }
        }
    }
    
    private void increaseBadge(String tokenId) {
        if (badges.get(tokenId) == null) {
            badges.put(tokenId, 0);
        }
        
        int test = badges.get(tokenId);
        test++;
        badges.put(tokenId, test);
    }
    
    private int getBadgeNumber(String tokenId) {
        return badges.get(tokenId);
    }

    private void sendIosMessage(Token token, String message) {
        increaseBadge(token.tokenId);
        Thread thread = new Thread(new AppleNotificationThread(message, token.tokenId, getBadgeNumber(token.tokenId), "ProMeister", "auto1000", frameworkConfig));
        thread.start();
    }
    
    private void sendAndroidMessage(Token token, String message) {
        if (!frameworkConfig.productionMode) {
            System.out.println("WARNING: Did not send push notification to android device, framework is set to DEVELOPMENT mode. Be careful :) !");
            return;
        }
        
        try {
            Message gcmmessage = new Message.Builder()
                    .addData("message", message)
                    .collapseKey("demo")
                    .delayWhileIdle(true)
                    .timeToLive(3)
                    .build();
            
            Sender sender = new Sender("AIzaSyBk5eR0ZxtHUafrXAdv5pw-PtEj4GNnSvg");
            sender.send(gcmmessage, token.tokenId, 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    @Override
    public void clearBadged(String tokenId) {
        System.out.println("Clearing badget :  " + tokenId);
        badges.put(tokenId, 0);
    }

    @Override
    public void registerTokenToUserId(String tokenId) throws ErrorException {
        Token token = tokens.get(tokenId);
        if (token != null && getSession() != null && getSession().currentUser != null) {
            token.userId = getSession().currentUser.id;
            saveObject(token);
            System.out.println("Assigned token to userid");
        }
    }
}