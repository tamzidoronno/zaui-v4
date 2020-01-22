/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.wubook;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.apache.xmlrpc.XmlRpcException;


/**
 * Wubook management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IWubookManager {
    @Administrator
    public boolean testConnection() throws Exception;
    @Administrator
    public HashMap<Long, String> getLogEntries();
    
    @Administrator
    public void doUpdateMinStay();
    
    @Administrator
    public List<WubookBooking> fetchAllBookings(Integer daysback) throws Exception;
    @Administrator
    public List<Integer> fetchBookingCodes(Integer daysback) throws Exception;
    @Administrator
    @ForceAsync
    public List<WubookBooking> addNewBookingsPastDays(Integer daysback) throws Exception;
    @Administrator
    public void fetchNewBookings() throws Exception;
    @Administrator
    public WubookBooking fetchBooking(String rcode) throws Exception;
    @Administrator
    public void activateWubookCallback() throws Exception;
    @Administrator
    public void addBooking(String rcode) throws Exception;
    @Administrator
    public boolean updateAvailabilityFromButton() throws Exception;
    @Administrator
    public String updateShortAvailability() throws Exception;
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
    public HashMap<String, WubookRoomData> getWubookRoomData();
    
    @Administrator
    public void saveWubookRoomData(HashMap<String,WubookRoomData> res);
    
    @Administrator
    public void addRestriction(WubookAvailabilityRestrictions restriction);
    
    @Administrator
    public List<WubookAvailabilityRestrictions> getAllRestriction();
    
    @Administrator
    public void deleteRestriction(String id);
    
    @Administrator
    public List<WubookBooking> fetchBookings(Integer daysBack, boolean registrations) throws Exception;
    
    @Administrator
    public List<WubookOta> getOtas() throws Exception;
    
    @Administrator
    public boolean newOta(String type) throws Exception;
    
    @Administrator
    public List<WubookRoomRateMap> getRoomRates(Integer channelId, Integer channelType) throws Exception;
    
    @Administrator
    public void setRoomRates(Integer channelId, List<WubookRoomRateMap> rates, Integer channelType);
    
    @Administrator
    public String getCallbackUrl();
    
    public void fetchBookingFromCallback(String rcode) throws Exception;
    
    @Administrator
    public String updatePricesBetweenDates(Date now, Date end) throws XmlRpcException, Exception;
    
}
