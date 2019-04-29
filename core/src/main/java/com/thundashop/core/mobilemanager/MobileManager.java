/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import com.getshop.scope.GetShopSession;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.mobilemanager.data.MobileApp;
import com.thundashop.core.mobilemanager.data.Token;
import com.thundashop.core.mobilemanager.data.TokenType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class MobileManager extends ManagerBase implements IMobileManager {
    private Map<String, Integer> badges = new HashMap();
    public Map<String, Token> tokens = new HashMap();
    
    @Autowired
    private FrameworkConfig frameworkConfig;

    
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
        token.storeId = storeId;
        saveObject(token);
        tokens.put(token.tokenId, token);
    }

    @Override
    public void sendMessageToAll(String message) {
        for (Token token : tokens.values()) {
            if (token.type != null && token.type.equals(TokenType.IOS)) {
                sendIosMessage(token.tokenId, message);
            }
            if (token.type != null && token.type.equals(TokenType.ANDROID)) {
                sendAndroidMessage(token.tokenId, message);
            }
        }
    }

    @Override
    public void sendMessageToAllTestUnits(String message) {
        for (Token token : tokens.values()) {
            if (token.testMode) {
                if (token.type != null && token.type.equals(TokenType.IOS)) {
                    sendIosMessage(token.tokenId, message);
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

    private void sendIosMessage(String tokenId, String message) {
        MobileApp mobileApp = new MobileApp();
        mobileApp.id = "449d144c-c8be-4273-8917-b9b94f29a17f";
        mobileApp.iosPassword = "MecaFleet2017#";
        
        increaseBadge(tokenId);
        Thread thread = new Thread(new AppleNotificationThread(message, tokenId, getBadgeNumber(tokenId), frameworkConfig, mobileApp));
        thread.setName("IOS Android push message");
        thread.start();
    }
    
    private void sendAndroidMessage(String tokenId, String message) {
        if (tokenId.length() < 100)
            return;
        
        if (!frameworkConfig.productionMode) {
            logPrint("WARNING: Did not send push notification to android device, framework is set to DEVELOPMENT mode. Be careful!");
            return;
        }
        
        try {
            Message gcmmessage = new Message.Builder()
                    .addData("message", message)
                    .delayWhileIdle(true)
                    .timeToLive(3)
                    .build();
            
//            ProMeister Academy
//            Sender sender = new Sender("AIzaSyBk5eR0ZxtHUafrXAdv5pw-PtEj4GNnSvg");
//            sender.send(gcmmessage, token.tokenId, 1);
            
//          Meca Fleet
            Sender sender = new Sender("AIzaSyDkfN5kMdU54C5DoXw-2rgcmoWeZRi0-8c");
            sender.send(gcmmessage, tokenId, 1);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
    }

    @Override
    public void clearBadged(String tokenId) {
        badges.put(tokenId, 0);
    }

    public void sendMessage(String token, String message) {
        sendIosMessage(token, message);
        sendAndroidMessage(token, message);
    }
}