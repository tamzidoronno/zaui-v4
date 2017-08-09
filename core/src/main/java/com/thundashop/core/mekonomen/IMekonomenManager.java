/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mekonomen;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.mekonomen.MekonomenUser;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IMekonomenManager {
    @Administrator
    public List<MekonomenUser> searchForUser(String name);
    
    @Administrator
    public void addUserId(String userId, String mekonomenUserName);
    
    public MekonomenUser getMekonomenUser(String userId);
}
