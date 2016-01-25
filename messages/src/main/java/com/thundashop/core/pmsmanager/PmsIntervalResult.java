package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingTimeLine;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PmsIntervalResult implements Serializable {
    public LinkedHashMap<String, List<BookingTimeLine>> typeTimeLines = new LinkedHashMap();
    public LinkedHashMap<String, HashMap<Long, Integer>> itemTimeLines = new LinkedHashMap();
}
