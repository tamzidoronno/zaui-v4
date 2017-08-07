package com.thundashop.core.arx;

import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
import com.thundashop.core.getshoplock.GetShopLockManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
        List<GetShopDevice> locks = getShopLockManager.getAllLocks(null);
        List<BookingItem> items = bookingEngine.getBookingItems();
        for(GetShopDevice lock : locks) {
            if(lock.isLock()) {
                String name = lock.name;
                for(BookingItem item : items) {
                    if(item.bookingItemAlias.equals(lock.id)) {
                        name = item.bookingItemName;
                    }
                }

                Door door = new Door();
                door.externalId = lock.id;
                door.name = name;
                door.serverSource = lock.serverSource;
                doors.add(door);
            }
        }
        
        Collections.sort(doors, new Comparator<Door>(){
            public int compare(Door o1, Door o2){
                if(o1.name == null || o2.name == null) {
                    return -1;
                }
                return o1.name.compareTo(o2.name);
            }
       });
        
        return doors;
    }

    @Override
    public List<Person> getAllPersons() throws Exception {
        return new ArrayList();
    }

    public List<AccessCategory> getAllAccessCategories() throws Exception {
        return new ArrayList();
    }

    @Override
    public void doorAction(String externalId, String state) throws Exception {
        logPrint("External id: " + externalId + ", state: " + state);
        getShopLockManager.openLock(externalId, state);
    }

    @Override
    public String pmsDoorAction(String code, String type) throws Exception {
        return "";
    }

    @Override
    public List<AccessLog> getLogForDoor(String externalId, long start, long end) throws Exception {
        HashMap<String, List<AccessLog>> logs = getLogForAllDoor(start, end);
        return logs.get(externalId);
    }

    @Override
    public HashMap<String, List<AccessLog>> getLogForAllDoor(long start, long end) throws Exception {
        Date startDate = new Date();
        startDate.setTime(start);
        
       Date endDate = new Date();
       endDate.setTime(end);
       HashMap<String, List<AccessLog>> toReturn = new HashMap();
       for(GetShopDevice dev : getShopLockManager.getAllLocksCached(null)) {
           List<AccessLog> result = new ArrayList();
           for(Date accessEntry : dev.accessLog) {
               if(accessEntry.after(startDate) && accessEntry.before(endDate)) {
                   AccessLog entry = new AccessLog();
                   entry.door = dev.zwaveid + "";
                   entry.card = "******";
                   entry.personName = "door opened";
                   entry.timestamp = accessEntry.getTime();
                   result.add(entry);
               }
           }
           toReturn.put(dev.id, result);
        }
       return toReturn;
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

    @Override
    public void closeAllForTheDay() throws Exception {
    }

    @Override
    public GetShopLockMasterCodes getMasterCodes() {
        return getShopLockManager.getMasterCodes();
    }

    @Override
    public void saveMasterCodes(GetShopLockMasterCodes masterCodes) {
        for (Integer slot : masterCodes.codes.keySet()) {
            String code = masterCodes.codes.get(slot);
            if (code != null && !code.isEmpty()) {
                getShopLockManager.setMasterCode(slot, code);
            }
        }
    }
}
