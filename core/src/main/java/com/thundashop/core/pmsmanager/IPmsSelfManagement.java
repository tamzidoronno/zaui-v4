/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.ordermanager.data.Order;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsSelfManagement {
    public PmsBooking getBookingById(String id);
    public Order getOrderById(String id, String orderId);
    public void saveAddonSetup(String id, List<PmsSelfManageAddon> addons);
    public List<PmsBookingAddonItem> getAddonsWithDiscountForBooking(String id, String pmsBookingRoomId);
}
