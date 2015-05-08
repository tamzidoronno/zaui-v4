
package com.getshop.arx;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.hotelbookingmanager.Room;
import com.thundashop.core.hotelbookingmanager.UsersBookingData;
import com.thundashop.core.usermanager.data.User;
import java.util.List;
import java.util.UUID;

public class UpdateRooms {
//    private String apiAddress = "localhost";
//    private Integer backendport = 25554;
    
    private String apiAddress = "www.getshop.com";
    private Integer backendport = 3224;
    
    
    private String username = "wh,";
    private String password = "fsdu_nerdDDe234vcozs";

    private String apiUsername = "kai@getshop.com";
    private String apiPassword = "g4kkg4kk";
    private String website = "wh.no";
    private boolean notifiedGetshop = false;
    
        private String sessid;
    private GetShopApi api;
    
    public static void main(String[] args) throws Exception {
        UpdateRooms updator = new UpdateRooms();
        updator.doJob();
        
    }
    
    private void connectToBackend() throws Exception {
sessid = UUID.randomUUID().toString();
        System.out.println("Connecting to : " + apiAddress + " port : " + backendport);
        api = new GetShopApi(backendport, apiAddress, sessid, website);
        try {
            User result = api.getUserManager().logOn(apiUsername, apiPassword);
            if (!api.getUserManager().isLoggedIn()) {
                System.out.println("Failed to logon to backend");
                System.exit(0);
            }
            System.out.println("Connected to backend");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void doJob() throws Exception {
        connectToBackend();
        
        UsersBookingData booking = api.getHotelBookingManager().getUserBookingData("353669a6-df33-4ec0-a9b9-2c032b26d642");
        booking.orderIds.clear();
        booking.orderIds.add("1cf4afd2-b89e-4500-bb75-32648d87d4e5");
        booking.orderIds.add("6222251a-1892-4f25-ab3a-8731ffa2c4c0");
        api.getHotelBookingManager().updateUserBookingData(booking);
    }
}
