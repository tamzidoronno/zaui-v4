/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import static com.thundashop.core.arx.WrapClient.wrapClient;
import java.io.InputStream;
import java.net.URLDecoder;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

/**
 *
 * @author boggi
 */
public class GetshopLockCom {
    
    public static String httpLoginRequest(String address, String username, String password) throws Exception {
        String loginToken = null;
        String loginUrl = address;
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, 20000);
        HttpConnectionParams.setSoTimeout(my_httpParams, 20000);
        
        DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
        try {
            client = wrapClient(client);
            HttpResponse httpResponse;


            HttpEntity entity;
            HttpGet request = new HttpGet(loginUrl);
            byte[] bytes = (username + ":" + password).getBytes();
            String encoding = Base64.encode(bytes);

            request.addHeader("Authorization", "Basic " + encoding);
            httpResponse = client.execute(request);

            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            if(statusCode == 401) {
                return "401";
            }
            entity = httpResponse.getEntity();

            if (entity != null) {
                try (InputStream instream = entity.getContent()) {
                    int ch;
                    StringBuilder sb = new StringBuilder();
                    while ((ch = instream.read()) != -1) {
                        sb.append((char) ch);
                    }
                    String result = sb.toString();
                    return result.trim();
                }
            }
        } catch (Exception x) {
            throw x;
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }
        
        return "failed";
    }
}
