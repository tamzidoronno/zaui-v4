
package com.thundashop.core.pmsmanager;

import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class PingServerThread extends Thread {
    private final PmsLockServer server;
    
    public PingServerThread(PmsLockServer server) {
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            InetAddress inet = InetAddress.getByName(server.arxHostname);
            if(inet.isReachable(5000)) {
                this.server.lastPing = new Date();
            }
        }catch(Exception e) {
        }
    }
}
