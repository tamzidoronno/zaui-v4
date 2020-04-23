/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.central;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.data.User;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGetShopCentral {
    @Administrator
    public List<CentralAccessToken> getTokens();

    public Store validateAccessToken(String token);
    
    public String login(String token, User user);
    
    @Administrator
    public void createNewAccessToken(String name);
    
    public boolean loginByToken(String token);
    
    public boolean isConnectedToACentral();
    
}
