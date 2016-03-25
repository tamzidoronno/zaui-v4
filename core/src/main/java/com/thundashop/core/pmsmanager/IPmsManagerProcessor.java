package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;


/**
 * Property management system processor.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsManagerProcessor {
    public void doProcessing();
}
