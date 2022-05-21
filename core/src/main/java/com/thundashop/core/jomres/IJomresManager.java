package com.thundashop.core.jomres;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.jomres.dto.FetchBookingResponse;

import java.util.List;

@GetShopApi
public interface IJomresManager {
    public boolean testConnection() throws Exception;

    public String updateAvailability() throws Exception;

    public List<FetchBookingResponse> fetchBookings() throws Exception;

    public boolean changeCredentials(String clientId, String clientSecret) throws Exception;
}
