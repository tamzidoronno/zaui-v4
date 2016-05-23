/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.webmanager;

import com.google.gson.JsonObject;
import com.thundashop.core.common.GetShopApi;

/**
 *
 * @author hung
 */

@GetShopApi
public interface IWebManager {
    
    public String htmlGet(String url) throws Exception;
    
    public JsonObject htmlGetJson(String url) throws Exception;
    
    public String htmlPost(String url, String data, boolean jsonPost) throws Exception;
    
    public JsonObject htmlPostJson(String url, JsonObject data) throws Exception;
}
