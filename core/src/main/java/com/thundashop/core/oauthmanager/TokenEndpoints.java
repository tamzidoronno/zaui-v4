
package com.thundashop.core.oauthmanager;

import java.util.HashMap;

public class TokenEndpoints {
    HashMap<String,String> authorizeEndPoint = new HashMap();
    HashMap<String,String> tokenEndPoint = new HashMap();
    HashMap<String,String> endPoint = new HashMap();
    HashMap<String,String> endPointTest = new HashMap();
    HashMap<String,String> clientId = new HashMap();
    HashMap<String,String> clientIdTest = new HashMap();
    HashMap<String,String> clientSecret = new HashMap();
    HashMap<String,String> clientSecretTest = new HashMap();
    HashMap<String,String> clientRedirect = new HashMap();
    HashMap<String,String> clientRedirectTest = new HashMap();
    
    public TokenEndpoints() {
        //Visma eaccounting
        endPoint.put("vismaeaccounting", "https://identity.vismaonline.com/");
        endPointTest.put("vismaeaccounting", " https://identity-sandbox.test.vismaonline.com/");
        authorizeEndPoint.put("vismaeaccounting","connect/authorize");
        tokenEndPoint.put("vismaeaccounting","connect/token");
        clientSecret.put("vismaeaccounting", "dduj4kGnhy1tgSFXa179YnJAB1atauRDXkYxKDCpUWvSCgWLDFecGkt3bn6GdRv");
        clientSecretTest.put("vismaeaccounting", "BhQ2172BnzvSxXIzzViuyIimGTKfnkD9IeyhyZmI5K7NzgQ86m7baOx6WfvEEhHaa4dFv9dDzHuS69");
        clientId.put("vismaeaccounting","getshop");
        clientIdTest.put("vismaeaccounting","getshop");
        clientRedirect.put("vismaeaccounting","https://localhost:44300/callback");
        clientRedirectTest.put("vismaeaccounting","https://localhost:44300/callback");
    }
}
