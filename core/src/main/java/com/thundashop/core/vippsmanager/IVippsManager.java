package com.thundashop.core.vippsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * Vipps management.
 */
@GetShopApi
public interface IVippsManager {
    public String startMobileRequest(String phoneNumber, String orderId) throws Exception;
    @Administrator
    public void checkForOrdersToCapture();
    public boolean checkIfOrderHasBeenCompleted(Integer incOrderId);
    public boolean cancelOrder(String orderId) throws Exception ;
}
