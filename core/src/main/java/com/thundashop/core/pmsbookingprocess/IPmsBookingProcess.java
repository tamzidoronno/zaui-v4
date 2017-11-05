/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsbookingprocess;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;


/**
 * Booking process for property management system.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsBookingProcess {
    public StartBookingResult startBooking(StartBooking object);
}
