
import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;
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
        GetShopLogHandler.logPrintStatic("Open", null);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        //Handle client received message here
        //send a message back:
        GetShopLogHandler.logPrintStatic("Msg", null);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        GetShopLogHandler.logPrintStatic("Close", null);
    }

    @Override
    public void onError(WebSocket conn, Exception exc) {
        GetShopLogHandler.logPrintStatic("Err", null);
    }

    public static void main(String[] args) {
        Main server = new Main();
        server.start();
    }
}