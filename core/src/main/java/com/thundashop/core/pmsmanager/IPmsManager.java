package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;

/**
 * Property management system.<br>
 */

@GetShopApi
public interface IPmsManager {
    @Administrator
    public void addRoom(String name) throws Exception;
}
