
package com.thundashop.core.arx;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.apacmanager.ApacManager;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.GetShopLockCode;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
import com.thundashop.core.getshoplock.GetShopLockManager;
import com.thundashop.core.getshoplocksystem.GetShopLockSystemManager;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.storemanager.StoreManager;
import com.thundashop.core.usermanager.UserManager;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    
    @Autowired
    private GetShopLockSystemManager apacManager;
    
    private List<Door> doorList = new ArrayList();
    private Date lastClosed;
    
    @Override
    public List<Door> getAllDoors() throws Exception {
        if (apacManager.isActivated()) {
            List<Door> doorsToRet = new ArrayList();
            apacManager.getLockServers()
                    .stream()
                    .forEach(server -> {
                        server.getLocks().stream()
                                .forEach(lock -> {
                                    Door door = new Door();
                                    door.name = lock.name;
                                    door.serverSource = server.getId();
                                    door.externalId = lock.id;
                                    doorsToRet.add(door);
                                });
                    });
            return doorsToRet;
        }
        
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
        if (apacManager.isActivated()) {
            if (state.equals("forceOpenOn")) {
                apacManager.openLock(externalId);
            } else if(state.equals("pulseOpen")) {
                apacManager.pulseOpenLock(externalId);
            } else {
                 apacManager.closeLock(externalId);
            }
        } else {
            getDoorManager().doorAction(externalId, state);
        }
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
    
    public void closeAllForTheDay() throws Exception {
        if(isClosedToday()) {
            return;
        }
        getDoorManager().closeAllForTheDay();
        lastClosed = new Date();
    }

    private boolean isClosedToday() {
        if(lastClosed == null) {
            return false;
        }
        
        Calendar now = Calendar.getInstance();
        Calendar closed = Calendar.getInstance();
        closed.setTime(lastClosed);
        
        if(now.get(Calendar.DAY_OF_YEAR) == closed.get(Calendar.DAY_OF_YEAR)) {
            return true;
        }
        
        return false;
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
    
    public GetShopLockCode getNextAvailableCode(String deviceId) throws Exception {
        
        GetShopDevice device = getShopLockManager.getDevice(deviceId);
        if (device != null) {
            return device.getNextAvailableCode();
        }

        return null;
    }
    
    public void claimUsage(String deviceId, GetShopLockCode code, String source) {
        getShopLockManager.claimUsage(deviceId, code, source);
    }

    public Door getDoorByDeviceId(String deviceId) throws Exception {
        return getAllDoors().stream()
                .filter(door -> door.externalId != null && door.externalId.equals(deviceId))
                .findFirst()
                .orElse(null);
        
    }

    public void removeCode(String deviceId, Integer slot) {
        getShopLockManager.removeCodeOnLockBySlotId(deviceId, slot);
    }

    @Override
    public GetShopLockMasterCodes getMasterCodes() {
        return getDoorManager().getMasterCodes();
    }

    @Override
    public void saveMasterCodes(GetShopLockMasterCodes masterCodes) {
        getDoorManager().saveMasterCodes(masterCodes);
    }

}
