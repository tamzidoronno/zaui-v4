package com.thundashop.core.wubook;

import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class WubookManagerUpdateThread extends Thread {

    private final Vector params;
    private final WubookManager mgr;
    private final XmlRpcClient client;

    WubookManagerUpdateThread(XmlRpcClient client, WubookManager mgr, Vector params) {
        this.client = client;
        this.mgr = mgr;
        this.params = params;
    }
    
    @Override
    public void run() {
        Vector result;
        try {
            result = (Vector) client.execute("update_rooms_values", params);
            if ((Integer)result.get(0) != 0) {
                mgr.logText("Failed to update availability " + "(" + result.get(0) + ")" + result.get(1));
            } else {
                mgr.logText("Availability is being updated.");
            }
        } catch (XmlRpcException ex) {
            mgr.logText("XMLException caught: " + ex.code);
        } catch (IOException ex) {
            mgr.logText("IOException caught: " + ex.getLocalizedMessage());
        }

    }

    
}
