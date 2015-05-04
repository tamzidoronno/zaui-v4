
package com.getshop.arx;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.hotelbookingmanager.Room;
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
        
        List<Room> rooms = api.getHotelBookingManager().getAllRooms();
        for(Room roomtocheck : rooms) {

            if(roomtocheck.roomName.equals("102")) {
                roomtocheck.isClean = false;
            } else if(roomtocheck.roomName.equals("218")) {
                roomtocheck.isClean = false;
            } else if(roomtocheck.roomName.equals("307")) {
                roomtocheck.isClean = false;
            } else if(roomtocheck.roomName.equals("316")) {
                roomtocheck.isClean = false;
            } else if(roomtocheck.roomName.equals("302")) {
                roomtocheck.isClean = false;
            } else if(roomtocheck.roomName.equals("202")) {
                roomtocheck.isClean = false;
            } else {
                roomtocheck.isClean = true;
            }
            api.getHotelBookingManager().saveRoom(roomtocheck);
        }
    }
}
