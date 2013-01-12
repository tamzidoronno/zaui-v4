/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.socket;

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

    public WebInterface2(StorePool storePool, int port) {
        this.storePool = storePool;
        this.port = port;
        new Thread(this).start();
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(this.port);
            System.out.println("Listening to port: " + this.port);
        } catch (IOException ex) {
            System.out.println("");
            System.out.println("=============================================================================================");
            System.out.println("= Was not able to bind port, check if you have access and that no other programs is running =");
            System.out.println("=============================================================================================");
            System.out.println("");
            System.exit(0);
        }

        while (true) {
            try {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new WebInterfaceSocketThread2(socket, storePool));
                thread.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
