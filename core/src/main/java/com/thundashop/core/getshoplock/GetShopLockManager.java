/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSession;
import static com.thundashop.core.arx.DoorManager.wrapClient;
import com.thundashop.core.usermanager.data.User;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.stereotype.Component;


@Component 
@GetShopSession
public class GetShopLockManager {
    public String username = "";
    public String password = "";
    public String hostname = "";
    
    
    public void setCredentials(String username, String password, String hostname) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
    }
    
    public String httpLoginRequest(String address, String username, String password) throws Exception {

        String loginToken = null;
        String loginUrl = address;
        
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, 3000);
        HttpConnectionParams.setSoTimeout(my_httpParams, 6000);
        
        DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
        client = wrapClient(client);
        HttpResponse httpResponse;
        

        HttpEntity entity;
        HttpGet request = new HttpGet(loginUrl);
        byte[] bytes = (username + ":" + password).getBytes();
        String encoding = Base64.encode(bytes);

        request.addHeader("Authorization", "Basic " + encoding);

        System.out.println("Now sending to arx");
        httpResponse = client.execute(request);

        Integer statusCode = httpResponse.getStatusLine().getStatusCode();
        if(statusCode == 401) {
            return "401";
        }


        entity = httpResponse.getEntity();



        System.out.println("Done sending to arx");

        if (entity != null) {
            InputStream instream = entity.getContent();
            int ch;
            StringBuilder sb = new StringBuilder();
            while ((ch = instream.read()) != -1) {
                sb.append((char) ch);
            }
            String result = sb.toString();
            return result.trim();
        }
            
        return "failed";
    }

    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception {
        SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.YYYY HH:mm");
        String startString = start.getTime()/1000 + "";
        String endString = end.getTime()/1000 + "";
        
        id = URLEncoder.encode(id, "UTF-8");
        
        String address = "http://"+hostname+":8080/storecode/"+door+"/"+id+"/"+startString+"/"+endString+"/" + code;
        System.out.println("Executing: " + address);
        return this.httpLoginRequest(address, username, password);
    }

    public String removeCode(String pmsBookingRoomId) throws Exception {
        String id = URLEncoder.encode(pmsBookingRoomId, "UTF-8");
        String address = "http://"+hostname+":8080/deletekey/"+id;
        System.out.println("Executing: " + address);
        return this.httpLoginRequest(address, username, password);
    }
    
}
