package com.thundashop.core.vippsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * Vipps management.
 */
@GetShopApi
public interface IVippsManager {
    public boolean startMobileRequest(String phoneNumber, String orderId, String ip) throws Exception;
    public void checkForOrdersToCapture();
    public boolean checkIfOrderHasBeenCompleted(Integer incOrderId);
    public boolean cancelOrder(String orderId, String ip) throws Exception ;
}
