/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.oauthmanager;

import com.thundashop.core.common.DataCommon;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * We use the ID of this object as the state.
 * 
 * @author ktonder
 */
public class OAuthSession extends DataCommon {
    
    public String state = UUID.randomUUID().toString();
    
    public String getShopSessionId;
    
    public String loginLink = "";
    
    public String tokenAddress;
    public String address;
    public String scope;
    
    public String clientId = "";
    public String clientSecretId = "";
    
    /**
     * Used to verify man in the middle attack.
     */
    public String authCode;
    
    /**
     * The user this session belongs to.
     */
    public String userId;
    
    /**
     * When it expires
     */
    public Date expiryDate = null;
    
    /**
     * After an exchange the access token will be poplulated.
     * 
     * This access token is verification that the user has really logged in
     * and is also used in all API requests.
     * 
     * We default expire it 30 minutes after it has been created, this means that the login 
     * most likely has failed.
     */
    public String accessToken;
    
    public boolean hasExpired() {
        // Its not even stored to the database, so it cant be expired.
        if (rowCreatedDate == null)
            return false;
        
        Date now = new Date();
        
        if (expiryDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(rowCreatedDate);
            cal.add(Calendar.MINUTE, 30);
            return now.after(cal.getTime());
        }

        return expiryDate.before(now);
    }

    /**
     * Example of a loginlink 
     * 
     * https://authorization-server.com/auth?response_type=code&client_id=CLIENT_ID&redirect_uri=REDIRECT_URI&scope=photos&state=1234zy
     * 
     */
    public void createLoginLink() {
        try {
            loginLink = address + "?response_type=code&client_id="+clientId+"&redirect_uri="+URLEncoder.encode("https://oauth2.getshop.com/auth.php", "UTF-8")+"&scope="+scope+"&state="+state;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(OAuthSession.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
