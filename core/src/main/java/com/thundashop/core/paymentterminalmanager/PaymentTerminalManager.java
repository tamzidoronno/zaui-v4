/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.paymentterminalmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class PaymentTerminalManager extends ManagerBase implements IPaymentTerminalManager {
    List<PaymentTerminalSettings> settings = new ArrayList();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataFromDatabase : data.data) {
            if(dataFromDatabase instanceof PaymentTerminalSettings) {
                settings.add((PaymentTerminalSettings) dataFromDatabase);
            }
        }
    }
    
    @Override
    public void saveSettings(PaymentTerminalSettings tosave) {
        PaymentTerminalSettings result = getSetings(tosave.offset);
        settings.clear();
        if(result != null) {
            settings.remove(result);
            result.offset = tosave.offset;
            result.ip = tosave.ip;
            result.verifoneTerminalId = tosave.verifoneTerminalId;
        } else {
            result = tosave;
        }
        
        saveObject(result);
        settings.add(result);
    }

    @Override
    public PaymentTerminalSettings getSetings(Integer offset) {
        for(PaymentTerminalSettings setting : settings) {
            if(offset.equals(setting.offset)) {
                return setting;
            }
        }
        return null;
    }
    
}
