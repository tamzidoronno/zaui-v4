package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class BookingReference extends DataCommon {
    public int bookingReference;
    public List<String> rooms = new ArrayList();
    public long startDate;
    public long endDate;
}
