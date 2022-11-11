package com.thundashop.core.gotohub;

import java.util.HashMap;
import java.util.List;

import com.thundashop.core.gotohub.dto.RatePlan;

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
