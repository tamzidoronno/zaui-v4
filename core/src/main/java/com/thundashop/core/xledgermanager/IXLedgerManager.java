package com.thundashop.core.xledgermanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 * For handling connection to xledger.<br>
 */
@GetShopApi
public interface IXLedgerManager {
    @Administrator
    public List<String> createOrderFile();
}
