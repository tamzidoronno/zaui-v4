/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.jomres;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.jomres.dto.FetchBookingResponse;
import com.thundashop.core.jomres.dto.JomresProperty;

import java.util.List;

/**
 * Jomres management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IJomresManager {
    @Administrator
    List<JomresLog> getLogEntries();
    @Administrator
    public boolean testConnection();
    @Administrator
    @ForceAsync
    public boolean updateAvailability() throws Exception;
    @Administrator
    public List<FetchBookingResponse> fetchBookings() throws Exception;

    public boolean changeConfiguration(JomresConfiguration newConfiguration);

    @Administrator
    public List<JomresProperty> getJomresChannelProperties() throws Exception;
    @Administrator
    public boolean saveMapping(List<JomresRoomData> mappingRoomData) throws Exception;
    @Administrator
    public JomresConfiguration getConfigurationData() throws Exception;
    @Administrator
    public List<JomresRoomData> getMappingData() throws Exception;

}
