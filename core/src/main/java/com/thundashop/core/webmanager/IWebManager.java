/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.webmanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author hung
 */

@GetShopApi
public interface IWebManager {
    
    public String htmlGet(String url) throws Exception;
}
