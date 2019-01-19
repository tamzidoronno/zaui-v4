package com.thundashop.core.pmsbookingprocess;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class StartBookingResult {
    public LinkedList<BookingProcessRooms> rooms = new LinkedList();
    public Integer totalRooms = 0;
    public Integer roomsSelected = 0;
    public Double totalAmount = 0.0;
    public Integer numberOfDays = 0;
    public boolean supportPayLaterButton = false;
    public List<String> supportedPaymentMethods = new ArrayList();
    String prefilledContactUser = "";

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
}
