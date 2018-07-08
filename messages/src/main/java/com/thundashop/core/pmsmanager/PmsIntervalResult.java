package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingTimeLine;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class PmsIntervalResult implements Serializable {
    public LinkedHashMap<String, List<BookingTimeLine>> typeTimeLines = new LinkedHashMap();
    public LinkedHashMap<String, LinkedHashMap<Long, IntervalResultEntry>> itemTimeLines = new LinkedHashMap();
    public ArrayList<LinkedHashMap<Long, IntervalResultEntry>> overFlowLines = new ArrayList();
    public LinkedHashMap<String, LinkedHashMap<Long, Integer>> availableCounter = new LinkedHashMap(); 
}
