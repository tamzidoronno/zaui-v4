
package com.thundashop.core.ocr;

import com.thundashop.core.common.GetShopApi;


/**
 * The ordermanager handles all orders created by this store.<br>
 * An order is usually created after the order has been added to the cart.<br>
 */
@GetShopApi
public interface IOcrManager {
    public void scanOcrFiles();
}
