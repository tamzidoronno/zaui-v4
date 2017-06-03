/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mekonomen;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.mekonomen.MekonomenUser;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class MekonomenManager extends ManagerBase implements IMekonomenManager {
    public MekonomenDatabase mekonomenDatabase = null;
   
    @Override
    public void dataFromDatabase(DataRetreived data) {
        if (storeId.equals("6524eb45-fa17-4e8c-95a5-7387d602a69b")) {
            mekonomenDatabase = MekonomenDatabase.getDatabase();
        }
    }
    
    @Override
    public List<MekonomenUser> searchForUser(String name) {
        return mekonomenDatabase.searchForUser(name);
    }
    
}
