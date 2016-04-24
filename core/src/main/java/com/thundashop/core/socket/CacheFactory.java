/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.socket;

import com.getshop.scope.GetShopSessionScope;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.JsonObject2;
import com.thundashop.core.common.SerializationExcludeStragety;
import com.thundashop.core.common.StoreHandler;
import static com.thundashop.core.common.StoreHandler.getJsonSerializer;
import com.thundashop.core.common.StorePool;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
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
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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
    
    @Autowired
    private GetShopSessionScope getShopSessionScope;

    public void setStorePool(StorePool storePool) {
        this.storePool = storePool;
    }
    
    /**
     * We delete all the cache on startup
     */
    @PostConstruct
    public void clearAllCache() {
        if (!enabled)
            return;
        
        File file = new File(getStoreCacheFolder(""));
        if (file.isDirectory()) {
            file.mkdir();
        }
        
        File folder = new File(getStoreCacheFolder(""));
        final File[] files = folder.listFiles();
        
        for (final File dir : files) {
            deleteDirectory(dir);
        }
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getCachedResult(String message, String addr) {
        if (!enabled) {
            return null;
        }

        JsonObject2 object = storePool.createJsonObject(message, addr);

        String md5Sum = readMd5Sum(object);
        
        if (!canCache(object, message, md5Sum)) {
            return null;
        }

        String noSecurityNeededResult = getFileContentNoSecurity(md5Sum, object);
        
        if (noSecurityNeededResult != null) {
            return noSecurityNeededResult;
        }
        
        Object cachedResult = getFileContent(md5Sum, object);
        return runTroughSecurity(cachedResult, object, md5Sum);
    }

    public void writeContent(String message, String addr, Object jsonResult) {
        if (!enabled) {
            return;
        }
        
        if (jsonResult == null) {
            return;
        }
        
        JsonObject2 object = storePool.createJsonObject(message, addr);
        String md5Sum = readMd5Sum(object);
        
        if (!canCache(object, message, md5Sum)) {
            return;
        }

        String path = createPath(md5Sum, object);
        try {
            FileOutputStream fout = new FileOutputStream(path);
            ObjectOutputStream oos = new ObjectOutputStream(fout);   
            oos.writeObject(jsonResult);
            oos.close();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void notify(JsonObject2 inObject, boolean wasUsed, String message, String addr) {
        if (!enabled) {
            return;
        }
        
        JsonObject2 object = storePool.createJsonObject(message, addr);
        String md5Sum = readMd5Sum(object);
        canCache.put(md5Sum, !wasUsed);
    }

    private boolean canCache(JsonObject2 object, String message, String md5) throws ErrorException {
        StoreHandler handler = storePool.getStoreHandler(object.sessionId);

        if (handler == null) {
            return false;
        }

        // Cant cache init store, this needs to be run whatever happens.
        if (object.interfaceName.equals("core.storemanager.IStoreManager") && object.method.equals("initializeStore")) {
            return false;
        }
        Class[] types = storePool.getArguments(object);
        Object[] args = storePool.createExecuteArgs(object, types, message);
        boolean isPublic = handler.isPublicMethod(object, types, args);

        if (!isPublic) {
            return false;
        }

        Boolean booleanCanCache = canCache.get(md5);
        
        if (booleanCanCache != null && booleanCanCache.equals(Boolean.TRUE)) {
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

    private Object getFileContent(String md5Sum, JsonObject2 inObject) {
        String path = createPath(md5Sum, inObject);

        Object retObject = null;
        if (Files.isReadable(Paths.get(path))) {
            try {
                FileInputStream fout = new FileInputStream(path);
                ObjectInputStream oos = new ObjectInputStream(fout);
                retObject = oos.readObject();
                oos.close();
            } catch (IOException ex) {
                return null;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return retObject;
    }

    private String getStoreCacheFolder(String storeId) {
        String folder = "/tmp/testcache/" + storeId;
        return folder;
    }

    private String createPath(String md5Sum, JsonObject2 object) {
        String storeId = storePool.getStoreHandler(object.sessionId).storeId;
        String folder = getStoreCacheFolder(storeId) + "/" + object.interfaceName;

        File file = new File(folder);
        if (!file.exists()) {
            file.mkdirs();
        }

        return folder + "/" + md5Sum;
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

    private boolean deleteDirectory(File directory) {
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

    private String runTroughSecurity(Object cachedResult, JsonObject2 object, String md5) {
        String storeId = storePool.getStoreHandler(object.sessionId).storeId;
        
        if (cachedResult == null) {
            return null;
        }
        
        UserManager userManager = (UserManager)getShopSessionScope.getManagerBasedOnNameAndStoreId("scopedTarget.userManager", storeId);
        if (userManager == null) {
            return null;
        }
        
        User user = userManager.getLoggedOnUserNotNotifySession(object.sessionId);
        SerializationExcludeStragety excludeStrategy = new SerializationExcludeStragety(user);
        Gson gson = getJsonSerializer(excludeStrategy);
        String result = gson.toJson(cachedResult);
        
        if (excludeStrategy.isNeededToSkipData()) {
            return result;
        }
        
        try {
            String folder = createPath(md5, object) + ".noSecurityNeeded";
            Files.write(Paths.get(folder), result.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

    private String getFileContentNoSecurity(String md5sum, JsonObject2 inObject) {
        String fileName = createPath(md5sum, inObject) + ".noSecurityNeeded";
        
        if (Files.exists(Paths.get(fileName))) {
            try {
                return new String(Files.readAllBytes(Paths.get(fileName)));
            } catch (IOException ex) {
                Logger.getLogger(CacheFactory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return null;
    }

}
