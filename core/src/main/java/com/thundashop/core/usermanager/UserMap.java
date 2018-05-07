/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.usermanager;

import com.thundashop.core.common.SmartDataMap;
import com.thundashop.core.databasemanager.Database;
import com.thundashop.core.usermanager.data.User;
import com.thundashop.core.usermanager.data.UserLight;

/**
 *
 * @author ktonder
 */
public class UserMap extends SmartDataMap<String, User, UserLight> {

    public UserMap(Database database, String storeId, Class manager) {
        super(database, storeId, manager, User.class);
    }

    @Override
    public int getVersionNumber() {
        return 1;
    }
    
}
