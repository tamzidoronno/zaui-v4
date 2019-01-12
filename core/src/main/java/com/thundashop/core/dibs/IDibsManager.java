
package com.thundashop.core.dibs;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * Dibs management.
 */
@GetShopApi
public interface IDibsManager {
    @Administrator
    public boolean checkForOrdersToCapture();
}
