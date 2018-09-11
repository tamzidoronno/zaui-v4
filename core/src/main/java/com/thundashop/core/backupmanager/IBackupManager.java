/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.backupmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.ForceAsync;

/**
 *
 * @author ktonder
 */
@GetShopApi
public interface IBackupManager {
    
    @Administrator
    @ForceAsync
    public void createBackup();
}
