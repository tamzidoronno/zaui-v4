/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.mobilemanager.data.Token;
import com.thundashop.core.mobilemanager.data.TokenType;
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
        Thread thread = new Thread(new AppleNotificationThread(message, token.tokenId, getBadgeNumber(token.tokenId)));
        thread.start();
    }

    @Override
    public void clearBadged(String tokenId) {
        badges.put(tokenId, 0);
    }
}