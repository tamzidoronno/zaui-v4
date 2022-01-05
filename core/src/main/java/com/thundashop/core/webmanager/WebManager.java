/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.webmanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import com.getshop.scope.GetShopSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thundashop.core.common.ManagerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 *
 * @author hung
 */

@Component
@GetShopSession
public class WebManager extends ManagerBase implements IWebManager {
    
    private static final Logger logger = LoggerFactory.getLogger(WebManager.class);
    
    private HashMap<String, String> latestResponseHeader = new HashMap<>();

    @Autowired
    private OkHttpService okHttpService;
    
    
    @Override
    public String htmlGet(String url) {
        OkHttpRequest request = OkHttpRequest.builder()
                .setUrl(url)
                .build();
        
        OkHttpResponse response = okHttpService.get(request);
        
        if (!response.isSuccessful()) {
            logger.error("Unsuccessful GET request, response: {}", response);
            throw new RuntimeException(String.format("Unsuccessful GET request url: [%s] , code: [%s]", 
                    url, response.statusCode()));
        }
        
        return response.getBody();
    }
    
    @Override
    public JsonObject htmlGetJson(String url) throws Exception{
        return new JsonParser().parse(htmlGet(url)).getAsJsonObject();
    }
    
    @Override
    public String htmlPost(String url, String data, boolean jsonPost, String encoding) throws Exception {
        return htmlPostBasicAuth(url, data, jsonPost, encoding, "");
    }       
    
    public void htmlPostThreaded(String url, String data, boolean jsonPost, String encoding) throws Exception {
        WebManagerPostThread thread = new WebManagerPostThread(url, data, jsonPost, encoding, "", "Basic", true, "POST", new HashMap());
        Thread td = new Thread(thread);
        td.setName("Posting data to " + url);
        td.start();
    }       
    
    @Override
    public JsonObject htmlPostJson(String url, JsonObject jsonObject, String encoding) throws Exception {
        return new JsonParser().parse(htmlPost(url, jsonObject.toString(), true, encoding)).getAsJsonObject();
    }

    @Override
    public String htmlPostBasicAuth(String url, String data, boolean jsonPost, String encoding, String auth) throws Exception {
        return htmlPostBasicAuth(url, data, jsonPost, encoding, auth, "Basic", true, "POST");
    }

    public String htmlPostBasicAuth(String url, String data, boolean jsonPost, String encoding, String auth, String basic, boolean base64EncodeAuth, String htmlType)  throws Exception {
        return htmlPostBasicAuth(url, data, jsonPost, encoding, auth, basic, base64EncodeAuth, htmlType, new HashMap());
    }

    public String htmlPostBasicAuth(String url, String data, boolean jsonPost, String encoding, String auth, String basic, boolean base64EncodeAuth, String htmlType, HashMap<String, String> headerData) throws Exception {

        // ignore the `encoding` param. payload always will be utf-8 encoded.

        OkHttpRequest request = OkHttpRequest.builder()
                .setAuth(authorization(auth, basic, base64EncodeAuth))
                .setPayload(data)
                .setUrl(url)
                .jsonPost(jsonPost)
                .build();

        OkHttpResponse response = okHttpService.post(request);

        if (!response.isSuccessful()) {
            logger.error("Unsuccessful POST request {}, response: {}", request, response);
            throw new RuntimeException(String.format("Unsuccessful POST request url: [%s] , code: [%s]",
                    url, response.statusCode()));
        }

        // previous logic. but don't know why?!
        latestResponseHeader = (HashMap<String, String>) response.headers();

        return response.getBody();
    }

    private String authorization(String auth, String basic, boolean base64EncodeAuth) {
        if (auth != null && !auth.isEmpty()) {
            String encoded = auth;
            if (base64EncodeAuth) {
                encoded = Base64.encodeBase64String(auth.getBytes());
            }
            String authorization = basic + " " + encoded;
            return authorization;
        }

        return null;
    }
    
    public HashMap<String, String> getLatestResponseHeader() {
        return latestResponseHeader;
    }
}
