package com.thundashop.core.wubook;

import org.apache.xmlrpc.XmlRpcClient;

import java.util.Vector;
import java.util.concurrent.*;

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
        mgr.logPrint(Thread.currentThread().getName() + " " + getClass() + " " + "Starting thread...");

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<Vector> task = () -> (Vector) client.execute(action, params);
        Future<Vector> taskFuture = executor.submit(task);

        Vector result;
        try {
            mgr.logPrint(Thread.currentThread().getName() + " " + getClass() + "Calling wubookManger api, apiCall: " + action + " params: " + params);
            result = taskFuture.get(3, TimeUnit.MINUTES);
            mgr.logPrint(getClass() + "Response from wubookManager api, apiCall: " + action + " response: " + result);
            if ((Integer)result.get(0) != 0) {
                mgr.logText("Failed to update availability (" + result.get(0) + ") " + result.get(1) + " Parameters sent: " + params.toString() );
            } else {
                mgr.logText("Availability successfully updated.");
            }
        } catch (Exception ex) {
            mgr.logText(Thread.currentThread().getName() + " " + getClass() + " Exception : " + ex.getMessage());
            mgr.messageManager.sendErrorNotification(Thread.currentThread().getName() + " " + getClass() + " params: " + params + " Exception " + ex.getMessage(), ex);
        } finally {
            taskFuture.cancel(true);
            executor.shutdownNow();
        }

    }

    
}
