
package com.getshop.arx;

import com.getshop.javaapi.GetShopApi;
import com.thundashop.core.hotelbookingmanager.BookingReference;
import com.thundashop.core.hotelbookingmanager.Room;
import com.thundashop.core.hotelbookingmanager.RoomInformation;
import com.thundashop.core.hotelbookingmanager.UsersBookingData;
import com.thundashop.core.hotelbookingmanager.Visitors;
import com.thundashop.core.ordermanager.data.Order;
import com.thundashop.core.ordermanager.data.Payment;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
        api.getUserManager().logOn(apiUsername, apiPassword);
    }

    void doJob() throws Exception {
        connectToBackend();
//        updateOrder();
//        updateRoomCleaning();
//        updateReference();
//        generateUniqueCodes();
//        updateJonTerjeOrder();
//        getArx();
//        setRoomExtendStay();
//        removeSomeBookings();
//        getoneUserBooking();
        moveDateOnReference();
//        updateInvoice();
        
    }

    private void updateOrder() throws Exception {
        Order order = api.getOrderManager().getOrder("74474c94-52e4-4372-9bc1-947dd79df20e");
        
        User user = api.getUserManager().getUserById(order.userId);
        
        order.payment = new Payment();
        order.payment.paymentType = "ns_70ace3f0_3981_11e3_aa6e_0800200c9a66\\InvoicePayment";
        order.status = Order.Status.PAYMENT_COMPLETED;
        order.cart.address = user.address;
        order.cart.address.fullName = user.fullName;
        

        api.getOrderManager().saveOrder(order);
        
    }

    private void updateRoomCleaning() throws Exception {
        List<Room> rooms = api.getHotelBookingManager().getAllRooms();
        
       for(Room room : rooms) {
            switch(room.id) {
                case "c27bb5bc-c99c-438e-9986-353fc8c4d7b5":
                    room.isClean = true;
                    room.cleaningDates.add(new Date());
                    api.getHotelBookingManager().saveRoom(room);
                default:
            }
        }
    }

    private void updateReference() throws Exception {
        UsersBookingData bdata = api.getHotelBookingManager().getUserBookingData("4ca2cd84-99db-4a91-bad1-35d4422d6478");
        for(BookingReference reference : bdata.references) {
            reference.roomsReserved.get(0).roomState = RoomInformation.RoomInfoState.externalDoorGranted;
        }
        api.getHotelBookingManager().updateUserBookingData(bdata);
    }

    private void generateUniqueCodes() throws Exception {
        List<UsersBookingData> bdatas = api.getHotelBookingManager().getAllUsersBookingData();
        HashMap<Integer, Integer> codes = new HashMap();
        for(UsersBookingData bdata : bdatas) {
            for(BookingReference breference : bdata.references) {
                for(RoomInformation roomInfo : breference.roomsReserved) {
                    int counter = 1;
                    if(codes.containsKey(roomInfo.code)) {
                        counter = codes.get(roomInfo.code);
                        counter++;
                    }
                    codes.put(roomInfo.code, counter);
                }
            }
        }
        
        List<Integer> newCodes = new ArrayList();
        newCodes.add(5842);
        newCodes.add(5843);
        newCodes.add(5844);
        newCodes.add(5845);
        newCodes.add(5846);
        newCodes.add(5847);
        newCodes.add(5848);
        newCodes.add(5849);
        newCodes.add(5810);
        newCodes.add(5811);
        newCodes.add(5812);
        newCodes.add(5813);
        newCodes.add(5814);
        
        int counter = 0;
        for(UsersBookingData bdata : bdatas) {
            boolean needSaving = false;
            for(BookingReference breference : bdata.references) {
                for(RoomInformation roomInfo : breference.roomsReserved) {
                    if(codes.get(roomInfo.code) > 1) {
                        System.out.println("Need diff: " + roomInfo.code + "  : " + roomInfo.roomId + " : " + breference.startDate + " -> " + breference.endDate); 
                        roomInfo.code = newCodes.get(counter);
                        needSaving = true;
                        counter++;
                    }
                }
            }
            if(needSaving) {
//                api.getHotelBookingManager().updateUserBookingData(bdata);
            }
        }
    }

    private void updateJonTerjeOrder() throws Exception {
        UsersBookingData bdata = api.getHotelBookingManager().getUserBookingData("4f1d9d38-29a4-445e-b54a-4f6485818960");
        for(BookingReference reference : bdata.references) {
            for(RoomInformation roominfo : reference.roomsReserved) {
                if(roominfo.visitors.isEmpty()) {
                    roominfo.visitors.add(new Visitors());
                }
            }
        }
        api.getHotelBookingManager().updateUserBookingData(bdata);
    }

    private void getArx() throws Exception {
        api.getHotelBookingManager().getAllReservationsArx();
    }

    private void updateCleanRoom() {
        
    }

    private void setRoomExtendStay() throws Exception {
        UsersBookingData bdata = api.getHotelBookingManager().getUserBookingData("ab30675f-fb14-47e2-a525-a8f7bba36a4c");
        for(BookingReference breference : bdata.references) {
            for(RoomInformation rinfor : breference.roomsReserved) {
                rinfor.roomState = RoomInformation.RoomInfoState.initial;
            }
        }
        api.getHotelBookingManager().updateUserBookingData(bdata);
    }

    private void removeSomeBookings() throws Exception {
        api.getHotelBookingManager().deleteReference(8715);
        api.getHotelBookingManager().deleteReference(8714);
        
    }

    private void getoneUserBooking() throws Exception {
        api.getHotelBookingManager().getReservationByReferenceId(8477);
    }

    private void moveDateOnReference() throws Exception {
        String bdataid = "f10d33cd-c424-4706-90b4-8e1e73e2d547";
        Integer referenceId = 10018;
        
        UsersBookingData bdata = api.getHotelBookingManager().getUserBookingData(bdataid);
        for(BookingReference breference : bdata.references) {
            if(breference.bookingReference == referenceId) {
                Date startDate = breference.startDate;
                Date endDate = breference.endDate;
                
                Calendar calstart = Calendar.getInstance();
                calstart.setTime(startDate);
                
                Calendar calend = Calendar.getInstance();
                calend.setTime(endDate);
                
                //Set new dates.-
                calstart.set(Calendar.DAY_OF_MONTH, 19);
                calend.set(Calendar.DAY_OF_MONTH, 20);
                
                breference.startDate = calstart.getTime();
                breference.endDate = calend.getTime();

                for(RoomInformation rinfo : breference.roomsReserved) {
                    rinfo.roomState = RoomInformation.RoomInfoState.accessGranted;
                }
                
            }
        }
        bdata.orderIds.remove("cf74c63c-8f8e-4a67-b1b8-926009613cbb");
        api.getHotelBookingManager().updateUserBookingData(bdata);
    }

    private void updateInvoice() throws Exception {
        Order order = api.getOrderManager().getOrder("cf74c63c-8f8e-4a67-b1b8-926009613cbb");
        order.status = Order.Status.CANCELED;
        order.testOrder = false;
        api.getOrderManager().saveOrder(order);
    }
}