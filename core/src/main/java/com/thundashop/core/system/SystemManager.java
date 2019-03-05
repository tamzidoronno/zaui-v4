/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.system;

import com.getshop.javaapi.GetShopApi;
import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.director.DailyUsage;
import com.thundashop.core.director.DirectorManager;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    public HashMap<String, DailyUsage> usages = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon o : data.data) {
            if (o instanceof GetShopSystem) {
                systems.put(o.id, (GetShopSystem)o);
            }
            if (o instanceof DailyUsage) {
                usages.put(o.id, (DailyUsage)o);
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
    
    @Override
    public void syncSystem(String systemId) throws Exception {
        GetShopSystem system = getSystem(systemId);
        Date yesterday =  getYesterDay();
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        
        if (system != null) {
            GetShopApi api = getApiForSystem(system);
            Date storeCreatedDate = api.getDirectorManager().getCreatedDate(DirectorManager.password);
            cal.setTime(storeCreatedDate);
            
            while (cal.getTime().before(yesterday)) {
                Date timeToGet = cal.getTime();
                DailyUsage savedUsageForDay = getSavedUsageForDay(system, timeToGet);
                
                if (savedUsageForDay == null) {
                    DailyUsage usage = api.getDirectorManager().getDailyUsage(DirectorManager.password, cal.getTime());
                    usage.systemId = system.id;
                    saveObject(usage);
                    usages.put(usage.id, usage);
                }
                
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            api.transport.close();
        }
    }

    private GetShopApi getApiForSystem(GetShopSystem system) {
        String sessionId = UUID.randomUUID().toString();
        GetShopApi api;
        try {
            api = new GetShopApi(25554, system.serverVpnIpAddress, sessionId, system.storeId);
            api.getStoreManager().initializeStore(system.webAddresses, sessionId);
            return api;
        } catch (Exception ex) {
            Logger.getLogger(SystemManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new RuntimeException("Could not initialize");
    }

    private DailyUsage getSavedUsageForDay(GetShopSystem system, Date timeToGet) {
        return usages.values()
                .stream()
                .filter(o -> o.systemId.equals(system.id))
                .filter(o -> o.isOnDay(timeToGet))
                .findAny()
                .orElse(null);
    }

    @Override
    public List<DailyUsage> getDailyUsage(String systemId) {
        return usages.values()
                .stream()
                .filter(o -> o.systemId.equals(systemId))
                .collect(Collectors.toList());
    }

    private Date getYesterDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }

    public void markUsageAsBilled(DailyUsage usage) {
        usage.markAsInvoiced();
        saveObject(usage);
        usages.put(usage.id, usage);
    }
}
