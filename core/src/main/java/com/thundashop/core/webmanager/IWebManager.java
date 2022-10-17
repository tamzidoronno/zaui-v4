/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.webmanager;

import com.google.gson.JsonObject;
import com.thundashop.core.common.GetShopApi;

import java.io.IOException;
import java.util.Map;

/**
 *
 * @author hung
 */

@GetShopApi
public interface IWebManager {
    
    public String htmlGet(String url) throws Exception;
    
    public JsonObject htmlGetJson(String url) throws Exception;
    
    public String htmlPost(String url, String data, boolean jsonPost, String encoding) throws Exception;
    
    public String htmlPostBasicAuth(String url, String data, boolean jsonPost, String encoding, String auth) throws Exception;
    
    public JsonObject htmlPostJson(String url, JsonObject data, String encoding) throws Exception;

    public default String getResponseWithHeaders(
            String url, String accessToken, Map<String, String> headers, Object requestBody, Class classType, String method) throws IOException {
        return "";
    }
}
