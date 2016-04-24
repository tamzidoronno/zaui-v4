/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.socket;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.StoreHandler;
import com.thundashop.core.common.StorePool;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class CacheFactory {
    private final HashMap<String, Boolean> canCache = new HashMap();
    private StorePool storePool;
    private boolean enabled = true;

    public void setStorePool(StorePool storePool) {
        this.storePool = storePool;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCachedResult(String message, String addr) {
        JsonObject2 object = storePool.createJsonObject(message, addr);

        String md5Sum = readMd5Sum(object);
        
        if (!canCache(object, message, md5Sum)) {
            return null;
        }

        return getFileContent(md5Sum, object);
    }

    public void writeContent(String message, String addr, String jsonContent) {
        JsonObject2 object = storePool.createJsonObject(message, addr);
        String md5Sum = readMd5Sum(object);
        
        if (!canCache(object, message, md5Sum)) {
            return;
        }

        Path path = createPath(md5Sum, object);
        try {
            Files.write(path, jsonContent.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean canCache(JsonObject2 object, String message, String md5) throws ErrorException {
        if (!enabled) {
            return false;
        }

        StoreHandler handler = storePool.getStoreHandler(object.sessionId);

        if (handler == null) {
            return false;
        }

        // Cant cache init store, this needs to be run whatever happens.
        if (object.interfaceName.equals("core.storemanager.StoreManager") && object.method.equals("initializeStore")) {
            return true;
        }
        Class[] types = storePool.getArguments(object);
        Object[] args = storePool.createExecuteArgs(object, types, message);
        boolean isPublic = handler.isPublicMethod(object, types, args);

        if (!isPublic) {
            return false;
        }

        Boolean booleanCanCache = canCache.get(md5);
        
        
        
        if (booleanCanCache != null && booleanCanCache.equals(Boolean.TRUE)) {
//            System.out.println("Caching");
            return true;
        }
        
        return false;
    }

    private String readMd5Sum(JsonObject2 object) {
        JsonObject2 clonedObject;
        try {
            clonedObject = (JsonObject2) object.clone();
            clonedObject.sessionId = "";

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(clonedObject);
            byte[] inOjectArray = bos.toByteArray();

            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digested = md.digest(inOjectArray);

            return convert(digested);
        } catch (CloneNotSupportedException | NoSuchAlgorithmException | IOException ex) {
            Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "";
    }

    private String convert(byte[] hash) {
        StringBuffer hexString = new StringBuffer();

        for (int i = 0; i < hash.length; i++) {
            if ((0xff & hash[i]) < 0x10) {
                hexString.append("0"
                        + Integer.toHexString((0xFF & hash[i])));
            } else {
                hexString.append(Integer.toHexString(0xFF & hash[i]));
            }
        }

        return hexString.toString();
    }

    private String getFileContent(String md5Sum, JsonObject2 inObject) {
        Path path = createPath(md5Sum, inObject);

        if (Files.isReadable(path)) {
            try {
                return new String(Files.readAllBytes(path));
            } catch (IOException ex) {
                return null;
            }
        }

        return null;
    }

    private String getStoreCacheFolder(String storeId) {
        String folder = "/tmp/testcache/" + storeId;
        return folder;
    }

    private Path createPath(String md5Sum, JsonObject2 object) {
        String folder = getStoreCacheFolder(storePool.getStoreHandler(object.sessionId).storeId) + "/" + object.interfaceName;

        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }

        Path path = Paths.get(folder + "/" + md5Sum);
        return path;
    }

    public void clear(String storeId, String simpleName) {
        File folder = new File(getStoreCacheFolder(storeId));

        final File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(final File dir, final String name) {
                return name.contains(simpleName);
            }
        });

        if(files != null ) {
            for (final File file : files) {
                deleteDirectory(file);
            }
        }
    }

    public boolean deleteDirectory(File directory) {
        if (directory.exists()) {
            File[] files = directory.listFiles();
            if (null != files) {
                for (int i = 0; i < files.length; i++) {
                    if (files[i].isDirectory()) {
                        deleteDirectory(files[i]);
                    } else {
                        files[i].delete();
                    }
                }
            }
        }
        return (directory.delete());
    }

    
    public void notify(JsonObject2 inObject, boolean wasUsed, String message, String addr) {
        JsonObject2 object = storePool.createJsonObject(message, addr);
        String md5Sum = readMd5Sum(object);
        canCache.put(md5Sum, !wasUsed);
    }

}
