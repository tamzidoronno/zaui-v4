package com.thundashop.core.bambora;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.ordermanager.OrderManager;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Bambora payment management.
 */
@GetShopApi
public interface IBamboraManager {
    public String getCheckoutUrl(String orderId);
    public void checkForOrdersToCapture();
}
