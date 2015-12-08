
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.DataCommon;
import java.util.ArrayList;
import java.util.List;

public class PmsBooking extends DataCommon {
    public List<PmsBookingRooms> rooms = new ArrayList();
    public List<PmsBookingDateRange> dates = new ArrayList();
    public PmsBookingAddons addons = new PmsBookingAddons();
    public String sessionId;
}
