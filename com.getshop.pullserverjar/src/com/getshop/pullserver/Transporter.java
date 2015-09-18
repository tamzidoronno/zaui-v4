package com.getshop.pullserver;

import com.getshop.pullserver.GetShopApiPullserver;
import com.getshop.pullserver.JsonObject2;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Transporter {

    private Socket socket;
    private boolean connected = false;
    public int port = 25554;
    public String host = "backend.getshop.com";
    public String sessionId;
    public String webaddress;
    public GetShopApiPullserver api;
    private PrintWriter out;
    private BufferedReader in;

    public void connect() throws UnknownHostException, IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 3000);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());
        connected = true;
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
        String result = "";
        while (true) {
            out.println(jsonMessage);
            out.flush();
            result = in.readLine();
            if (result != null) {
                break;
            }
        }
        return result;
    }
    
}
