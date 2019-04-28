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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author hung
 */

@Component
@GetShopSession
public class WebManager extends ManagerBase implements IWebManager {
    
    private final String USER_AGENT = "Mozilla/5.0";
    private HashMap<String, String> latestResponseHeader = new HashMap();
    private String latestErrorMessage;
    
    
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
    
    public String htmlPostBasicAuth(String url, String data, boolean jsonPost, String encoding, String auth, String basic, boolean base64EncodeAuth, String htmlType, HashMap<String, String> headerData)  throws Exception {
        if(encoding == null || encoding.isEmpty()) {
            encoding = "UTF-8";
        }
        
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        
        connection.setRequestMethod(htmlType);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        
        if(auth != null && !auth.isEmpty()) {
            String encoded = auth;
            if(base64EncodeAuth) {
                encoded = Base64.encodeBase64String(auth.getBytes());
            }
            String authorization = basic+ " " + encoded;
            connection.setRequestProperty("Authorization",authorization);
        }
        for(String key : headerData.keySet()) {
            connection.setRequestProperty(key, headerData.get(key));
        }
        
        if(jsonPost) {
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
        }
        
        if(data != null && !data.isEmpty()) {
            connection.setDoOutput(true);
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
                outputStream.writeBytes(new String(data.getBytes(), encoding));
                outputStream.flush();    
            }
        } else {
            connection.setRequestProperty("Content-Length", "0");
        }

        try (InputStream stream = connection.getInputStream()) {
            BufferedReader responseStream = new BufferedReader(new InputStreamReader(stream));
        
            String responseLine;
            StringBuilder responseBuffer = new StringBuilder();

            while((responseLine = responseStream.readLine()) != null) {
                responseBuffer.append(responseLine);
            }
            latestResponseHeader = new HashMap();
            try {
                for (Map.Entry<String, List<String>> entries : connection.getHeaderFields().entrySet()) {    
                    String values = "";
                    for (String value : entries.getValue()) {
                        values += value + ",";
                    }
                    latestResponseHeader.put(entries.getKey(), values);
                }
            }catch(Exception e) {
                logPrintException(e);
            }
            
            return responseBuffer.toString();
        } catch(IOException ex) {
            try (InputStream errorStream = connection.getErrorStream()) {
                BufferedReader responseStream = new BufferedReader(new InputStreamReader(errorStream));

                String responseLine;
                StringBuilder responseBuffer = new StringBuilder();

                while((responseLine = responseStream.readLine()) != null) {
                    responseBuffer.append(responseLine);
                }
                String res = responseBuffer.toString();
                latestErrorMessage = res;
                throw ex;
            }
        } finally {
            try {
                connection.disconnect();
            } catch (Exception ex) {
                logPrintException(ex);
            }
        }
    }
    
    public HashMap<String, String> getLatestResponseHeader() {
        return latestResponseHeader;
    }

    public String getLatestErrorMessage() {
        return latestErrorMessage;
    }
    
}
