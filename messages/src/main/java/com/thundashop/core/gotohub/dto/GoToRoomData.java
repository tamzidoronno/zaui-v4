package com.thundashop.core.gotohub.dto;

import java.util.HashMap;
import java.util.List;

import lombok.Data;

@Data
public class GoToRoomData {
    private String goToRoomTypeCode;
    private String bookingEngineTypeId;
    private String name;
    private String description;
    private String about;
    private int numberOfUnits;
    private String roomCategory;
    private String sizeMeasurement;
    private String nonSmoking;
    private int maxGuest;
    private int numberOfAdults;
    private int numberOfChildren;
    private boolean status;
    private List<String> images;
    private List<RatePlan> ratePlans;
    private HashMap<Integer, Double> pricesByGuests;
    private long availableRooms;
}
