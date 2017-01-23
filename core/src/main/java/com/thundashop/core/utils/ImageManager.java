package com.thundashop.core.utils;


import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ImageManager extends ManagerBase {
    public String saveImage(String base64encodedImage) {
        try {
            return post(base64encodedImage);
        } catch (IOException ex) {
            Logger.getLogger(ImageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private String post(String data) throws IOException {
        String result = null;
        String url = "http://"+getStoreDefaultAddress()+"/scripts/makeImageHappen.php";
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);

        // Request parameters and other properties.
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>(2);
        params.add(new BasicNameValuePair("data", data));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));

        //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();

        if (entity != null) {
            InputStream instream = entity.getContent();
            try {
                java.util.Scanner s = new java.util.Scanner(instream).useDelimiter("\\A");
                result = s.hasNext() ? s.next() : "";
            } finally {
                instream.close();
            }
        }
        
        return result;
    }
}
