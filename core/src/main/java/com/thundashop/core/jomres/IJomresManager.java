package com.thundashop.core.jomres;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.jomres.dto.FetchBookingResponse;

import java.util.List;

/**
 * Jomres management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IJomresManager {
    @Administrator
    public boolean testConnection();
    @Administrator
    public boolean updateAvailability() throws Exception;
    @Administrator
    public List<FetchBookingResponse> fetchBookings() throws Exception;
    @Administrator
    public boolean changeCredentials(String clientId, String clientSecret) throws Exception;
}
