package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thundashop.core.common.CachingKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author boggi
 */
public class CacheManager extends Thread {

    private List<CacheManagerThread> connections;
    private HashMap<String, HashMap<CachingKey, Object>> storeCacheKeys;
    int port = 25555;
    private ServerSocket serverSocket;

    public void run() {
        startListener();
    }

    private void startListener() {
        try {
            connections = new ArrayList();
            serverSocket = new ServerSocket(this.port);
            storeCacheKeys = new HashMap();
            System.out.println("Listening for chaching connections on: " + this.port);
        } catch (IOException ex) {
            System.out.println("");
            System.out.println("=============================================================================================");
            System.out.println("= Was not able to bind port, check if you have access and that no other programs is running =");
            System.out.println("=============================================================================================");
            System.out.println("");
            System.exit(0);
        }

        while (true) {
            try {              
                Socket socket = serverSocket.accept();
                CacheManagerThread thread = new CacheManagerThread(socket);
                thread.setConnectionPool(connections);
                thread.start();
                System.out.println("Cache server " + socket.getInetAddress().getHostAddress() + " connected");
                connections.add(thread);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void addToCache(CachingKey key, Object result, String storeId, String addr) {
        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
        CachingKey newkey = createCachingKey(key);
        String cachedKey = gson.toJson((Object) newkey);
        String cachedResult = gson.toJson((Object) result);
        for(CacheManagerThread connection : connections) {
            if(connection.getAddr().equals(addr)) {
                connection.updateRemoteCache(cachedKey, cachedResult, storeId);
            } else {
                List<CacheManagerThread> connectionsToAddTo = new ArrayList();
                for(String tmpKey : connection.getKeys()) {
                    if(tmpKey.equals(cachedKey)) {
                        connectionsToAddTo.add(connection);
                    }
                }
                
                for(CacheManagerThread con : connectionsToAddTo) {
                    con.updateRemoteCache(cachedKey, cachedResult, storeId);
                }
            }
        }
        HashMap<CachingKey, Object> cachedObjects = getStoreCachedList(storeId);
        
        cachedObjects.put(key, result);
    }
    
    public HashMap<CachingKey, Object> getAllCachedObjects(String storeId) {
        return getStoreCachedList(storeId);
    }

    public void removeFromCache(CachingKey toRemove, String storeId, String addr) {
        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
        CachingKey newkey = createCachingKey(toRemove);
        String cachedKey = gson.toJson((Object) newkey);
        for(CacheManagerThread connection : connections) {
            List<String> keysToRemove = new ArrayList();
            for(String tmpKey : connection.getKeys()) {
                if(tmpKey.equals(cachedKey)) {
                    keysToRemove.add(cachedKey);
                }
            }
            
            for(String key : keysToRemove) {
                connection.removeRemoteCache(key, storeId);
            }
        }
    }

    private CachingKey createCachingKey(CachingKey key) {
        Gson gson = new GsonBuilder().serializeNulls().disableInnerClassSerialization().create();
        CachingKey newkey = new CachingKey();
        if(key.args instanceof Map) {
            LinkedHashMap<String, Object> toLoop = (LinkedHashMap) key.args;
            LinkedHashMap<String, Object> replace = new LinkedHashMap();
            for(String argKey : toLoop.keySet()) {
                replace.put(argKey, gson.toJson((Object) toLoop.get(argKey)));
            }
            newkey.args = replace;
        } 
        
        if(key.args instanceof List) {
            List<Object> toLoop = (ArrayList) key.args;
            List<Object> replace = new ArrayList();
            for(Object argKey : toLoop) {
                replace.add(gson.toJson(argKey));
            }
            newkey.args = replace;
        }
        
        newkey.method = key.method;
        newkey.sessionId = key.sessionId;
        newkey.interfaceName = key.interfaceName;
        return newkey;
    }

    public void executeOKSignal(String addr) {
        for(CacheManagerThread cmt : connections) {
            if(cmt.getAddr().equals(addr)) {
                cmt.executeOK();
            }
        }
    }

    private HashMap<CachingKey, Object> getStoreCachedList(String storeId) {
        HashMap<CachingKey, Object> result = storeCacheKeys.get(storeId);
        
        if(result == null) {
            result = new HashMap();
            storeCacheKeys.put(storeId, result);
        }
        
        return result;
    }
    
}
