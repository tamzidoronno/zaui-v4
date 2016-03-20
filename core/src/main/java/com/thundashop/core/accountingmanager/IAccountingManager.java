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
    public List<String> createUserFile(boolean newOnly) throws Exception;
    @Administrator
    public HashMap<String, String> getAllFiles();
    @Administrator
    public List<SavedOrderFile> getAllFilesNotTransferredToAccounting();
    @Administrator
    public void markAsTransferredToAccounting(String id);
    @Administrator
    public List<String> getFile(String id);
    @Administrator
    public List<String> createCombinedOrderFile(boolean newUsersOnly) throws Exception;
    
    @Administrator
    public void setAccountingManagerConfig(AccountingManagerConfig config);
    
    @Administrator
    public void transferFilesToAccounting();
    
    @Administrator
    public AccountingManagerConfig getAccountingManagerConfig();
}
