package com.thundashop.core.oauthmanager;

import com.getshop.scope.GetShopSession;
import com.mongodb.BasicDBObject;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.OAuthDatabase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.webmanager.WebManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class OAuthManager extends ManagerBase implements IOAuthManager {
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    WebManager webManager;

    @Autowired
    private OAuthDatabase oauthDatabase;
    
  
    /**
     * Example of address.
     * 
     * Creates an OAuth session. Use the ID of this object for further communication.
     * 
     * @param address - example: integration.visma.net/API-index/doc/oauth/authorization
     * @param clientId - example: getshop
     * @param storeWebHostName - normally the webpage you are already at
     * @param scope - the different scopes the applications provides.
     * @return 
     */
    @Override
    public OAuthSession startNewOAuthSession(String authAddress, String clientId, String scope, String clientSecretId, String tokenAddress) {
        OAuthSession session = new OAuthSession();
        session.address = authAddress;
        session.tokenAddress = tokenAddress;
        session.clientId = clientId;
        session.scope = scope;
        session.clientSecretId = clientSecretId;
        
        if (getSession() != null && getSession().currentUser != null) {
            session.userId = getSession().currentUser.id;
        }
        
        session.createLoginLink();
        
        saveObject(session);
        
        return session;
    }

    @Override
    public OAuthSession getCurrentOAuthSession(String oauthSessionId) {
        cleanUpExpiredSession();
        
        BasicDBObject query = new BasicDBObject();
        query.put("_id", oauthSessionId);
        
        return (OAuthSession) oauthDatabase.query(query)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public void saveObject(DataCommon data) throws ErrorException {
        oauthDatabase.save(data);
    }

    private void cleanUpExpiredSession() {
        BasicDBObject query = new BasicDBObject();
        List<OAuthSession> expiredSessions = oauthDatabase.query(query)
                .stream()
                .filter(o -> (o instanceof OAuthSession))
                .map(o -> (OAuthSession)o)
                .filter(o -> o.hasExpired())
                .collect(Collectors.toList());
        
        expiredSessions.stream()
                .forEach(session -> {
                    System.out.println("Deleted: " + session.id);
                    oauthDatabase.hardDelete(session);
                });
        
    }
}
