package com.thundashop.core.pmsmanager;

import com.thundashop.core.bookingengine.data.BookingItemType;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.List;

/**
 * Property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsManager {
    @Administrator
    public Integer addRoom(String name) throws Exception;
    public List<BookingItemType> getAllRooms() throws Exception;
}
