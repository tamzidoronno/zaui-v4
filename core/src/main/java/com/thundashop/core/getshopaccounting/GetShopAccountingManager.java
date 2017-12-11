/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshopaccounting;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.accountingmanager.SavedOrderFile;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GetShopAccountingManager extends ManagerBase implements IGetShopAccountingManager {
    
    private final AccountingSystemBase.SystemType activatedSystemType = AccountingSystemBase.SystemType.GBAT10;
    
    @Autowired
    private final List<AccountingSystemBase> accountingSystems = new ArrayList();

    @Override
    public List<String> createNextOrderFile(Date endDate) {
        return getActivatedAccountingSystem().createNextOrderFile(endDate);
    }
    
    @Override
    public List<SavedOrderFile> getOrderFiles() {
        return getActivatedAccountingSystem().getOrderFiles();
    }
    
    @Override
    public SavedOrderFile getOrderFile(String fileId) {
        return getActivatedAccountingSystem().getOrderFile(fileId);
    }
    
    @Override
    public Date getPreviouseEndDate() {
        return getActivatedAccountingSystem().getPreviouseEndDate();
    }
    
    private AccountingSystemBase getActivatedAccountingSystem() {
        AccountingSystemBase system = accountingSystems
                .stream()
                .filter(o -> o.getSystemType().equals(activatedSystemType))
                .findAny()
                .orElse(null);
        
        if (system == null) {
            throw new ErrorException(1047);
        }
        
        return system;
    }

    public void transferOldFiles(Collection<SavedOrderFile> savedFiles) {
        savedFiles.stream()
                .forEach(file -> {
                    getActivatedAccountingSystem().transferOldOrderFile(file);
                });
        
        getActivatedAccountingSystem().createMegaFile();
        
    }

    @Override
    public List<String> getOrdersToIncludeForNextTransfer(Date endDate) {
        return getActivatedAccountingSystem().getOrdersToIncludeForNextTransfer(endDate);
    }

    @Override
    public void deleteFile(String fileId, String password) {
        if (password.equals("asdjfa094u51jn12on51o2n35123nasdfasdf")) {
            getActivatedAccountingSystem().deleteFile(fileId);
        }
    }

    @Override
    public List<String> getLogEntries() {
        return getActivatedAccountingSystem().getLogEntries();
    }
    
}
