/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.certego;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.certego.data.CertegoSystem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class CertegoManager extends ManagerBase implements ICertegoManager {

    @Autowired
    private UserManager userManager;
    
    Map<String, CertegoSystem> systems = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream()
            .filter(checkObject -> checkObject.isOf(CertegoSystem.class))
            .forEach(o -> systems.put(o.id, (CertegoSystem) o));
    }

    
    @Override
    public CertegoSystem saveSystem(CertegoSystem system) {
        saveObject(system);
        systems.put(system.id, system);
        return system;
    }

    @Override
    public List<CertegoSystem> getSystems() {
        ArrayList<CertegoSystem> list = new ArrayList(systems.values());
        list.stream().forEach(o -> finalize(o));
        return list;
    }

    @Override
    public void deleteSystem(String systemId) {
        CertegoSystem system = systems.remove(systemId);
        
        if (system != null) {
            deleteObject(system);
        }
    }

    private void finalize(CertegoSystem o) {
        Group group = userManager.getGroup(o.groupId);
        o.group = group;
    }

    @Override
    public List<CertegoSystem> getSystemsForGroup(Group group) {
        List<CertegoSystem> retList= new ArrayList();
        for (CertegoSystem system : systems.values()) {
            if (system.groupId != null && system.groupId.equals(group.id)) {
                retList.add(system);
            }
        }
        
        retList.stream().forEach(o -> finalize(o));
        
        return retList;
    }

    @Override
    public List<CertegoSystem> search(String searchWord) {
        if (searchWord == null) {
            return new ArrayList();
        }
        
        return systems.values().stream()
            .filter( o -> 
                   (o.name != null && o.name.toLowerCase().contains(searchWord.toLowerCase()))
                || (o.phoneNumber != null && o.phoneNumber.toLowerCase().contains(searchWord.toLowerCase()))
                || (o.number != null && o.number.toLowerCase().contains(searchWord.toLowerCase()))
                || (o.email != null && o.email.toLowerCase().contains(searchWord.toLowerCase()))
            )
            .collect(Collectors.toList());
    }
}
