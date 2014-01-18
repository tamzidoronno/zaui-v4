/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bigstock;

import com.google.gson.Gson;
import com.thundashop.core.bigstock.data.BigStockPurchaseResponse;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("prototype")
public class BigStock extends ManagerBase implements IBigStock {

    @Autowired
    private FrameworkConfig frameworkConfig;
    
    private Gson gson = new Gson();
    
    @Autowired
    public BigStock(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
    private String getSha1Coded(String sha1String) {
        try {
            MessageDigest digest = null;
            digest = java.security.MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(sha1String.getBytes("UTF-8"));

            byte[] mdbytes = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        
        return "";
    }

    @Override
    public String purchaseImage(String imageId, String sizeCode) throws ErrorException {

        String url = null;
        String accountId = "442193";
        String key = "72c2747254f7606977bf2c528430eb4f4bcdf703";
        String sha1String = getSha1Coded(key + accountId + imageId);

        String address = frameworkConfig.productionMode ? "api.bigstockphoto.com": "testapi.bigstockphoto.com";
        address = "http://"+address+"/2/" + accountId;
        url = address + "/purchase?image_id=" + imageId + "&size_code=" + sizeCode + "&auth_key=" + sha1String;
        
        String content = getContent(url);
        BigStockPurchaseResponse response = gson.fromJson(content, BigStockPurchaseResponse.class);

        String downloadKey = getSha1Coded(key + accountId + response.data.download_id);
        String returnAddress = address + "/download?auth_key=" + downloadKey + "&download_id=" + response.data.download_id;
        return returnAddress;
    }

    private String getContent(String address) {
        URL url;
        try {
            // get URL content
            url = new URL(address);
            URLConnection conn = url.openConnection();

            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String inputLine;

            StringWriter bw = new StringWriter();

            while ((inputLine = br.readLine()) != null) {
                bw.write(inputLine);
            }

            bw.close();
            br.close();

            return bw.toString();
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        return "";
    }
}
