package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.HashMap;
import java.util.List;

/**
 * Property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsManager {
    public List<Room> getAllRoomTypes(long start, long end);
    public void setBooking(PmsBooking addons) throws Exception;
    public PmsBooking getCurrentBooking();
    public PmsBooking startBooking();
    public HashMap<String,Integer> validateCurrentBooking();
}
