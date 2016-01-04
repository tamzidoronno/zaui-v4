package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingTimeLine;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class PmsIntervalResult implements Serializable {
    public HashMap<String, List<BookingTimeLine>> typeTimeLines = new HashMap();
    public HashMap<String, HashMap<Long, Integer>> itemTimeLines = new HashMap();
}
