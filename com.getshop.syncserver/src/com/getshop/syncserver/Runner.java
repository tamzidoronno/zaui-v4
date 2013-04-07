package com.getshop.syncserver;

import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author boggi
 */
public class Runner {

    public static void main(String[] args) {
        ServerSocket serverSocket = null;
        
        File file = new File("");
        System.out.println(file.getAbsolutePath());
        try {
            serverSocket = new ServerSocket(25557);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Starting processor");
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
