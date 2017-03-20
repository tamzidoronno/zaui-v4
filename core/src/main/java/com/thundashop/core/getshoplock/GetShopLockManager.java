/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.bookingengine.BookingEngine;
import com.thundashop.core.bookingengine.data.BookingItem;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.GetShopHotelLockCodeResult;
import com.thundashop.core.getshop.data.GetShopLockCode;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
import com.thundashop.core.getshop.data.ZWaveDevice;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import com.thundashop.core.pmsmanager.PmsLockServer;
import com.thundashop.core.pmsmanager.PmsManager;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component 
@GetShopSession
public class GetShopLockManager extends GetShopSessionBeanNamed implements IGetShopLockManager {
    private HashMap<String, GetShopDevice> devices = new HashMap();
    private GetShopLockMasterCodes masterCodes = new GetShopLockMasterCodes();
    private boolean stopUpdatesOnLock = false;
    private boolean doUpdateLockList = false;
    
        
    @Autowired
    MessageManager messageManager;

    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Override
    public void refreshAllLocks(String source) {
            doUpdateLockList = true;
            getAllLocks(source);
            doUpdateLockList = false;
    }
    
    @Override
    public void refreshLock(String lockId) {
        GetShopDevice dev = devices.get(lockId);
        if(dev.zwaveid == 1) {
            refreshAllLocks(dev.serverSource);
        }
        for(GetShopLockCode code : dev.codes.values()) {
            code.resetOnLock();
        }
    }
    

    private void checkForMasterCodeUpdates(GetShopDevice dev) {
        for(int i = 1; i <= 5; i++) {
            GetShopLockCode code = dev.codes.get(i);
            String masterCode = masterCodes.codes.get(i);
            if(code == null) {
                System.out.println("Null code?");
            }
            if(!code.fetchCodeToAddToLock().equals(masterCode)) {
                code.setCode(masterCode);
            }
        }
    }

    @Override
    public void saveMastercodes(GetShopLockMasterCodes codes) {
        saveObject(codes);
        masterCodes = codes;
    }

    @Override
    public void deleteAllDevices(String password, String source) {
        if(!password.equals("fdsafbvvre4234235t")) {
            return;
        }
        List<String> toRemove = new ArrayList();
        for(GetShopDevice dev : devices.values()) {
            boolean remove = false;
            if(source.equals("default")) {
                if(dev.serverSource.equals("") || dev.serverSource.equals("default")) {
                    remove = true;
                }
            } else if(dev.serverSource.equals(source)) {
                remove = true;
            }
            if(remove) {
                toRemove.add(dev.id);
                deleteObject(dev);
            }
        }
        for(String id : toRemove) {
            devices.remove(id);
        }
    }

    @Override
    public List<String> getCodesInUse() {
        List<BookingItem> items = bookingEngine.getBookingItems();
        List<String> codes = new ArrayList();
        for(GetShopDevice dev : devices.values()) {
            if(connectedToBookingEngineItem(dev, items) == null) {
                continue;
            }
            for(GetShopLockCode code : dev.codes.values()) {
                if(code.inUse() || storeId.equals("9dda21a8-0a72-4a8c-b827-6ba0f2e6abc0")) {
                    codes.add(code.fetchCodeToAddToLock());
                }
            }
        }
        
        for(String masterCode : masterCodes.codes.values()) {
            codes.add(masterCode);
        }
        return codes;
    }

    @Override
    public void stopUpdatesOnLock() {
        stopUpdatesOnLock = !stopUpdatesOnLock;
    }

    @Override
    public boolean getUpdatesOnLock() {
        return stopUpdatesOnLock;
    }

    @Override
    public void removeAllUnusedLocks(String serverSource) throws Exception {
        List<GetShopDevice> toremove = new ArrayList();
        for(GetShopDevice dev : devices.values()) {
            if(dev.zwaveid == 1) {
                continue;
            }
            boolean inuse = false;
            for(GetShopLockCode code : dev.codes.values()) {
                if(code.canUse()) {
                    inuse = true;
                }
            }
            if(!inuse) {
                toremove.add(dev);
            }
        }
        
        for(GetShopDevice dev : toremove) {
            boolean toRemove = false;
            if(serverSource.equals("default")) {
                if(dev.serverSource.isEmpty() || dev.serverSource.equals("default")) {
                    toRemove = true;
                }
            } else if(serverSource.equals(dev.serverSource)) {
                toRemove = true;
            }
            if(toRemove) {
                devices.remove(dev.id);
                deleteObject(dev);
            }
        }
    }

