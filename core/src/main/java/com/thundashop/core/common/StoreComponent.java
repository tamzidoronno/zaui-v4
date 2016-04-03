/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSessionObject;

/**
 *
 * @author ktonder
 */
public class StoreComponent extends GetShopSessionObject {
    public String storeId;

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    @Override
    public void clearSession() {
    }
}
