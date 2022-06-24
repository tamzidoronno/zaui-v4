package com.thundashop.core.availability.dto;

import com.thundashop.core.pmsbookingprocess.BookingProcessRooms;
import com.thundashop.core.pmsbookingprocess.RoomsSelected;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StartBookingResult {
    public LinkedList<BookingProcessRooms> rooms = new LinkedList();
    public Integer totalRooms = 0;
    public Integer roomsSelected = 0;
    public Double totalAmount = 0.0;
    public Integer numberOfDays = 0;
    public boolean supportPayLaterButton = false;
    public boolean startYesterday = false;
    public List<String> supportedPaymentMethods = new ArrayList();
    public String prefilledContactUser = "";
    public String bookingId = "";
    public String errorMessage = "";
    public boolean hasAvailableRooms = false;

    public List<RoomsSelected> getRoomsSelected() {
        List<RoomsSelected> res = new ArrayList();
        for(BookingProcessRooms room : rooms) {
            RoomsSelected toAdd = new RoomsSelected();
            toAdd.id = room.id;
            toAdd.roomsSelectedByGuests = room.roomsSelectedByGuests;
            res.add(toAdd);
        }
        return res;
    }

    public boolean hasAvailableRooms() {
        for(BookingProcessRooms room : rooms) {
            if(room.availableRooms > 0) {
                return true;
            }
        }
        return false;
    }
}
