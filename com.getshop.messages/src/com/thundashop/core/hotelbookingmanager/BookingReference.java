package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingReference extends DataCommon {
    public int bookingReference;
    public Date startDate;
    public Date endDate;
    public List<Integer> codes = new ArrayList();
    public List<String> roomIds = new ArrayList();
    public ContactData contact = new ContactData();
    public Double bookingFee = 0.0;
    public boolean updateArx = true;
}
