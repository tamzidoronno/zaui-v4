package com.thundashop.core.accountingmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.pmsmanager.PmsOrderStatistics;
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
    public List<SavedOrderFile> getAllFiles(boolean showAllFiles);
    @Administrator
    public List<SavedOrderFile> getAllFilesNotTransferredToAccounting();
    @Administrator
    public void markAsTransferredToAccounting(String id);
    @Administrator
    public List<String> getFile(String id) throws Exception;
    @Administrator
    public SavedOrderFile getFileById(String id) throws Exception;
    @Administrator
    public SavedOrderFile getFileByIdResend(String id) throws Exception;
    
    @Administrator
    public SavedOrderFile transferSingleOrders(String configId, List<Integer> incOrderIds) throws Exception;
    
    @Administrator
    public List<String> createCombinedOrderFile(boolean newUsersOnly) throws Exception;

    @Administrator
    public void forceTransferFiles();
    
    @Administrator
    public List<String> getLatestLogEntries() throws Exception;
    
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
    
    @Administrator
    public PmsOrderStatistics getStats(String configId);
    
    @Administrator
    public void deleteFile(String fileId) throws Exception;
    
    @Administrator
    public void resetAllAccounting();
    
    @Administrator
    public void transferAllToNewSystem();
    
}
