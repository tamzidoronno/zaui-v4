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
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.springframework.stereotype.Component;

/**
 *
 * @author hung
 */

@Component
@GetShopSession
public class WebManager extends ManagerBase implements IWebManager {
    
    private final String USER_AGENT = "Mozilla/5.0";
    
    
    
    @Override
    public String htmlGet(String url) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        
        BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));    
        
        String responseLine;
        StringBuilder responseBuffer = new StringBuilder();
        
        while((responseLine = responseStream.readLine()) != null) {
            responseBuffer.append(responseLine);
        }
        
        return responseBuffer.toString();
    }
    
    @Override
    public JsonObject htmlGetJson(String url) throws Exception{
        return new JsonParser().parse(htmlGet(url)).getAsJsonObject();
    }
    
    @Override
    public String htmlPost(String url, String data, boolean jsonPost, String encoding) throws Exception {
        return htmlPostBasicAuth(url, data, jsonPost, encoding, "");
    }       
    
    @Override
    public JsonObject htmlPostJson(String url, JsonObject jsonObject, String encoding) throws Exception {
        return new JsonParser().parse(htmlPost(url, jsonObject.toString(), true, encoding)).getAsJsonObject();
    }

    @Override
    public String htmlPostBasicAuth(String url, String data, boolean jsonPost, String encoding, String auth) throws Exception {
        if(encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
        
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        
        if(auth != null && !auth.isEmpty()) {
            String encoded = Base64.encodeBase64String(auth.getBytes());
            connection.setRequestProperty("Authorization", "Basic "+encoded);
        }
        
        if(jsonPost) {
            connection.setRequestProperty("Content-Type", "application/json");
        }
        
        connection.setDoOutput(true);
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        outputStream.writeBytes(new String(data.getBytes(), encoding));
        outputStream.flush();
        outputStream.close();
        
        BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));    
        
        String responseLine;
        StringBuilder responseBuffer = new StringBuilder();
        
        while((responseLine = responseStream.readLine()) != null) {
            responseBuffer.append(responseLine);
        }
        
        return responseBuffer.toString();    }
}
