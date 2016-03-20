/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.accountingmanager;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author boggi
 */
public class AutoTransferFiles extends GetShopSchedulerBase {

    public AutoTransferFiles(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    @Override
    public void execute() throws Exception {
        AccountingManagerConfig config = getApi().getAccountingManager().getAccountingManagerConfig();
        if(config.username != null && !config.username.isEmpty()) {
            this.getApi().getAccountingManager().transferFilesToAccounting();
        }
    }
    
}
