
package com.getshop.arx;

import com.getshop.javaapi.GetShopApi;
import java.util.UUID;

public class RunGetArx {
        
    private String apiAddress = "localhost";
    private Integer backendport = 25554;
    
    private String username = "wh,";
    private String password = "fsdu_nerdDDe234vcozs";

    private String apiUsername = "kai@getshop.com";
    private String apiPassword = "g4kkg4kk";
    private String website = "wh.no";
    private boolean notifiedGetshop = false;
    
        private String sessid;
    private GetShopApi api;
        
    public static void main(String[] args) throws Exception {
        RunGetArx updator = new RunGetArx();
        updator.doJob();
        
    }
    private void connectToBackend() throws Exception {
        sessid = UUID.randomUUID().toString();
        System.out.println("Connecting to : " + apiAddress + " port : " + backendport);
        api = new GetShopApi(backendport, apiAddress, sessid, website);
        api.getUserManager().logOn(apiUsername, apiPassword);
    }
    
    private void doJob() throws Exception {
        connectToBackend();
        while(true) {
            System.out.println("Fetching");
            api.getHotelBookingManager().getAllReservationsArx();
            try { Thread.sleep(5000);}catch(Exception e) {}
        }
    }
}
