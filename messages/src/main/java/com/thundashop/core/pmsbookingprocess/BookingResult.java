package com.thundashop.core.pmsbookingprocess;

import java.util.ArrayList;
import java.util.List;

public class BookingResult {
    String bookingid;
    int continuetopayment;
    int success;
    String orderid;
    boolean goToCompleted = false;
    double amount = 0;
    public List<BookingResultRoom> roomList = new ArrayList();
}
