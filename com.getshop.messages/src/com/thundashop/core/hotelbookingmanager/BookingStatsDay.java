package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class BookingStatsDay extends DataCommon {
    public LinkedHashMap<String, Integer> income = new LinkedHashMap();
    public LinkedHashMap<String, Integer> count = new LinkedHashMap();
    public LinkedList<String> takenRooms = new LinkedList();
}
