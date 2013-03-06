package com.getshop.syncserver;

import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author boggi
 */
public class Runner {

    public static void main(String[] args) {
                ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(25557);
            while(true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                handler.start();
            }
        }catch(Exception e) {
            
        }
    }
}
