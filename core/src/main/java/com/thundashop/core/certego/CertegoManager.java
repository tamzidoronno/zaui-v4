/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.certego;

import com.getshop.scope.GetShopSession;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thundashop.core.certego.data.CertegoOrder;
import com.thundashop.core.certego.data.CertegoOrders;
import com.thundashop.core.certego.data.CertegoSystem;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.Group;
import com.thundashop.core.usermanager.data.User;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
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
    Map<String, CertegoOrders> orders = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        data.data.stream()
            .filter(checkObject -> checkObject.isOf(CertegoSystem.class))
            .forEach(o -> systems.put(o.id, (CertegoSystem) o));
        
        data.data.stream()
            .filter(checkObject -> checkObject.isOf(CertegoOrders.class))
            .forEach(o -> orders.put(o.id, (CertegoOrders) o));
        
        
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

    @Override
    public List<CertegoOrder> getOrders() {
        Group group = getGroupForCurrentUser();
        if (group == null) {
            throw new ErrorException(26);
        }
        
        CertegoOrders orderCollection = getCertegoOrders(group);
        Collections.sort(orderCollection.orders);
        Collections.reverse(orderCollection.orders);
        return orderCollection.orders;
    }

    @Override
    public void saveOrder(CertegoOrder order) {
        if (getSession() == null) {
            return;
        }
        
        Group group = getGroupForCurrentUser();
        if (group == null) {
            return;
        }
        
        validateOrder(order);
        
        CertegoOrders orderCollection = getCertegoOrders(group);
        orderCollection.orders.add(order);
        saveObject(orderCollection);
    }

    private void validateOrder(CertegoOrder order) {
        JsonElement jelement = new JsonParser().parse(order.data);
        JsonObject jobject = jelement.getAsJsonObject();
        JsonElement dataElement = jobject.get("data");
        JsonObject dataObject = dataElement.getAsJsonObject();
        
        JsonObject page2 = dataObject.get("page2").getAsJsonObject();
        
        JsonArray keysSetupElement = page2.getAsJsonArray("keys_setup");
        checkOrderLines(keysSetupElement);
        
        JsonArray cylinderSetupElement = page2.getAsJsonArray("cylinder_setup");
        checkOrderLines(cylinderSetupElement);
    }

    private void checkOrderLines(JsonArray keysSetupElement) {
        if (keysSetupElement != null) {
            Iterator<JsonElement> iterator = keysSetupElement.iterator();
            while(iterator.hasNext()) {
                JsonObject keySetup = iterator.next().getAsJsonObject();
                String systemNumber = keySetup.get("systemNumber").getAsString();
                System.out.println(systemNumber);
                if (!hasAccessToSystemNumber(systemNumber)) {
                    throw new ErrorException((26));
                }
            }
        }
    }
    
    private boolean hasAccessToSystemNumber(String systemNumber) {
        Group group = getGroupForCurrentUser();
        if (group == null) {
            return false;
        }
        
        List<CertegoSystem> currentSystems = getSystemsForGroup(group);
        for (CertegoSystem system : currentSystems) {
            if (system.number != null && system.number.toLowerCase().equals(systemNumber.toLowerCase())) {
                return true;
            }
        }
        
        return false;
    }

    private Group getGroupForCurrentUser() {
        User user = getSession().currentUser;
        if (user == null) {
            return null;
        }
        
        if (user.groups == null || user.groups.size() != 1) {
            return null;
        }
        
        if (user.groups != null && user.groups.size() > 1) {
            System.out.println("Warning, user assigned to multiple groups. Can not save order");
            return  null;
        }
        
        return userManager.getGroup(user.groups.get(0));    
    }

    private CertegoOrders getCertegoOrders(Group group) {
        CertegoOrders orderCollection = orders.get(group.id);
        if (orderCollection == null) {
            orderCollection = new CertegoOrders();
            orderCollection.id = group.id;
            orders.put(group.id, orderCollection);
        }
        return orderCollection; 
   }   
}