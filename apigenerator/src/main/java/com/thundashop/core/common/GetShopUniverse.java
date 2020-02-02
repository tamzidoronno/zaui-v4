/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.javaapi.core.Pms;
import com.getshop.javaapi.pos.Central;
import com.getshop.javaapi.pos.Pos;
import com.getshop.javaapi.pos.Seros;
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
    private String store;

    public GetShopUniverse(String serverIp, String store) {
        this.serverIp = serverIp;
        this.store = store;
    }
    
    public GetShopUniverse(int cluster, String store) {
        this.cluster = cluster;
        this.store = store;
    }
    
    public Seros getSerosLockSystem() throws Exception {
        if (this.transporter != null && !createdForSystemType.equals("seros")) {
            throw new RuntimeException("This instance has already been initalized for another system type, please create a new instance if you need to communicate with multiple systems");
        }
        if (this.transporter == null) {
            setTransporter("seros");
        }
        
        Seros api = new Seros(transporter);
        api.getStoreManager().initializeStore(store, sessionId);
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
        api.getStoreManager().initializeStore(store, sessionId);
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
        api.getStoreManager().initializeStore(store, sessionId);
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
        api.getStoreManager().initializeStore(store, sessionId);
        return api;
    }

    private void setTransporter(String systemtype) {
        int port = 25554;
        
        String clusterIp = "10.0."+cluster+".33";
        
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

    public void initalize(String gkroengetshopcom) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
