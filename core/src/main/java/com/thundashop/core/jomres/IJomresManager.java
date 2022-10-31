/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.jomres;

import java.util.List;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.jomres.dto.FetchBookingResponse;
import com.thundashop.core.jomres.dto.JomresProperty;

/**
 * Jomres management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IJomresManager {
    @ForceAsync
    @Administrator
    List<JomresLog> getLogEntries();

    @ForceAsync
    @Administrator
    public boolean testConnection();

    @ForceAsync
    @Administrator
    public boolean updateAvailability() throws Exception;

    @ForceAsync
    @Administrator
    public List<FetchBookingResponse> fetchBookings() throws Exception;

    @ForceAsync
    @Administrator
    public boolean changeConfiguration(JomresConfiguration newConfiguration);

    @ForceAsync
    @Administrator
    public List<JomresProperty> getJomresChannelProperties() throws Exception;

    @ForceAsync
    @Administrator
    public boolean saveMapping(List<JomresRoomData> mappingRoomData) throws Exception;

    @ForceAsync
    @Administrator
    public JomresConfiguration getConfigurationData() throws Exception;

    @ForceAsync
    @Administrator
    public List<JomresRoomData> getMappingData() throws Exception;
}
