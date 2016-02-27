package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.HashMap;
import java.util.List;

/**
 * For handling connection to xledger.<br>
 */
@GetShopApi
public interface IAccountingManager {
    @Administrator
    public List<String> createOrderFile() throws Exception;
    @Administrator
    public List<String> createUserFile() throws Exception;
    @Administrator
    public HashMap<String, String> getAllFiles();
    @Administrator
    public List<String> getFile(String id);
}
