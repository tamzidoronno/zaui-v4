/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.storemanager;

import com.getshop.javaapi.GetShopApi;
import java.util.UUID;

/**
 *
 * @author ktonder
 */
public class GSEnvironment {
    private int port;
    private String host;
    private String name;

    public GSEnvironment(int port, String host, String name) {
        this.port = port;
        this.host = host;
        this.name = name;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getName() {
        return name;
    }
    
    
    public GetShopApi getApi(String webAddress) throws Exception {
        String sessionId = UUID.randomUUID().toString();
        GetShopApi api = new GetShopApi(getPort(), getHost(), sessionId, webAddress);
        return api;
    }
    
}
