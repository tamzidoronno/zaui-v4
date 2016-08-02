
package com.thundashop.core.arx;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.getshoplock.GetShopLockManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class DoorManager extends GetShopSessionBeanNamed implements IDoorManager {
    @Autowired
    UserManager usermanager;
    
    @Autowired
    StoreManager storeManager;
    
    @Autowired
    PmsManager pmsManager;
    
    @Autowired
    GetShopLockManager getShopLockManager;
    
    @Autowired
    BookingEngine bookingEngine;
    
    private List<Door> doorList = new ArrayList();
    
    @Override
    public List<Door> getAllDoors() throws Exception {
        List<Door> cachedDoors = getCachedDoors();
        if(!cachedDoors.isEmpty()) {
            return cachedDoors;
        }
        
        
        IDoorManager mgr = getDoorManager();
        
        List<Door> res = mgr.getAllDoors();
        doorList.clear();
        doorList.addAll(res);
        return res;
    }
    
    @Override
    public List<AccessCategory> getAllAccessCategories() throws Exception {
        return getDoorManager().getAllAccessCategories();
    }
    
    @Override
    public List<Person> getAllPersons() throws Exception {
        return getDoorManager().getAllPersons();
    }

    @Override
    public void doorAction(String externalId, String state) throws Exception {
        getDoorManager().doorAction(externalId, state);
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        return getDoorManager().getLogForAllDoor(start, end);
    }

    @Override
    public HashMap<String, List<AccessLog>> generateDoorLogForAllDoorsFromResult(String result) throws Exception {
        return getDoorManager().generateDoorLogForAllDoorsFromResult(result);
    }
    
    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        return getDoorManager().getLogForDoor(externalId, start, end);
    }

    @Override
    public Person updatePerson(Person person) throws Exception {
        return getDoorManager().updatePerson(person);
    }

    @Override
    public Person getPerson(String id) throws Exception {
        return getDoorManager().getPerson(id);
    }

    @Override
    public Person addCard(String personId, Card card) throws Exception {
         return getDoorManager().addCard(personId, card);
    }

    private List<Door> getCachedDoors() {
        return doorList;
    }

    @Override
    public void clearDoorCache() throws Exception {
        List<Door> cachedDoors = getAllDoors();
        for(Door door : cachedDoors) {
            deleteObject(door);
        }
        doorList.removeAll(cachedDoors);
    }
    
    @Override
    public String pmsDoorAction(String code, String type) throws Exception {
        return getDoorManager().pmsDoorAction(code, type);
    }
    
    private IDoorManager getDoorManager() {
        if(pmsManager.getConfigurationSecure().isGetShopHotelLock()) {
            GetShopLockDoorManager mgr = new GetShopLockDoorManager();
            mgr.setManager(bookingEngine, getShopLockManager);
            return mgr;
        } else {
            ArxDoorManager mgr = new ArxDoorManager();
            mgr.setManager(storeManager, pmsManager, this, bookingEngine);
            return mgr;
        }
    }

    List<Door> getDoorList() {
        return doorList;
    }

}
