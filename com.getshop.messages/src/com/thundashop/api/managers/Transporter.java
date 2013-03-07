package com.thundashop.api.managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
    public int port = 25554;
    public String host = "backend.getshop.com";
    public String sessionId;
    
    
    public void connect() throws UnknownHostException, IOException {
        socket = new Socket(host, port);
        connected = true;
    }
    
    public String send(JsonObject2 message) throws Exception {
        
        message.sessionId = sessionId;
        
        String jsonMessage = new Gson().toJson(message);
        if(jsonMessage == null) {
            System.out.println("Failed converting message to json");
            System.exit(0);
        }
        if(!connected) {
            throw new Exception("Not connected to the backend");
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));        
        PrintWriter out = new PrintWriter(socket.getOutputStream());
        out.println(jsonMessage);
        out.flush();
        
        String result = in.readLine();
        return result;
    }

}
