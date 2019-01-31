/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.system;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@GetShopSession
@Component
public class SystemManager extends ManagerBase implements ISystemManager {
    public HashMap<String, GetShopSystem> systems = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon o : data.data) {
            if (o instanceof GetShopSystem) {
                systems.put(o.id, (GetShopSystem)o);
            }
        }
    }

    @Override
    public GetShopSystem createSystem(String systemName, String companyId) {
        GetShopSystem system = new GetShopSystem();
        system.systemName = systemName;
        system.companyId = companyId;
        saveObject(system);
        systems.put(system.id, system);
        return system;
    }

    @Override
    public void deleteSystem(String systemId) {
        GetShopSystem system = systems.remove(systemId);
        if (system != null) {
            deleteObject(system);
        }
    }

    @Override
    public GetShopSystem getSystem(String systemId) {
        return systems.get(systemId);
    }

    @Override
    public void saveSystem(GetShopSystem system) {
        saveObject(system);
        systems.put(system.id, system);
    }

    @Override
    public List<GetShopSystem> getSystemsForCompany(String companyId) {
        return systems.values()
                .stream()
                .filter(o -> o.companyId != null && o.companyId.equals(companyId))
                .collect(Collectors.toList());
    }
    
}
