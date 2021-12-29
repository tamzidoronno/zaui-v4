
package com.thundashop.core.pmsmanager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.Date;

/**
 *
 * @author boggi
 */
public class PingServerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(PingServerThread.class);

    private final PmsLockServer server;
    
    public PingServerThread(PmsLockServer server) {
        this.server = server;
    }
    
    @Override
    public void run() {
        try {
            String host = server.arxHostname;
            if(host.contains(":")) {
                host = host.substring(0,host.indexOf(":"));
            }
            host = host.replace("http://", "");
            host = host.replace("https://", "");
            InetAddress inet = InetAddress.getByName(host);
            if(inet.isReachable(5000)) {
                this.server.lastPing = new Date();
            } else {
                logger.warn("Server is not reachable, arxHostname: {}", server.arxHostname);
            }
        } catch (Exception e) {
            logger.error("Error while ping server arxHostname: {}", server.arxHostname, e);
        }
    }
}
