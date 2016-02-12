/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;

/**
 *
 * Communicating with the getshop lock.
 * @author boggi
 */
@GetShopApi
public interface IGetShopLockManager {
    
    @Administrator
    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception;
}
