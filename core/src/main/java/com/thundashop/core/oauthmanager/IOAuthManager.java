/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.oauthmanager;

import com.thundashop.core.common.Customer;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author boggi
 */
@GetShopApi
public interface IOAuthManager {
    public OAuthSession startNewOAuthSession(String address, String clientId, String redirectTo, String scope, String endDestination, String clientSecretId);
    public OAuthSession exchangeToken(String authCode, String state);
    public OAuthSession getCurrentOAuthSession(String oauthSessionId);
}
