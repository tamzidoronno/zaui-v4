/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
