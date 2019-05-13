/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IGetShopAccountingManager {
    
    @Administrator
    public List<String> createNextOrderFile(Date endDate);
    
    @Administrator
    public List<SavedOrderFile> getOrderFiles();
    
    @Administrator
    public SavedOrderFile getOrderFile(String fileId);
    
    @Administrator
    public Date getPreviouseEndDate();
    
    @Administrator
    public List<String> getOrdersToIncludeForNextTransfer(Date endDate);
    
    @Administrator
    public void deleteFile(String fileId, String password);
    
    @Administrator
    public List<String> getLogEntries();
    
    @Administrator
    public HashMap<String, String> getListOfSystems();
    
    @Administrator
    public String getCurrentSystemInvoices();
    
    @Administrator
    public String getCurrentSystemOther();
    
    @Administrator
    public void setSystemTypeInvoice(String systemType);
    
    @Administrator
    public void setSystemTypeOther(String systemType);
    
    @Administrator
    public void setConfig(String systemType, String key, String value);
    
    @Administrator
    public HashMap<String, String> getConfigs(String systemType);
    
    @Administrator
    public HashMap<String, String> getConfigOptions(String systemType);
    
    @Administrator
    public boolean canOrderBeTransferredDirect(String orderId);
    
    @Administrator
    public List<String> transferDirect(String orderId);

    @Administrator
    public String createBankTransferFile();
    
    @Administrator
    public boolean isCurrentSelectedAccountingSystemPrimitive();
    
    @Administrator
    public boolean isCurrentSelectedSupportingDirectTransfer();
    
    @Administrator
    public void transferData(Date start, Date end);
    
    @Administrator
    public List<String> getTransferData(Date start, Date end, String doublePostingFileId);
}
