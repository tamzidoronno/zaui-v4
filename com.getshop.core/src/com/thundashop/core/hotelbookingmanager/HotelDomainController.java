/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class HotelDomainController {
    
    private List<RoomType> roomTypes = new ArrayList();
    private List<Room> rooms = new ArrayList();
    private final Domain domain;
    private HotelBookingManager hotelBookingManager;


    HotelDomainController(Domain domain, HotelBookingManager hotelBookingManager) {
        this.domain = domain;
        this.hotelBookingManager = hotelBookingManager;
    }

    public void dataFromDatabase(List<DataCommon> data) {
        for (DataCommon dataCommon : data) {
            if (!isForMe(dataCommon))
                continue;
            
            if (dataCommon instanceof RoomType)
                roomTypes.add((RoomType)dataCommon);
        }
    }

    public RoomType createRoom(String name, double price, int size) {
        RoomType roomType = new RoomType();
        roomTypes.add(roomType);
        saveObject(roomType);
        return roomType; 
    }

    private void saveObject(DomainControlledObject domainControlledObject) {
        domainControlledObject.domainId = domain.id;
        hotelBookingManager.saveObject(domainControlledObject);
    }

    private boolean isForMe(DataCommon dataCommon) {
        if (dataCommon instanceof DomainControlledObject) {
            DomainControlledObject obj = (DomainControlledObject)dataCommon;
            return obj.domainId.equals(domain.id);
        }
        
        return false;
    }
}
