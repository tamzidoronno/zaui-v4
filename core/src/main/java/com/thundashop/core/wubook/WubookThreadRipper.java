package com.thundashop.core.wubook;

import static com.stripe.net.OAuth.token;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;

public class WubookThreadRipper extends Thread {

    private final WubookManager manager;
    private final Integer type;
    private String lcode;
    private XmlRpcClient client;
    private String token;
    private String storeId;
    
    public WubookThreadRipper(WubookManager manager, Integer type) {
        this.type = type;
        this.manager = manager;
    }
    
    public void setWubookSettings(String token, String lcode, XmlRpcClient client) {
        this.token = token;
        this.client = client;
        this.lcode = lcode;
    }
    
    @Override
    public void run() {
        manager.logPrint(Thread.currentThread().getName() + " " + getClass() + " " + "Starting thread... opType: " + type);
        if(type == 1) { fetchNewBookings(); }
        if(type == 2) { updateShortAvailability(); }
    }
    
    
    private Vector executeClient(String apicall, Vector params) throws XmlRpcException, IOException {
        
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
        new javax.net.ssl.HostnameVerifier(){

            public boolean verify(String hostname,
                    javax.net.ssl.SSLSession sslSession) {
                return true;
            }
        });
        
        manager.logText("Executing api call: " + apicall);
        try {
            manager.logPrint(Thread.currentThread().getName() + " " + getClass() + "Calling wubookManger api, apiCall: " + apicall + " params: " + params);
            Vector res = (Vector) client.execute(apicall, params);
            manager.logPrint(Thread.currentThread().getName() + " " + getClass() + "Response from wubookManager api, apiCall: " + apicall + " response: " + res);
            return res;
        }catch(Exception d) {
            manager.logText("Could not connect to wubook on api call: " + apicall + " message: " + d.getMessage());
            manager.messageManager.sendErrorNotification(Thread.currentThread().getName() + " " + getClass() + " Exception while calling wubook, apiCall: " + apicall + " params: " + params + " error: " + d.getMessage(), d);
            manager.disableWubook = new Date();
            manager.logPrintException(d);
        }
        return null;
    }
    
    public void fetchNewBookings() {
        manager.logPrint(Thread.currentThread().getName() + " " + getClass() + " fetch Booking Thread Is Running: " + manager.fetchBookingThreadIsRunning);
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

            Vector result = executeClient("fetch_new_bookings", params);
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
            manager.logPrintException(d);
            manager.messageManager.sendErrorNotification(Thread.currentThread().getName() + " " + getClass() + " Exception while calling wubook, apiCall: fetch_new_bookings" + " error: " + d.getMessage(), d);
        }
        manager.fetchBookingThreadIsRunning = false;
        
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