    public void saveLock(GetShopDevice lock) {
        saveObject(lock);
        devices.put(lock.id, lock);
    }

    class GetshopLockCodeManagemnt extends Thread {

        private final GetShopDevice device;
        
        private String hostname = "";
        private String username = "";
        private String password = "";
        private List<BookingItem> items;
        private boolean stopUpdatesOnLock;

        public GetshopLockCodeManagemnt(GetShopDevice device, String username, String password, String hostname, List<BookingItem> items, boolean stopUpdatesOnLock) {
            this.device = device;
            this.hostname = hostname;
            this.password = password;
            this.username = username;
            this.items = items;
            this.stopUpdatesOnLock = stopUpdatesOnLock;
        }
        
        public void setCode(Integer offset, String code, boolean remove) {
            String doUpdate = "0";
            if(!remove) {
                doUpdate = "1";
            }
            
            try {
                String addr = "http://"+hostname+":8083/" + URLEncoder.encode("ZWaveAPI/Run/devices["+device.zwaveid+"].UserCode.Set("+offset+","+code+","+doUpdate+")", "UTF-8");
                String addr2 = "http://"+hostname+":8083/" + URLEncoder.encode("ZWave.zway/Run/devices["+device.zwaveid+"].UserCode.Get("+offset+")", "UTF-8");
                
                GetshopLockCom.httpLoginRequest(addr,username,password);
//                GetshopLockCom.httpLoginRequest(addr2,username,password);
            } catch (Exception ex) {
                Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }
        
        @Override
        public void run() {
            if(stopUpdatesOnLock) { device.beingUpdated = false; return; }
            if(hasConnectivity()) {
                if(device.oldBatteryStatus()) {
                    try {
                        checkBatteryStatus();
                        device.batteryLastUpdated = new Date();
                        device.needSaving = true;
                    }catch(Exception e) {
                        logPrintException(e);
                    }
                }
                int codesAdded = 0;
                List<Integer> offsets = new ArrayList(device.codes.keySet());
                offsets = Lists.reverse(offsets);
                
                for(Integer offset : offsets) {
                    if(stopUpdatesOnLock) { device.beingUpdated = false; return; }
                    GetShopLockCode code = device.codes.get(offset);
                    if(code.needUpdate()) {
                        if(code.needToBeRemoved()) {
                            code.refreshCode();
                        }
                        boolean added = false;
                        for(int i = 0; i < 10; i++) {
                            if(codesAdded >= 2) {
                                int minutesTried = getMinutesTriedSettingCodes(device);
                                if(minutesTried > 5) {
                                    Calendar future = Calendar.getInstance();
                                    future.add(Calendar.HOUR_OF_DAY, 2);
                                    device.lastTriedUpdate = future.getTime();
                                } else {
                                    device.lastTriedUpdate = new Date();
                                }
                                device.beingUpdated = false;
                                return; 
                            }

                            logPrint("\t Need to add code to offsett: " + offset + " (" + device.name + ")");
                            setCode(offset, code.fetchCodeToAddToLock(), true);
                            try {
                                GetShopHotelLockCodeResult result = getSetCodeResult(offset);
                                waitForEmptyQueue();
                                if(result != null && result.hasCode != null && result.hasCode.value != null && result.hasCode.value.equals(true)) {
                                    logPrint("\t\t Code alread set... should not be on offset: " + offset + " (" + device.name + ")");
                                } else {
                                    logPrint("\t\t We are ready to set code to " +  offset + " attempt: " + i + " (" + device.name + ")");
                                    for(int j = 0; j < 24; j++) {
                                        setCode(offset, code.fetchCodeToAddToLock(), false);
                                        waitForEmptyQueue();
                                        GetShopHotelLockCodeResult res = getSetCodeResult(offset);
                                        if(res != null && res.hasCode != null && res.hasCode.value != null && res.hasCode.value.equals(true)) {
                                            code.setAddedToLock();
                                            device.needSaving = true;
                                            logPrint("\t\t Code was successfully set on offset " + offset + "(" + j + " attempt)"+ " (" + device.name + ")");
                                            codesAdded++;
                                            added = true;
                                            break;
                                        } else {
                                            logPrint("\t\t Failed to set code to offset " + offset + " on attempt: " + j+ " (" + device.name + ")");
                                        }
                                    }
                                    if(!added) {
                                        logPrint("\t\t Where not able to set a code on " + offset + " moving on."+ " (" + device.name + ")");
                                        device.beingUpdated = false;
                                        return; 
                                    }
                                    break;
                                }
                                if(code.isAddedToLock()) {
                                    break;
                                }
                            } catch (Exception ex) {
                                Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        try { Thread.sleep(10000); }catch(Exception e) {}
                    }
                }
            }
            device.beingUpdated = false;
        }

        private GetShopHotelLockCodeResult getSetCodeResult(Integer offset) {
            try {
                String postfix = "ZWave.zway/Run/devices["+device.zwaveid+"].UserCode.data["+offset+"]";
                postfix = URLEncoder.encode(postfix, "UTF-8");
                String address = "http://"+hostname+":8083/" + postfix;
                String res = GetshopLockCom.httpLoginRequest(address,username,password);
                Gson gson = new Gson();
                GetShopHotelLockCodeResult result = gson.fromJson(res, GetShopHotelLockCodeResult.class);
                return result;
            } catch (Exception ex) {
                Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        private boolean hasConnectivity() {
             try {
                logPrint("Checking for connectivity for " + device.name + " (" + device.zwaveid + ")");
                String postfix = "ZWave.zway/Run/devices["+device.zwaveid+"].SendNoOperation()";
                postfix = URLEncoder.encode(postfix, "UTF-8");
                String address = "http://"+hostname+":8083/" + postfix;
                GetshopLockCom.httpLoginRequest(address,username,password);
               
                waitForEmptyQueue();
                 
                postfix = "ZWave.zway/Run/devices["+device.zwaveid+"]";
                postfix = URLEncoder.encode(postfix, "UTF-8");
                address = "http://"+hostname+":8083/" + postfix;
                
                Gson gson = new Gson();
                String res = GetshopLockCom.httpLoginRequest(address,username,password);
                ZWaveDevice result = gson.fromJson(res, ZWaveDevice.class);
                boolean isFailed = new Boolean(result.data.isFailed.value + "");
                if(isFailed) { 
                    logPrint("No connectivity found for :" + device.name + " (" + device.zwaveid + ")");
                    return false; 
                }
                return true;
            } catch (Exception ex) {
                Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
        }

        private void checkBatteryStatus() throws Exception {
            logPrint("Checking for battery for " + device.name + " (" + device.zwaveid + ")");
            String postfix = "ZWave.zway/Run/devices["+device.zwaveid+"].instances[0].commandClasses[128].Get()";
            postfix = URLEncoder.encode(postfix, "UTF-8");
            String address = "http://"+hostname+":8083/" + postfix;
            GetshopLockCom.httpLoginRequest(address,username,password);
            waitForEmptyQueue();
        }

        private void waitForEmptyQueue() {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 360);
            String postfix = "ZWave.zway/InspectQueue";
            while(true) {
                try {
                    String address = "http://"+hostname+":8083/" + postfix;
                    String res = GetshopLockCom.httpLoginRequest(address,username,password);
                    if(res.equals("[]")) {
                        break;
                    }
                    Thread.sleep(2000);
                }catch(Exception e) {
                    logPrintException(e);
                }
                if(cal.getTime().before(new Date())) {
                    logPrint("z-way: queue did not empty within timeout.");
                    return;
                }
            }
        }

        private int getMinutesTriedSettingCodes(GetShopDevice device) {
            Date now = new Date();
            long diff = now.getTime() - device.lastTriedUpdate.getTime();
            return (int)((diff / 1000) / 60);
        }
    }
    
    @Autowired
    PmsManager pmsManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon obj : data.data) {
            if(obj instanceof GetShopDevice) {
                GetShopDevice toAdd = (GetShopDevice) obj;
                devices.put(obj.id, toAdd);
                toAdd.beingUpdated = false;
                toAdd.lastTriedUpdate = null;
            }
            if(obj instanceof GetShopLockMasterCodes) {
                masterCodes = (GetShopLockMasterCodes) obj;
            }
        }
        createScheduler("pmsprocessor", "* * * * *", CheckAllOkGetShopLocks.class);
        createScheduler("pmsprocessor_lock", "30 23,04 * * *", UpdateLockList.class);
    }
    
    public String getUsername(String serverSource) {
        return pmsManager.getConfigurationSecure().getLockServer(serverSource).arxUsername;
    }
    
    public String getHostname(String serverSource) {
        return pmsManager.getConfigurationSecure().getLockServer(serverSource).arxHostname;
    }
    
    public String getPassword(String serverSource) {
        return pmsManager.getConfigurationSecure().getLockServer(serverSource).arxPassword;
    }
    
    private String httpLoginRequest(String address, String serverSource) throws Exception {
        String username = getUsername(serverSource);
        String password = getPassword(serverSource);
        return GetshopLockCom.httpLoginRequest(address, username, password);
    }

    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception {
        return "";
    }

    public String removeCode(String pmsBookingRoomId) throws Exception {
        return "";
    }

    @Override
    public String getCodeForLock(String lockId) {
        GetShopDevice dev = devices.get(lockId);
        if(dev == null) {
            logPrint("Lock device where not found");
            return "";
        }
        for(int i = 6; i < dev.maxNumberOfCodes; i++) {
            GetShopLockCode code = dev.codes.get(i);
            if(code.canUse()) {
                String codeToUse = code.fetchCode();
                saveObject(dev);
                return codeToUse;
            }
        }
        return "";
    }

    @Override
    public List<GetShopDevice> getAllLocks(String serverSource) {
        String hostname = getHostname(serverSource);
        if(hostname == null || hostname.isEmpty()) { return new ArrayList(); }
        if(doUpdateLockList) {
            try {
                String address = "http://" + hostname + ":8083/ZWave.zway/Run/devices";
                String res = httpLoginRequest(address, serverSource);

                HashMap<Integer, ZWaveDevice> result = new HashMap();
                Type type = new TypeToken<HashMap<Integer, ZWaveDevice>>(){}.getType();
                Gson gson = new Gson();
                result = gson.fromJson(res, type);
                List<GetShopDevice> currentDevices = new ArrayList();
                for(Integer offset : result.keySet()) {
                    ZWaveDevice device = result.get(offset);
                    GetShopDevice gsdevice = new GetShopDevice();
                    gsdevice.setDevice(device);
                    gsdevice.serverSource = serverSource;
                    addDeviceIfNotExists(gsdevice);
                    currentDevices.add(gsdevice);
                }

                List<GetShopDevice> toRemove = new ArrayList();
                for(GetShopDevice dev : devices.values()) {
                    boolean found = false;
                    for(GetShopDevice curlist :currentDevices) {
                        if(curlist.zwaveid.equals(dev.zwaveid)) {
                            found = true;
                        }
                    }
                    if(!found) {
                        toRemove.add(dev);
                    }
                }
                for(GetShopDevice torev : toRemove) {
//                    devices.remove(torev.id);
//                    deleteObject(torev);
                }

            } catch (Exception ex) {
                Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            }

            finalizeLocks();
        }
        
        ArrayList<GetShopDevice> res = new ArrayList(devices.values());
        Collections.sort(res, new Comparator<GetShopDevice>(){
            public int compare(GetShopDevice o1, GetShopDevice o2){
                if(o1.name == null || o2.name == null) {
                    return 0;
                }
                return o1.name.compareTo(o2.name);
            }
        });
        
        if(serverSource != null && !serverSource.isEmpty()) {
            List<GetShopDevice> toRemove = new ArrayList();
            for(GetShopDevice dev : res) {
                if(serverSource.equals("default")) {
                    if(!dev.serverSource.isEmpty() && !dev.serverSource.equals("default")) {
                        toRemove.add(dev);
                    }
                } else if(!serverSource.equals(dev.serverSource)) {
                    toRemove.add(dev);
                }
            }
            res.removeAll(toRemove);
        }
        
        return res;
    }

    @Override
    public void openLock(String lockId) {
        GetShopDevice dev = devices.get(lockId);
        String hostname = getHostname(dev.serverSource);
        if(hostname == null || hostname.isEmpty()) { return; }
        String postfix = "ZWave.zway/Run/devices["+dev.zwaveid+"].instances[0].commandClasses[98].Set(1)";
        try {
            postfix = URLEncoder.encode(postfix, "UTF-8");
        }catch(Exception e) {}
        String address = "http://"+getHostname(dev.serverSource)+":8083/" + postfix;
        try {
            httpLoginRequest(address, dev.serverSource);
        } catch (Exception ex) {
            Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }
    }

    @Override
    public boolean pingLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeCodeOnLock(String lockId, PmsBookingRooms room) {
        String code = room.code;
        GetShopDevice dev = devices.get(lockId);
        if(dev == null) {
            BookingItem item = bookingEngine.getBookingItem(room.bookingItemId);
            System.out.println("Lock device :" + lockId + " does not exists, room: ( " + item.bookingItemName);
        } else {
            dev.removeCode(code);
            saveObject(dev);
        }
    }

    @Override
    public void checkIfAllIsOk() {
        if(stopUpdatesOnLock) {
            logPrint("Lock updates stopped");
        }
        if(!frameworkConfig.productionMode) {
            return;
        }
        if(!pmsManager.getConfigurationSecure().isGetShopHotelLock()) {
            return;
        }
        finalizeLocks();
        for(GetShopDevice dev : devices.values()) {
            if(dev.needSaving) {
                dev.needSaving = false;
                saveObject(dev);
            }
            
            if(dev.isLock()) {
                checkForMasterCodeUpdates(dev);
            }
            
            if(dev.warnAboutCodeNotSet()) {
//                messageManager.sendErrorNotification("Failed to update getshop hotel locks, this have not been able to update locks for 6 hours. this might be critical.", null);
            }
        }
        
        for(GetShopDevice dev : devices.values()) {
            if(dev.beingUpdated) {
                return;
            }
        }
        
        List<BookingItem> items = bookingEngine.getBookingItems();
        GetShopDevice toSet = null;
        for(GetShopDevice dev : devices.values()) {
            if(connectedToBookingEngineItem(dev, bookingEngine.getBookingItems()) == null) {
                continue;
            }
            
            //Always prioritise the one that has least codes set.
            
            if(dev.isLock() && !dev.beingUpdated && dev.needUpdate()) {
                if(toSet == null || (dev.numberOfCodesNeedsUpdate() > toSet.numberOfCodesNeedsUpdate())) {
                    toSet = dev;
                }
            }
        }
        if(toSet != null) {
            toSet.beingUpdated = true;
            toSet.lastTriedUpdate = new Date();
            String user = getUsername(toSet.serverSource);
            String pass = getPassword(toSet.serverSource);
            String host = getHostname(toSet.serverSource);

            GetshopLockCodeManagemnt mgr = new GetshopLockCodeManagemnt(toSet, user, pass, host, items, stopUpdatesOnLock);
            mgr.start();
        }
        return;
        
    }
    
    private BookingItem connectedToBookingEngineItem(GetShopDevice device, List<BookingItem> items) {
        for(BookingItem item : items) {
            if(item.bookingItemAlias != null && item.bookingItemAlias.equals(device.id)) {
                return item;
            }
        }
        return null;
    }

    @Override
    public void deleteLock(String code, String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMasterCode(Integer slot, String code) {
        if(slot > 5) {
            logPrint("Only slot 0 to 5 is reserved for mastercodes");
            return;
        }
        for(GetShopDevice dev : devices.values()) {
            if(dev.isLock()) {
                GetShopLockCode toUpdate = dev.codes.get(slot);
                toUpdate.setCode(code);
            }
        }
    }

    @Override
    public GetShopLockMasterCodes getMasterCodes() {
        if(masterCodes.checkIfEmtpy()) {
            saveObject(masterCodes);
        }
        return masterCodes;
    }
    
    private void addDeviceIfNotExists(GetShopDevice gsdevice) {
        boolean found = false;
        String deviceid  = gsdevice.id;
        if(gsdevice.serverSource != null && !gsdevice.serverSource.isEmpty() && !gsdevice.serverSource.equals("default")) {
            deviceid = gsdevice.zwaveid + "_" + gsdevice.serverSource;
        }
        if(devices.containsKey(deviceid)) {
            GetShopDevice dev = devices.get(deviceid);
            if(dev.zwaveid.equals(gsdevice.zwaveid)) {
                dev.type = gsdevice.type;
                dev.name = gsdevice.name;
                dev.isAwake = gsdevice.isAwake;
                dev.isFailed = gsdevice.isFailed;
                dev.instances = gsdevice.instances;
                dev.batteryStatus = gsdevice.batteryStatus;
                dev.batteryLastUpdated = gsdevice.batteryLastUpdated;
                if(dev.name == null || dev.name.equals("null")) {
                    dev.name = "";
                }
                if(dev.type == null || dev.type.equals("null")) {
                    dev.type = "";
                }
                found = true;
                gsdevice = dev;
            }
        }
        saveObject(gsdevice);
        if(!found) {
            devices.put(deviceid, gsdevice);
        }
        
    }

    private void finalizeLocks() {
        for(GetShopDevice dev : devices.values()) {
            if(dev.isLock()) {
                boolean needSave = false;
                for(int i = 1; i <= dev.maxNumberOfCodes; i++) {
                    if(!dev.codes.containsKey(i)) {
                        GetShopLockCode code = new GetShopLockCode();
                        code.refreshCode();
                        dev.codes.put(i, code);
                        needSave = true;
                    }
                }
                if(needSave) {
                    saveObject(dev);
                }
            }
        }
    }
    
    @Override
    public void accessEvent(String id, String code, String domain) throws Exception {
        if(domain.equals("default")) {
            for(GetShopDevice dev : devices.values()) {
                Integer zwaveid = new Integer(id);
                if(dev.zwaveid.equals(zwaveid)) {
                    dev.accessLog.add(new Date());
                }
            }
        }
    }

}
