package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class UsersBookingData extends DataCommon {
    
    public List<BookingReference> references = new ArrayList();
    AdditionalBookingInformation additonalInformation;
    public boolean payedFor = false;
    public boolean partnerReference = false;
    public List<String> orderIds = new ArrayList();
    public boolean active = true;
    public String sessionId = "";
}
