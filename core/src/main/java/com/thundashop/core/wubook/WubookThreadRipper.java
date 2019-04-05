package com.thundashop.core.wubook;

import static com.stripe.net.OAuth.token;
import java.io.IOException;
import java.util.ArrayList;
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
        try {
            if(type == 1) { fetchNewBookings(); }
            if(type == 2) { updateShortAvailability(); }
        } catch (XmlRpcException ex) {
            manager.logPrintException(ex);
        } catch (IOException ex) {
            manager.logPrintException(ex);
        }
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
            Vector res = (Vector) client.execute(apicall, params);
            return res;
        }catch(Exception d) {
            manager.logPrint("Could not connect to wubook on api call: " + apicall + " message: " + d.getMessage());
            manager.disableWubook = new Date();
        }
        return null;
    }
    
    public void fetchNewBookings() throws XmlRpcException, IOException {
        if(manager.fetchBookingThreadIsRunning) {
            return;
        }
        manager.fetchBookingThreadIsRunning = true;
        markBookingsFetched();
        manager.bookingsToAdd = null;
        
        Vector params = new Vector();
        params.addElement(token);
        params.addElement(lcode);
        params.addElement(1);
        if(storeId.equals("fd2fecef-1ca1-4231-86a6-0ec445fbac83")) {
            params.addElement(0);
        } else {
            params.addElement(1);
        }
        
        Vector result = executeClient("fetch_new_bookings", params);
        if(result == null) {
            return;
        }
        
        if (!result.get(0).equals(0)) {
            manager.logText("0:" + result.get(0));
            manager.logText("1:" + result.get(1));
        } else {
            manager.bookingsToAdd = (Vector) result.get(1);
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
        if(!storeId.equals("fd2fecef-1ca1-4231-86a6-0ec445fbac83")) {
            return;
        }
        
        Vector bookings = manager.bookingsToAdd;
        if(bookings.size() > 0) {
            List<Integer> reservationCodes = new ArrayList();
            for(int bookcount = 0; bookcount < bookings.size(); bookcount++) {
                Hashtable reservation = (Hashtable) bookings.get(bookcount);
                Integer reservationCode = (Integer) reservation.get("reservation_code");
                reservationCodes.add(reservationCode);
            }
            
            Vector params = new Vector();
            params.addElement(token);
            params.addElement(lcode);
            params.addElement(reservationCodes);
            executeClient("mark_bookings", params);       
        }

    }

    void setStoreId(String storeId) {
        this.storeId = storeId;
    }
    
}
