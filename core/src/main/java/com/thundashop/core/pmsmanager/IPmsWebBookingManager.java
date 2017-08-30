package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.wubook.webbookingmanager.PmsWebRoom;
import java.util.Date;
import java.util.List;

/**
 * Web booking for property management system.<br>
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsWebBookingManager {
    public List<PmsWebRoom> getAllRooms(Date start, Date end);
}
