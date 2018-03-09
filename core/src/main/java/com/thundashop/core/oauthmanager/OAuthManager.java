package com.thundashop.core.oauthmanager;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.getshopaccounting.VismaEaccountingTokenResponse;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserOAuthorization;
import com.thundashop.core.webmanager.WebManager;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class OAuthManager extends ManagerSubBase implements IOAuthManager {
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Autowired
    UserManager userManager;
    
    @Autowired
    WebManager webManager;
    
    HashMap<String, OAuthState> states = new HashMap();
    
    public TokenEndpoints endpoints = new TokenEndpoints();
    
    public String getEndPoint(String source) {
        if(frameworkConfig.productionMode) {
            return endpoints.endPoint.get(source);
        } else {
            return endpoints.endPointTest.get(source);
        }
    }

    public String getAuthrizeEndPoint(String source) {
        String endpoint = getEndPoint(source);
        return endpoint+endpoints.authorizeEndPoint.get(source);
    }
    
    
    public String getToken(String source) {
        User user = userManager.getLoggedOnUser();
        UserOAuthorization authObject = user.oAuths.get(source);
        if(authObject == null) {
            return null;
        }
        if(!authObject.authorized) {
            return doAuthorization(source);
        } else {
            if(authObject.expire.before(new Date())) {
                return refreshToken(source);
            }
        }
        return authObject.token;
    }

    private String refreshToken(String source) {
        User user = userManager.getLoggedOnUser();
        UserOAuthorization authObject = user.oAuths.get(source);
        String tokenendPoint = getEndPoint(source) + endpoints.tokenEndPoint.get(source);
        String clientId = getClientId(source);
        String clientSecret = getClientSecret(source);
        String code = authObject.code;
        String redirect = getRedirect(source);
        
        HashMap<String, String> headerData = new HashMap();
        String toPost = "code="+code+"&grant_type=refresh_token&refresh_token="+authObject.refreshToken+"&redirect_uri="+redirect;
        String auth = clientId + ":" + clientSecret;
        try {
            String result = webManager.htmlPostBasicAuth(tokenendPoint, toPost, false, "UTF-8", auth, "Basic", true, "POST", headerData);
            Gson gson = new Gson();
            VismaEaccountingTokenResponse res = gson.fromJson(result, VismaEaccountingTokenResponse.class);
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, res.expires_in);
            authObject.expire = cal.getTime();
            authObject.token = res.access_token;
            authObject.refreshToken = res.refresh_token;            
        }catch(Exception e) {
            e.printStackTrace();
        }
        authObject.authorized = true;
        userManager.saveUser(user);
        return authObject.token;
    }

    private String doAuthorization(String source) {
        User user = userManager.getLoggedOnUser();
        UserOAuthorization authObject = user.oAuths.get(source);
        String tokenendPoint = getEndPoint(source) + endpoints.tokenEndPoint.get(source);
        String clientId = getClientId(source);
        String clientSecret = getClientSecret(source);
        String code = authObject.code;
        String redirect = getRedirect(source);
        
        HashMap<String, String> headerData = new HashMap();
        String toPost = "code="+code+"&grant_type=authorization_code&redirect_uri="+redirect;
        String auth = clientId + ":" + clientSecret;
        try {
            String result = webManager.htmlPostBasicAuth(tokenendPoint, toPost, false, "UTF-8", auth, "Basic", true, "POST", headerData);
            Gson gson = new Gson();
            VismaEaccountingTokenResponse res = gson.fromJson(result, VismaEaccountingTokenResponse.class);

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, res.expires_in);
            authObject.expire = cal.getTime();
            authObject.token = res.access_token;
            authObject.refreshToken = res.refresh_token;
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println(webManager.getLatestErrorMessage());
        }
        authObject.authorized = true;
        userManager.saveUser(user);
        
        return authObject.token;
    }
    
    public String getClientId(String source) {
        if(frameworkConfig.productionMode) {
            return endpoints.clientId.get(source);
        } else {
            return endpoints.clientIdTest.get(source);
        }
    }

    private String getClientSecret(String source) {
        if(frameworkConfig.productionMode) {
            return endpoints.clientSecret.get(source);
        } else {
            return endpoints.clientSecretTest.get(source);
        }
    }

    private String getRedirect(String source) {
        if(frameworkConfig.productionMode) {
            return endpoints.clientRedirect.get(source);
        } else {
            return endpoints.clientRedirectTest.get(source);
        }
    }

    public String createState(String source, String redirectTo) {
        String uuid = UUID.randomUUID().toString();
        OAuthState state = new OAuthState();
        state.source = source;
        state.redirectTo = redirectTo;
        states.put(uuid, state);
        return uuid;
    }

    @Override
    public void handleCallback(String code, String state) {
        OAuthState res = states.get(state);
        User user = userManager.getLoggedOnUser();
        UserOAuthorization authObject = new UserOAuthorization();
        authObject.code = code;
        user.oAuths.put(res.source, authObject);
        userManager.saveUser(user);
    }

    @Override
    public String getStateRedirect(String state) {
        OAuthState res = states.get(state);
        return res.redirectTo;
    }

}
