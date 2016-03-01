
package com.thundashop.core.arx;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ArxLogFetcherThread extends Thread {
    public String result = "";
    
    private ArxManager arxManager;
    String username = "";
    String password = "";
    String hostname = "";

    ArxLogFetcherThread(ArxManager mgr) {
        this.arxManager = mgr;
    }
    
    public void run() {
        while(true) {
            arxManager.overrideCredentials(hostname, username, password);
            long start = System.currentTimeMillis() - (60 * 1000);
//            System.out.println("Timediff:" +  start + " - " + System.currentTimeMillis());
            result = arxManager.getDoorLogForced(start, System.currentTimeMillis());
            try {
                Thread.sleep(10000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ArxLogFetcherThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
