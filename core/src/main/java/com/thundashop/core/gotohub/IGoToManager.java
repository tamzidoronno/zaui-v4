package com.thundashop.core.gotohub;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;


@GetShopApi
public interface IGoToManager {
    public void changeToken(String newToken);
    public String testConnection() throws Exception;
}
