package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.AppContext;
import org.apache.xmlrpc.XmlRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StopWatch;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.*;

public class WubookManagerUpdateThread extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(WubookManagerUpdateThread.class);

    private final Vector params;
    private final WubookManager mgr;
    private final XmlRpcClient client;
    private final String action;
    private final Map<String, String> mdcContext;
    private final String storeId;
    private final GetShopSessionScope scope;

    WubookManagerUpdateThread(String action, XmlRpcClient client, WubookManager mgr, Vector params, String storeId,Map<String, String> mdcContext) {
        this.client = client;
        this.mgr = mgr;
        this.params = params;
        this.action = action;
        this.storeId = storeId;
        scope = AppContext.appContext.getBean(GetShopSessionScope.class);
        this.mdcContext = mdcContext;
    }
    
    @Override
    public void run() {
        MDC.setContextMap(mdcContext);
        mgr.logPrint(Thread.currentThread().getName() + " " + getClass() + " " + "Starting thread...");
        scope.setStoreId(storeId, "", null);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<Vector> task = () -> (Vector) client.execute(action, params);
        Future<Vector> taskFuture = executor.submit(task);

        Vector result;
        try {
            logger.info("Calling wubookManger api, apiCall: {} , params: {}", action, params);
            StopWatch stopWatch = new StopWatch("Api Call: " + action);
            stopWatch.start();

            result = taskFuture.get(3, TimeUnit.MINUTES);

            stopWatch.stop();
            logger.info("Executed api: {} , time: {} , response: {}", action, stopWatch, result);
            if ((Integer)result.get(0) != 0) {
                mgr.logText("Failed to update availability (" + result.get(0) + ") " + result.get(1) + " Parameters sent: " + params.toString() );
            } else {
                mgr.logText("Availability successfully updated.");
            }
        } catch (Exception ex) {
            mgr.logText(Thread.currentThread().getName() + " " + getClass() + " Exception : " + ex.getMessage());
            logger.error("Exception while calling wubook api: {}", action, ex);
            mgr.messageManager.sendErrorNotification(Thread.currentThread().getName() + " " + getClass() + " params: " + params + " Exception " + ex.getMessage(), ex);
        } finally {
            taskFuture.cancel(true);
            executor.shutdownNow();
            scope.removethreadStoreId(storeId);
            mdcContext.forEach((k, v) -> MDC.remove(k));
        }

    }

    
}
