/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.external;

import com.thundashop.core.pmsmanager.PmsRoomSimple;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ktonder
 */

class GuestInformation {
    public String name;
    public String phone;
    public String email;
}

public class ExternalRoom {
    public String id;
    public String roomName;
    public String roomTypeName;
    public Date checkin;
    public Date checkout;
    
    public List<GuestInformation> guests = new ArrayList();

    public void makeRoom(PmsRoomSimple pmsRoom, String roomName, String roomTypeName) {
        id = pmsRoom.pmsRoomId;
        this.roomName = roomName;
        this.roomTypeName = roomTypeName;
        checkin = new Date(pmsRoom.start);
        checkout = new Date(pmsRoom.end);
        
        pmsRoom.guest.stream()
                .forEach(guest -> {
                    GuestInformation info = new GuestInformation();
                    info.name = guest.name;
                    info.email = guest.email;
                    info.phone = guest.phone;
                });
    }
}
