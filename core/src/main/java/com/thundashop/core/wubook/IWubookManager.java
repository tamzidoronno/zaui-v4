/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.wubook;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.List;


/**
 * Wubook management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IWubookManager {
    @Administrator
    public boolean testConnection() throws Exception;
    @Administrator
    public List<String> fetchAllBookings() throws Exception;
    @Administrator
    public List<String> fetchNewBookings() throws Exception;
    @Administrator
    public boolean fetchBooking(String rcode) throws Exception;
    @Administrator
    public String updateAvailability() throws Exception;
    @Administrator
    public void markNoShow(String rcode) throws Exception;
    @Administrator
    public String updatePrices() throws Exception;
    @Administrator
    public void markCCInvalid(String rcode) throws Exception;
    @Administrator
    public List<String> insertAllRooms() throws Exception;
}
