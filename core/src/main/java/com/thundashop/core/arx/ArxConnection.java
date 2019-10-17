
package com.thundashop.core.arx;

import static com.thundashop.core.arx.WrapClient.wrapClient;
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.usermanager.data.User;
import java.io.InputStream;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class ArxConnection {
    
    public void doorAction(String hostname, String username, String password, String externalId, String state, boolean setOn) throws Exception {
        String hostName = "https://" + hostname + ":5002/arx/door_actions?externalid="+externalId+"&type="+state;
        if(setOn) {
            hostName += "&value=on";
        } else {
            hostName += "&value=off";
        }
        GetShopLogHandler.logPrintStatic(hostName, null);
        httpLoginRequest(hostName, username, password, "", null);
    }
    
    public String httpLoginRequest(String address, String username, String password, String content, String storeId) throws Exception {
        
        if(address == null || address.isEmpty() || !address.startsWith("http") || address.contains("://:50")) {
            GetShopLogHandler.logPrintStatic("Tried accessing arx with empty host: " + address, null);
            return "";
        }
        String loginToken = null;
        String loginUrl = address;
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, 3000);
        HttpConnectionParams.setSoTimeout(my_httpParams, 6000);
        
        DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
        client = wrapClient(client);
        
        try {
            HttpResponse httpResponse;


            HttpEntity entity;
            HttpRequestBase request = null;
            if(storeId.equals("b0d1acd4-4f4c-49ff-b4c1-e6414c59f587") && address.contains("eventexport")) {
                request = new HttpGet(loginUrl);
            } else {
                StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);
                StringBody body = new StringBody(content, ContentType.TEXT_PLAIN);
            
                HttpPost tmpRequest = new HttpPost(loginUrl);
                HttpEntity reqEntity = MultipartEntityBuilder.create()
                        .addPart("upfile", body)
                        .addPart("comment", comment)
                        .build();
                
                tmpRequest.setEntity(reqEntity);
                request = tmpRequest;
            }
            byte[] bytes = (username + ":" + password).getBytes();
            String encoding = Base64.encode(bytes);

            request.addHeader("Authorization", "Basic " + encoding);

            try {
                httpResponse = client.execute(request);
            }catch(Exception e) {
                GetShopLogHandler.logPrintStatic("Failed lookup on address: " + address + " : " + e.getMessage(), null);
                throw e;
            }

            Integer statusCode = httpResponse.getStatusLine().getStatusCode();
            if(statusCode == 401) {
                return "401";
            }

            entity = httpResponse.getEntity();

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
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }
            
        return "failed";
    }
    
}
