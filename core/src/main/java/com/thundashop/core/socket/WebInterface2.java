/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.socket;

import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.common.StorePool;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author boggi
 */
public class WebInterface2 implements Runnable {

    private final StorePool storePool;
    private final int port;
    private Logger log;
    
    public WebInterface2(Logger log, StorePool storePool, int port) {
        this.log = log;
        this.storePool = storePool;
        this.port = port;
        Thread td = new Thread(this);
        td.setName("Web interface thread");
        td.start();
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
            log.info(this, "Listening to port: " + this.port);
        } catch (IOException ex) {
            GetShopLogHandler.logPrintStatic("", null);
            GetShopLogHandler.logPrintStatic("===================================================================================================", null);
            GetShopLogHandler.logPrintStatic("= Was not able to bind port "+ port +", check if you have access and that no other programs is running =", null);
            GetShopLogHandler.logPrintStatic("===================================================================================================", null);
            GetShopLogHandler.logPrintStatic("", null);
            System.exit(0);
        }

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new WebInterfaceSocketThread2(socket, storePool));
                thread.setName("WebSocket client thread");
                thread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
