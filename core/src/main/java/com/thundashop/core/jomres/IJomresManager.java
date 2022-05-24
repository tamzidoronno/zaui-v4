package com.thundashop.core.jomres;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.jomres.dto.FetchBookingResponse;

import java.util.List;

/**
 * Jomres management system.<br>
 */

@GetShopApi
public interface IJomresManager {
    @Administrator
    public boolean testConnection() throws Exception;
    @Administrator
    public String updateAvailability() throws Exception;
    @Administrator
    public List<FetchBookingResponse> fetchBookings() throws Exception;
    @Administrator
    public boolean changeCredentials(String clientId, String clientSecret) throws Exception;
}
