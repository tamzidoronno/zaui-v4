/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.webmanager;

import com.braintreegateway.org.apache.commons.codec.binary.Base64;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class WebManagerPostThread implements Runnable {
    private final String USER_AGENT = "Mozilla/5.0";
    private String url;
    private String data;
    private boolean jsonPost;
    private String encoding;
    private String auth;
    private String basic;
    private boolean base64EncodeAuth;
    private String htmlType;
    private HashMap<String, String> headerData;

    public WebManagerPostThread(String url, String data, boolean jsonPost, String encoding, String auth, String basic, boolean base64EncodeAuth, String htmlType, HashMap<String, String> headerData) {
        this.url = url;
        this.data = data;
        this.jsonPost = jsonPost;
        this.encoding = encoding;
        this.auth = auth;
        this.basic = basic;
        this.base64EncodeAuth = base64EncodeAuth;
        this.htmlType = htmlType;
        this.headerData = headerData;
    }

    @Override
    public void run() {
        try {
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
                HashMap latestResponseHeader = new HashMap();
                try {
                    for (Map.Entry<String, List<String>> entries : connection.getHeaderFields().entrySet()) {
                        String values = "";
                        for (String value : entries.getValue()) {
                            values += value + ",";
                        }
                        latestResponseHeader.put(entries.getKey(), values);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
                
                return;
            } catch(IOException ex) {
                try (InputStream errorStream = connection.getErrorStream()) {
                    BufferedReader responseStream = new BufferedReader(new InputStreamReader(errorStream));
                    
                    String responseLine;
                    StringBuilder responseBuffer = new StringBuilder();
                    
                    while((responseLine = responseStream.readLine()) != null) {
                        responseBuffer.append(responseLine);
                    }
                    String res = responseBuffer.toString();
                    throw ex;
                }
            } finally {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch(MalformedURLException ex) {
            Logger.getLogger(WebManagerPostThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ProtocolException ex) {
            Logger.getLogger(WebManagerPostThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebManagerPostThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
