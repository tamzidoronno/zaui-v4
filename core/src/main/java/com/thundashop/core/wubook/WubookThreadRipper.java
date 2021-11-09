package com.thundashop.core.wubook;

import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.AppContext;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.BeansException;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

public class WubookThreadRipper extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(WubookThreadRipper.class);

    private final WubookManager manager;
    private final Map<String, String> mdcContext;
    private final Integer type;
    private String lcode;
    private XmlRpcClient client;
    private String token;
    private String storeId;
    private GetShopSessionScope scope;

    public WubookThreadRipper(WubookManager manager, Integer type, Map<String, String> mdcContext) {
        this.type = type;
        this.manager = manager;
        this.mdcContext = mdcContext;
        try {
            scope = AppContext.appContext.getBean(GetShopSessionScope.class);
        } catch (BeansException ex) {
            logger.error("sid-{} Cannot find GetShopSessionScope bean defined for WubookThreadRipper.", storeId);
        }
    }

    
    public void setWubookSettings(String token, String lcode, XmlRpcClient client) {
        this.token = token;
        this.client = client;
        this.lcode = lcode;
    }
    
    @Override
    public void run() {
        scope.setStoreId(storeId, "", null);
        MDC.setContextMap(mdcContext);
        logger.debug("Starting thread... opType: {}", type);
        try {
            doRealStuff();
        }catch (Exception e){
            logger.error("Execution of (1)fetchNewBookings or (2)updateShortAvailability failed: {}", type);
        }finally {
            scope.removethreadStoreId(storeId);
            mdcContext.forEach((k, v) -> MDC.remove(k));
        }
    }

    private void doRealStuff() {
        if(type == 1) { fetchNewBookings(); }
        if(type == 2) { updateShortAvailability(); }
    }


    private Vector executeClient(String apicall, Vector params) {

        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> true);

        manager.logText("Executing api call: " + apicall);

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<Vector> task = () -> (Vector) client.execute(apicall, params);
        Future<Vector> taskFuture = executor.submit(task);

        try {
            logger.info("Calling wubookManger api, apiCall: {} , params: {}", apicall, params);
            StopWatch stopWatch = new StopWatch("Api Call: " + apicall);
            stopWatch.start();

            Vector res = taskFuture.get(3, TimeUnit.MINUTES);

            stopWatch.stop();
            logger.info("Executed api: {} , time: {} , response: {}", apicall, stopWatch, res);
            return res;
        } catch (Exception d) {
            String errStr = "Could not connect to wubook on api call: " + apicall + " message: " + d.getMessage();
            manager.logText(errStr);
            manager.messageManager.sendErrorNotification(Thread.currentThread().getName() + " " + getClass() + " Exception while calling wubook, apiCall: " + apicall + " params: " + params + " error: " + d.getMessage(), d);
            manager.disableWubook = new Date();
            logger.error("Could not connect to wubook on api call: {}", apicall, d);
            throw new RuntimeException(errStr, d);
        } finally {
            taskFuture.cancel(true);
            executor.shutdownNow();
        }

    }
    
    public void fetchNewBookings() {
        logger.debug(" fetch Booking Thread Is Running: " + manager.fetchBookingThreadIsRunning);
        if(manager.fetchBookingThreadIsRunning) {
            manager.logText(Thread.currentThread().getName() + " " + getClass() + " A thread already running for fetchnewbooking");
            Calendar cal = Calendar.getInstance();
            long diff = System.currentTimeMillis() - manager.fetchBookingThreadStarted.getTime();
            diff = diff / 10000;
            manager.logText(Thread.currentThread().getName() + " " + getClass() + " diff from last running time: " + diff);
            if(diff > 600) {
                manager.fetchBookingThreadIsRunning = false;
            }
            return;
        }
        manager.fetchBookingThreadStarted = new Date();
        manager.logText(Thread.currentThread().getName() + " " + getClass() + " Acquiring lock at: " + manager.fetchBookingThreadStarted);
        manager.fetchBookingThreadIsRunning = true;
        try {
            markBookingsFetched();
            manager.bookingsToAdd = null;

            Vector params = new Vector();
            params.addElement(token);
            params.addElement(lcode);
            params.addElement(1);
            
            if(isCityLiving()) {
                params.addElement(0);
            } else {
                params.addElement(1);
            }

            logger.info("Calling wubookManger api, apiCall: {} , params: {}", "fetch_new_bookings", params);
            StopWatch stopWatch = new StopWatch("Api Call: fetch_new_bookings");
            stopWatch.start();

            Vector result = executeClient("fetch_new_bookings", params);

            stopWatch.stop();
            logger.info("Executed api: {} , time: {} , response: {}", "fetch_new_bookings", stopWatch, result);

            if(result == null) {
                manager.fetchBookingThreadIsRunning = false;
                return;
            }

            if (!result.get(0).equals(0)) {
                manager.logText("0:" + result.get(0));
                manager.logText("1:" + result.get(1));
            } else {
                manager.bookingsToAdd = (Vector) result.get(1);
            }
        }catch(Exception d) {
            manager.logText(Thread.currentThread().getName() + " " + getClass() +"Failed in fetch new booking " + d.getMessage());
            logger.error("Failed in fetch new booking", d);
            manager.messageManager.sendErrorNotification(Thread.currentThread().getName() + " " + getClass() + " Exception while calling wubook, apiCall: fetch_new_bookings" + " error: " + d.getMessage(), d);
        }finally {
            manager.fetchBookingThreadIsRunning = false;
        }

    }

    private void updateShortAvailability() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void markBookingsFetched() throws XmlRpcException, IOException {
        if(manager.bookingsToAdd== null) {
            return;
        }
        
        Vector bookings = manager.bookingsToAdd;
        if(bookings.size() > 0) {
//            List<Integer> reservationCodes = new ArrayList();
            Vector reservationCodes = new Vector();
            int bookingSize = bookings.size();
            for(int bookcount = 0; bookcount < bookingSize; bookcount++) {
                Hashtable reservation = (Hashtable) bookings.get(bookcount);
                Integer reservationCode = (Integer) reservation.get("reservation_code");
                reservationCodes.add(reservationCode);
            }
            
            Vector params = new Vector();
            params.addElement(token);
            params.addElement(lcode);
            params.addElement(reservationCodes);
            Vector marked = executeClient("mark_bookings", params);       
        }

    }

    void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    private boolean isCityLiving() {
        if(storeId.equals("7bb18e4a-7a5c-4a0a-9a59-7e7705f0f004")) {
            return true;
        }
        if(storeId.equals("a4548012-433e-47a4-b154-ac47c4b7b0ed")) {
            return true;
        }
        return false;
    }
    
}
