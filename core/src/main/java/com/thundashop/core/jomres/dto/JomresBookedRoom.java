package com.thundashop.core.jomres.dto;

import com.google.gson.internal.LinkedTreeMap;

public class JomresBookedRoom {
    public String roomName;
    public String tariffTitle;
    public String roomNumber;
    public String floor;
    public int maxPeople;
    public String roomType;
    public int roomTypeID;

    public JomresBookedRoom(LinkedTreeMap<String, ?> roomDetail) {
        this.roomName = roomDetail.get("RINFO_NAME").toString();
        this.tariffTitle = roomDetail.get("RINFO_TARIFF").toString();
        this.roomNumber = roomDetail.get("RINFO_NUMBER").toString();
        this.floor = roomDetail.get("RINFO_ROOM_FLOOR").toString();
        this.roomType = roomDetail.get("TYPE").toString();

        this.maxPeople = Integer.parseInt(roomDetail.get("RINFO_MAX_PEOPLE").toString());

    }
}
