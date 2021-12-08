/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author boggi
 */
class PingThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PingThread.class);

    private final LockServer server;
    private final String storeId;

    public PingThread(LockServer server, String storeId) {
        this.server = server;
        this.storeId = storeId;
    }
    
    
    @Override
    public void run() {
        try {
            String host = server.getHostname();
            if(host.contains(":")) {
                host = host.substring(0,host.indexOf(":"));
            }
            host = host.replace("http://", "");
            host = host.replace("https://", "");
            InetAddress inet = InetAddress.getByName(host);
            if(inet.isReachable(5000) || server.getLastPing() == null) {
                this.server.setLastPing(new Date());
            } else {
                logger.warn("Server is not reachable, storeId: {},  serverName: {}", storeId, server.getGivenName());
            }
        } catch (Exception e) {
            logger.error("Error while ping server, storeId: {} , serverName: {}", storeId, server.getGivenName(), e);
        }
    }
    
}
