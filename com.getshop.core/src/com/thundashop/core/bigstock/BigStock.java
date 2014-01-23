/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bigstock;

import com.google.gson.Gson;
import com.thundashop.core.bigstock.data.BigStockCreditAccount;
import com.thundashop.core.bigstock.data.BigStockOrder;
import com.thundashop.core.bigstock.data.BigStockPurchaseResponse;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
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
import java.util.Date;
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
    
    private BigStockCreditAccount bigStockCreditAccount = new BigStockCreditAccount();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof BigStockCreditAccount) {
                bigStockCreditAccount = (BigStockCreditAccount)dataCommon;
            }
        }
    }
    
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
        int imageCost = 6;
        
        if (bigStockCreditAccount.creditAccount < imageCost) {
            throw new ErrorException(102);
        }
        
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
        
        addBigStockOrder(accountId, key, sha1String, url, imageId, downloadKey, response, sizeCode, returnAddress, imageCost);
        return returnAddress;
    }

    private void addBigStockOrder(String accountId, String key, String sha1String, String url, String imageId, String downloadKey, BigStockPurchaseResponse response, String sizeCode, String returnAddress, int credit) throws ErrorException {
        BigStockOrder order = new BigStockOrder();
        order.accountId = accountId;
        order.accountKey = key;
        order.purchaseSha1String = sha1String;
        order.purchaseUrl = url;
        order.imageId = imageId;
        order.downloadKey = downloadKey;
        order.respone = response;
        order.sizeCode = sizeCode;
        order.downloadAddress = returnAddress;
        order.purchaseDate = new Date();
        order.credit = credit;
        bigStockCreditAccount.addOrder(order);
        
        databaseSaver.saveObject(bigStockCreditAccount, credentials);
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

    @Override
    public void setCreditAccount(int credits, String password) throws ErrorException {
        if (!password.equals("as0f9820q9345uj12lø5n1k2j5bhklhj651ølkj345øqlwgfkdjklshgjebn6k12b43523.r-q3.45,-24.35,1.-2m3541.65j23945678231oi541hn-sfd-13245m2.345,2-3.45,1")) {
            throw new ErrorException(26);
        }
        
        bigStockCreditAccount.creditAccount = credits;
        bigStockCreditAccount.storeId = storeId;
        databaseSaver.saveObject(bigStockCreditAccount, credentials);
    }

    @Override
    public int getAvailableCredits() {
        return bigStockCreditAccount.creditAccount;
    }

    @Override
    public void addGetShopImageIdToBigStockOrder(String downloadUrl, String imageId) throws ErrorException {
        bigStockCreditAccount.addGetShopImageId(downloadUrl, imageId);
        databaseSaver.saveObject(bigStockCreditAccount, credentials);
    }

}
