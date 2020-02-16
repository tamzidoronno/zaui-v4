package com.thundashop.core.common;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

public class Communicator {

    private Socket socket;
    private boolean connected = false;
    private boolean autoReconnect = false;
    private final int port;
    public String host = "backend.getshop.com";
    public String sessionId;
    public String webaddress;
    private PrintWriter out;
    private BufferedReader in;

    public Communicator(String address, int port, String sessionId) {
        this.host = address;
        this.port = port;
        this.sessionId = sessionId;
    }

    public boolean connect() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), 2000);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());
            connected = true;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void setAutoReconnect(boolean auto) {
        autoReconnect = auto;
    }
    
    public boolean isConnected() {
        return connected;
    }

    public String send(JsonObject2 message) {
        message.sessionId = sessionId;

        String jsonMessage = new Gson().toJson(message);
        if (jsonMessage == null) {
            throw new RuntimeException("Failed converting message to json");
        }
        try {
            if (!connected) {
                reconnect();
            }
            String result = "";
            while (true) {
                out.println(jsonMessage);
                out.flush();
                result = in.readLine();
                if (result != null) {
                    break;
                } else {
                    if(!reconnect()) {
                        return null;
                    }
                } 
            }
            return result;
        } catch(Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public boolean reconnect() throws Exception {
        connected = false;
        while (true) {
            if(autoReconnect) {
                System.out.println("Reconnecting to java backend");
            }
            if (connected) {
                break;
            }
            connect();
            if(!autoReconnect) {
                break;
            }
            Thread.sleep(2000);
        }
        if(connected) {
//            api.getStoreManager().initializeStore(webaddress, sessionId);
            return true;
        }
        return false;
    }
   
    public void close() {
        try {
            out.close();
            in.close();
            socket.close();
            connected = false;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
