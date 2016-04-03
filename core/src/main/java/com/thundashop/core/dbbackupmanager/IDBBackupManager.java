/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.dbbackupmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import java.util.List;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IDBBackupManager {
    @Administrator
    public List<DBChange> getChanges(String className);
    
    @Administrator
    public List<DBChange> getChangesById(String className, String id);
    
    @Administrator
    public String getDiff(String className, String id1, String id2);
}
