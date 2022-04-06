/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.channelmanagerexperiment;

import com.thundashop.core.channelmanagerexperiment.wubook.WubookRoomRateMap;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.wubook.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Wubook management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IChannelManager {
    @Administrator
    public boolean testConnection() throws Exception;
    @Administrator
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookLog> getLogEntries();
    
    @Administrator
    public void doUpdateMinStay();
    
    @Administrator
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookBooking> fetchAllBookings(Integer daysback) throws Exception;
    @Administrator
    public List<Integer> fetchBookingCodes(Integer daysback) throws Exception;
    @Administrator
    @ForceAsync
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookBooking> addNewBookingsPastDays(Integer daysback) throws Exception;

    @Administrator
    public void fetchNewBookings();
    
    @Administrator
    public com.thundashop.core.channelmanagerexperiment.wubook.WubookBooking fetchBooking(String rcode) throws Exception;
    @Administrator
    public void activateWubookCallback() throws Exception;
    @Administrator
    public void addBooking(String rcode) throws Exception;
    @Administrator
    public boolean updateAvailabilityFromButton() throws Exception;

    @Administrator
    public String updateShortAvailability();
    
    @Administrator
    public String markNoShow(String rcode) throws Exception;
    @Administrator
    @ForceAsync
    public String updatePrices() throws Exception;
    @Administrator
    public String updateMinStay() throws Exception;
    @Administrator
    public String markCCInvalid(String rcode) throws Exception;
    @Administrator
    public List<String> insertAllRooms() throws Exception;
    @Administrator
    public void deleteAllRooms() throws Exception;
    @Administrator
    public String deleteBooking(String rcode) throws Exception;
    
    @Administrator
    @ForceAsync
    public void doubleCheckDeletedBookings() throws Exception;
    
    @Administrator
    public void checkForNoShowsAndMark() throws Exception;
    
    @Administrator
    public HashMap<String, com.thundashop.core.channelmanagerexperiment.wubook.WubookRoomData> getWubookRoomData();
    
    @Administrator
    public void saveWubookRoomData(HashMap<String, com.thundashop.core.channelmanagerexperiment.wubook.WubookRoomData> res);
    
    @Administrator
    public void addRestriction(com.thundashop.core.channelmanagerexperiment.wubook.WubookAvailabilityRestrictions restriction);
    
    @Administrator
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookAvailabilityRestrictions> getAllRestriction();
    
    @Administrator
    public void deleteRestriction(String id);
    
    @Administrator
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookBooking> fetchBookings(Integer daysBack, boolean registrations) throws Exception;
    
    @Administrator
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookOta> getOtas() throws Exception;
    
    @Administrator
    public boolean newOta(String type) throws Exception;
    
    @Administrator
    public List<com.thundashop.core.channelmanagerexperiment.wubook.WubookRoomRateMap> getRoomRates(Integer channelId, Integer channelType) throws Exception;
    
    @Administrator
    public void setRoomRates(Integer channelId, List<WubookRoomRateMap> rates, Integer channelType);
    
    @Administrator
    public String getCallbackUrl();
    
    public void fetchBookingFromCallback(String rcode) throws Exception;
    
    @Administrator
    public String updatePricesBetweenDates(Date now, Date end) throws Exception;
    
}
