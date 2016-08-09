package com.thundashop.core.arx;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshoplock.GetShopLockManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author boggi
 */
public class GetShopLockDoorManager extends ManagerBase implements IDoorManager {

    private GetShopLockManager getShopLockManager;
    private BookingEngine bookingEngine;

    public void setManager(BookingEngine bookingEngine, GetShopLockManager getShopLockManager) {
        this.bookingEngine = bookingEngine;
        this.getShopLockManager = getShopLockManager;
    }
    
    @Override
    public List<Door> getAllDoors() throws Exception {
        List<Door> doors = new ArrayList();
        List<GetShopDevice> locks = getShopLockManager.getAllLocks();
        List<BookingItem> items = bookingEngine.getBookingItems();
        for(GetShopDevice lock : locks) {
            if(lock.isLock()) {
                String name = "unkown";
                for(BookingItem item : items) {
                    if(item.bookingItemAlias.equals(lock.id)) {
                        name = item.bookingItemName;
                    }
                }

                Door door = new Door();
                door.externalId = lock.id;
                door.name = name;
                doors.add(door);
            }
        }
        
        return doors;
    }

    @Override
    public List<Person> getAllPersons() throws Exception {
        return new ArrayList();
    }

    @Override
    public List<AccessCategory> getAllAccessCategories() throws Exception {
        return new ArrayList();
    }

    @Override
    public void doorAction(String externalId, String state) throws Exception {
        logPrint("External id: " + externalId + ", state: " + state);
        if(state.equals("pulseOpen") || state.equals("forceOpenOn") || state.equals("forceOpenOff")) {
            getShopLockManager.openLock(externalId);
        }
    }

    @Override
    public String pmsDoorAction(String code, String type) throws Exception {
        return "";
    }

    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        return new ArrayList();
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        return new HashMap();
    }

    @Override
    public Person updatePerson(Person person) throws Exception {
        return new Person();
    }

    @Override
    public Person getPerson(String id) throws Exception {
        return new Person();
    }

    @Override
    public Person addCard(String personId, Card card) throws Exception {
        return new Person();
    }

    @Override
    public void clearDoorCache() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public HashMap<String, List<AccessLog>> generateDoorLogForAllDoorsFromResult(String resultFromArx) throws Exception {
        return new HashMap();
    }

    
}
