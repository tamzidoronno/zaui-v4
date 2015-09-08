
import java.net.InetSocketAddress;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author ktonder
 */
public class Main extends WebSocketServer {
 
    public Main() {
        super(new InetSocketAddress(31330));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Open");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //Handle client received message here
        //send a message back:
        System.out.println("Msg");
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Close");
    }

    @Override
    public void onError(WebSocket conn, Exception exc) {
        System.out.println("Err");
    }

    public static void main(String[] args) {
        Main server = new Main();
        server.start();
    }
}