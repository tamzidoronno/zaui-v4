package com.thundashop.api.managers;

import com.google.gson.Gson;
import com.thundashop.core.common.JsonObject2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Transporter {

    private Socket socket;
    private boolean connected = false;
    private boolean autoReconnect = true;
    public int port = 25554;
    public String host = "backend.getshop.com";
    public String sessionId;
    public String webaddress;
    public GetShopApi api;
    private PrintWriter out;
    private BufferedReader in;

    public boolean connect() throws UnknownHostException, IOException {
        try {
            socket = new Socket(host, port);
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

    public String send(JsonObject2 message) throws Exception {
        message.sessionId = sessionId;

        String jsonMessage = new Gson().toJson(message);
        if (jsonMessage == null) {
            System.out.println("Failed converting message to json");
            System.exit(0);
        }
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
            api.getStoreManager().initializeStore(webaddress, sessionId);
            return true;
        }
        return false;
    }
}
