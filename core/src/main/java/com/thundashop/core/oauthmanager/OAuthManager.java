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

    /**
     * Use the response that you get back from the login to start exchanging for the access token.
     * 
     * @param oauthSessionId
     * @param authCode
     * @param state
     * @return 
     */
    @Override
    public OAuthSession exchangeToken(String authCode, String state) {
        
//        OAuthSession session = oauthSessions.values()
//                .stream()
//                .filter(s -> s.state.equals(state))
//                .findAny()
//                .orElse(null);
//        
//        if (session != null) {
//            try {
//                String result = webManager.htmlPost(url, data, true, "UTF-8");
//                System.out.println(result);
//            } catch (Exception ex) {
//                Logger.getLogger(OAuthManager.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            
//            return session;
//        }
//        
        return null;
    }

    @Override
    public OAuthSession getCurrentOAuthSession(String oauthSessionId) {
        BasicDBObject query = new BasicDBObject();
        query.put("id", oauthSessionId);
        return (OAuthSession) oauthDatabase.query(query)
                .stream()
                .findAny()
                .orElse(null);
    }

    @Override
    public void saveObject(DataCommon data) throws ErrorException {
        oauthDatabase.save(data);
    }
}
