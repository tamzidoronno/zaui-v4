package com.thundashop.core.ratemanager;

import com.thundashop.core.bcomratemanager.RateManagerConfig;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;

/**
 * Pms booking.com ratemanager.
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IBookingComRateManagerManager {
    public void pushInventoryList();
    public void pushAllBookings();
    public void saveRateManagerConfig(RateManagerConfig config);
    public RateManagerConfig getRateManagerConfig();
    public String updateRate(Date start, Date end, String roomId, Double rate);
}
