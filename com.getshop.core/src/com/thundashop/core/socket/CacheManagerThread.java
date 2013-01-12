package com.thundashop.core.socket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.StoreHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author boggi
 */
public class CacheManagerThread extends Thread {

    private final Socket socket;
    private List<String> keysAdded;
    private PrintWriter bw;
    private List<CacheManagerThread> connections;
    private BufferedReader br;

    CacheManagerThread(Socket socket) {
        System.out.println("Creating thread");
        this.socket = socket;
        keysAdded = new ArrayList();
        try {
            bw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String line = br.readLine();
                if (line == null) {
                    System.out.println(this.getAddr() + " got disconnected");
                    connections.remove(this);
                    break;
                } else if (line.equals("log")) {
                    line = br.readLine();
                    try {
                        logApiCall(line);
                    } catch (ErrorException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAddr() {
        return socket.getInetAddress().getHostAddress();
    }

    public void updateRemoteCache(String key, String json, String storeId) {
        try {
            keysAdded.add(key);
            bw.println("addCacheEntry");
            bw.println(storeId);
            bw.println(key);
            bw.println(json);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeRemoteCache(String key, String storeId) {
        try {
            System.out.println("Removing key: " + key + " addr: " + getAddr());
            keysAdded.remove(key);
            bw.println("removeCacheEntry");
            bw.println(storeId);
            bw.println(key);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getKeys() {
        return keysAdded;
    }

    void executeOK() {
        try {
            bw.println("OK");
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void setConnectionPool(List<CacheManagerThread> connections) {
        this.connections = connections;
    }

    private void logApiCall(String message) throws ErrorException {
        Gson gson = new GsonBuilder().serializeNulls().create();

        Type type = new TypeToken<JsonObject2>() {
        }.getType();

        message = message.replace("\"args\":[]", "\"args\":{}");

        JsonObject2 object = gson.fromJson(message, type);
        object.interfaceName = object.interfaceName.replace(".I", ".");

        StoreHandler handler = AppContext.storePool.getStoreHandler(object.sessionId);
        if(handler != null) {
            handler.logApiCall(object);
        }
    }
}
