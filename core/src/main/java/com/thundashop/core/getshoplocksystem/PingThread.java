/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.thundashop.core.pmsmanager.PingServerThread;
import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author boggi
 */
class PingThread extends Thread {

    private final LockServer server;

    public PingThread(LockServer server) {
        this.server = server;
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
            }
        }catch(Exception e) {
        }
    }
    
}
