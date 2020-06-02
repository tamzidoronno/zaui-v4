/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.javaapi.central.Central;
import com.getshop.javaapi.core.Pms;
import com.getshop.javaapi.pos.Pos;
import com.getshop.javaapi.seros.Seros;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class GetShopUniverse {
    private String sessionId = UUID.randomUUID().toString();
    private int cluster;
    private Communicator transporter;
    private String createdForSystemType = "";
    private String serverIp = null;
    private String host;
    private String storeId;
    private boolean devMode;

    public <T> T convert(JsonElement in, Class toClass) {
        Object res = new Gson().fromJson(in, toClass);
        
        if (toClass.isArray()) {
            List<T> test = Arrays.asList((T[]) res);
            res = test;
        }
        
        return (T) res;
    }
    
    public GetShopUniverse(int cluster, String storeId, boolean devMode) {
        this.cluster = cluster;
        this.storeId = storeId;
        this.devMode = devMode;
    }
    
    public Seros getSerosLockSystem() throws Exception {
        if (this.transporter != null && !createdForSystemType.equals("seros")) {
            throw new RuntimeException("This instance has already been initalized for another system type, please create a new instance if you need to communicate with multiple systems");
        }
        if (this.transporter == null) {
            setTransporter("seros");
        }
        
        Seros api = new Seros(transporter);
        api.getStoreManager().initializeStore(storeId, sessionId);
        return api;
    }
    
    public Central getCentral() throws Exception {
        if (this.transporter != null && !createdForSystemType.equals("central")) {
            throw new RuntimeException("This instance has already been initalized for another system type, please create a new instance if you need to communicate with multiple systems");
        }
        if (this.transporter == null) {
            setTransporter("central");
        }
        
        Central api = new Central(transporter);
        api.getStoreManager().initializeStore(storeId, sessionId);
        return api;
    }
    
    public Pos getPos() throws Exception {
        if (this.transporter != null && !createdForSystemType.equals("pos")) {
            throw new RuntimeException("This instance has already been initalized for another system type, please create a new instance if you need to communicate with multiple systems");
        }
        if (this.transporter == null) {
            setTransporter("pos");
        }
        
        Pos api = new Pos(transporter);
        api.getStoreManager().initializeStore(storeId, sessionId);
        return api;
    }
    
    public Pms getPms() throws Exception {
        if (this.transporter != null && !createdForSystemType.equals("pms")) {
            throw new RuntimeException("This instance has already been initalized for another system type, please create a new instance if you need to communicate with multiple systems");
        }
        if (this.transporter == null) {
            setTransporter("pms");
        }
        
        Pms api = new Pms(transporter);
        api.getStoreManager().initializeStore(storeId, sessionId);
        return api;
    }

    public SystemCreator getCreator(String systemType) {
        if (transporter == null) {
            setTransporter(systemType);
        }
        
        return new SystemCreator(transporter);
    }
    
    private void setTransporter(String systemtype) {
        int port = 25554;
        
        String subAddr = "33";
        
        if (systemtype.equals("pos")) {
            subAddr = "43";
        }
        
        if (systemtype.equals("seros")) {
            subAddr = "63";
        }
        
        if (systemtype.equals("central")) {
            subAddr = "73";
        }
        
        String clusterIp = "10.0."+cluster+"."+subAddr;
        
        if (cluster == 0 || devMode) {
            clusterIp = "127.0.0.1";
        }
        
        
        if (serverIp != null) {
            clusterIp = serverIp;
        }
        
        if (systemtype.equals("seros")) {
            port = 35554;
        }
        
        if (systemtype.equals("central")) {
            port = 35555;
        }
        
        if (systemtype.equals("pos")) {
            port = 35558;
        }
        
        if (systemtype.equals("pms")) {
            port = 25554;
        }
        
        transporter = new Communicator(clusterIp, port, sessionId);
        createdForSystemType = systemtype;
    }

    public void close() {
        System.out.println("TODO - HANDLE CLOSE FROM GETSHOP UNIVERSE!");
    }
    
    
}
