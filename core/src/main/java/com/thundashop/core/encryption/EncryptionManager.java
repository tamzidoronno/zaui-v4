/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.encryption;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.messagemanager.MessageManager;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import javax.crypto.Cipher;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * THIS COMPONENT SHOULD NOT BE EXPOSED
 * TO THE API, THEREFOR IT DOES NOT IMPLEMENT
 * A INTERFACE WITH GETSHOPAPI ANNOTATION!
 * 
 * @author ktonder
 */
@Component
@GetShopSession
public class EncryptionManager extends ManagerBase  {
    private PublicEncryptionKey pubKey = null;
    
    @Autowired
    private MessageManager messageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof PublicEncryptionKey) {
                this.pubKey = (PublicEncryptionKey)dataCommon;
            }
        }
    }
    
    
    public String decryption(String privateKey, byte[] encryptedBytes) {
        makeKeyIfNescerry();
        
        try {
            PrivateKey key = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey)));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.PRIVATE_KEY, key);

            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

            return new String(decryptedBytes, "UTF-8");
        } catch (Exception ex) {
            logPrint(ex);
//            throw new RuntimeException("Not able to decrypt data");
            return "N/A/T/D";
        }
    }

    public byte[] encrypt(String data) {
        makeKeyIfNescerry();
        
        try {
            PublicKey key = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(pubKey.key));

            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.PUBLIC_KEY, key);

            byte[] inputData = data.getBytes("UTF-8");
            byte[] encryptedBytes = cipher.doFinal(inputData);
            return encryptedBytes;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Not able to encrypt");
        }
    }

    private void makeKeyIfNescerry() {
        if (pubKey != null) {
            return;
        }
        
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            KeyPair generateKeyPair = keyGen.generateKeyPair();
            
            String keyData = Base64.encodeBase64String(generateKeyPair.getPrivate().getEncoded());
            
            String email = getStoreEmailAddress();
            HashMap<String, String> attachments = new HashMap();
            attachments.put("private.key", keyData);
            messageManager.sendMailWithAttachments(email, "Store Owner", "Private decryption key", "Hi! <br/><br/> Attached is the private decryption key, please save it at a secure and safe place. This file is private and should not be shared with anyone. If you loose this file you will not be able to decrypt the encrypted data stored in your system.<br/><br/> Best Regards<br/> GetShop Systems", "post@getshop.com", "GetShop System", attachments);
            
            pubKey = new PublicEncryptionKey();
            pubKey.key = generateKeyPair.getPublic().getEncoded();
            saveObject(pubKey);
        } catch (Exception ex) {
            logPrintException(ex);
            throw new ErrorException(86);
        }
        
    }
    
}
