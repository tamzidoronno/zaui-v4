
package com.thundashop.core.arx;

import static com.thundashop.core.arx.DoorManager.wrapClient;
import com.thundashop.core.usermanager.data.User;
import java.io.InputStream;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
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
        System.out.println(hostName);
        httpLoginRequest(hostName, username, password, "");
    }
    
    public String httpLoginRequest(String address, String username, String password, String content) throws Exception {
        if(address == null || address.isEmpty() || !address.startsWith("http") || address.contains("://:50")) {
            System.out.println("Tried accessing arx with empty host: " + address);
            return "";
        }
        String loginToken = null;
        String loginUrl = address;
        
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, 3000);
        HttpConnectionParams.setSoTimeout(my_httpParams, 6000);
        
        DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
        client = wrapClient(client);
        HttpResponse httpResponse;
        

        HttpEntity entity;
        HttpPost request = new HttpPost(loginUrl);
        byte[] bytes = (username + ":" + password).getBytes();
        String encoding = Base64.encode(bytes);

        request.addHeader("Authorization", "Basic " + encoding);

        StringBody comment = new StringBody("A binary file of some kind", ContentType.TEXT_PLAIN);

        StringBody body = new StringBody(content, ContentType.TEXT_PLAIN);

        HttpEntity reqEntity = MultipartEntityBuilder.create()
                .addPart("upfile", body)
                .addPart("comment", comment)
                .build();

        request.setEntity(reqEntity);
        
        httpResponse = client.execute(request);

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
            
        return "failed";
    }
    
}
