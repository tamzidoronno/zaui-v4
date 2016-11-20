package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * For handling connection to accounting systems.<br>
 */
@GetShopApi
public interface IAccountingManager {
    @Administrator
    public List<String> createOrderFile() throws Exception;
    @Administrator
    public List<String> createUserFile(boolean newOnly) throws Exception;
    @Administrator
    public List<String> createCreditorFile(boolean newOnly) throws Exception;
    @Administrator
    public List<SavedOrderFile> getAllFiles();
    @Administrator
    public List<SavedOrderFile> getAllFilesNotTransferredToAccounting();
    @Administrator
    public void markAsTransferredToAccounting(String id);
    @Administrator
    public List<String> getFile(String id) throws Exception;
    @Administrator
    public List<String> createCombinedOrderFile(boolean newUsersOnly) throws Exception;
    
    @Administrator
    public void setAccountingManagerConfig(AccountingManagerConfig config);
    
    @Administrator
    public List<AccountingTransferConfig> getAllConfigs();
    
    @Administrator
    public void saveConfig(AccountingTransferConfig config);
    
    @Administrator
    public AccountingTransferConfig getAccountingConfig(String configId);
    
    @Administrator
    public void removeTransferConfig(String id);
    
    @Administrator
    public void transferFilesToAccounting();
    
    @Administrator
    public void transferFiles(String type) throws Exception;
    
    @Administrator
    public List<String> getNewFile(String type) throws Exception;
    
    @Administrator
    public SavedOrderFile downloadOrderFileNewType(String configId, Date start, Date end) throws Exception;
    
    @Administrator
    public AccountingManagerConfig getAccountingManagerConfig();
}
