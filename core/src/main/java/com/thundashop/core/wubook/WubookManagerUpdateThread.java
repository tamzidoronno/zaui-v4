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
    private final String action;

    WubookManagerUpdateThread(String action, XmlRpcClient client, WubookManager mgr, Vector params) {
        this.client = client;
        this.mgr = mgr;
        this.params = params;
        this.action = action;
    }
    
    @Override
    public void run() {
        Vector result;
        try {
            result = (Vector) client.execute(action, params);
            if ((Integer)result.get(0) != 0) {
                mgr.logText("Failed to update availability (" + result.get(0) + ") " + result.get(1) + " Parameters sent: " + params.toString() );
            } else {
                mgr.logText("Availability successfully updated.");
            }
        } catch (XmlRpcException ex) {
            mgr.logText("XMLException caught: " + ex.code);
        } catch (IOException ex) {
            mgr.logText("IOException caught: " + ex.getLocalizedMessage());
        }

    }

    
}
