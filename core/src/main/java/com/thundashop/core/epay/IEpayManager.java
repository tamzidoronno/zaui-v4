package com.thundashop.core.epay;

import com.thundashop.core.common.GetShopApi;

/**
 * Bambora payment management.
 */
@GetShopApi
public interface IEpayManager {
    public boolean checkForOrdersToCapture();
}
