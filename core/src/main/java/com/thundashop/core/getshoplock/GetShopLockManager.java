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
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.common.GetShopLogHandler;
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
import com.thundashop.core.pmsmanager.TimeRepeater;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component 
@GetShopSession
public class GetShopLockManager extends GetShopSessionBeanNamed implements IGetShopLockManager {
    
    private HashMap<String, GetShopDeviceLog> deviceLogs = new HashMap();
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
        saveObject(dev);
    }
    

    private void checkForMasterCodeUpdates(GetShopDevice dev) {
        for(int i = 1; i <= 5; i++) {
            GetShopLockCode code = dev.codes.get(i);
            String masterCode = masterCodes.codes.get(i);
            if(code == null) {
                logPrint("Null code?");
            }
            if(!code.fetchCodeToAddToLock().equals(masterCode)) {
                code.setCode(masterCode);
            }
        }
    }

    @Override
    public void initialize() throws SecurityException {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
        createScheduler("fetchLockLock", "22 0,2,4,6,8,10,12,14,16,18,20,22 * * *", GetShopLogFetcherStarter.class);
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
        List<BookingItem> items = bookingEngine.getBookingItems();
        for(GetShopDevice dev : devices.values()) {
            if(dev.zwaveid == 1) {
                continue;
            }
            boolean inuse = false;
            for(GetShopLockCode code : dev.codes.values()) {
                if(dev.isSubLock()) {
                    inuse = true;
                }
                BookingItem connected = connectedToBookingEngineItem(dev, items);
                if(connected != null) {
                    inuse = true;
                }
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

    @Override
    public void saveLock(GetShopDevice lock) {
        if(lock.isLock()) {
            finalizeLock(lock);
        }
        saveObject(lock);
        devices.put(lock.id, lock);
    }
    
    private PmsLockServer getLockServerForDevice(GetShopDevice dev) {
        for(String serverName : pmsManager.getConfigurationSecure().lockServerConfigs.keySet()) {
            if(serverName.equals(dev.serverSource)) {
                return pmsManager.getConfigurationSecure().lockServerConfigs.get(serverName);
            }
        }
        return null;
    }

    private void openGetShopLockBox(String lockId, String action) {
       GetShopDevice dev = devices.get(lockId);
       PmsLockServer lockServer = getLockServerForDevice(dev);

            String hostname = getHostname(dev.serverSource);
            if(hostname == null || hostname.isEmpty()) { return; }
            String postfix = "?username="+lockServer.arxUsername+"&password="+lockServer.arxPassword+"&deviceid="+dev.zwaveid+"&forceopen=";
            if(action.equalsIgnoreCase("forceOpenOn")) {
                postfix += "on";
            } else {
                postfix += "off";
            }
            String address = "http://"+lockServer.arxHostname+":18080/" + postfix;
            try {
                httpLoginRequest(address, dev.serverSource);
            } catch (Exception ex) {
                logPrintException(ex);
            }

       
    }

    @Override
    public void changeZWaveId(String lockId, Integer newId) {
        GetShopDevice toChange = devices.get(lockId);
        toChange.zwaveid = newId;
        for(GetShopLockCode code : toChange.codes.values()) {
            code.addedToLock = null;
        }
        saveObject(toChange);
    }

    @Override
    public GetShopDevice getDevice(String deviceId) {
        return devices.get(deviceId);
    }

    private void checkDoorsWithOpeningHours() {
        for(GetShopDevice dev : devices.values()) {
            if(dev.openingHoursData != null) {
                TimeRepeater data = new TimeRepeater();
                if(data.isInTimeRange(dev.openingHoursData, new Date())) {
                    if(dev.lockState.equals("unkown") || dev.lockState.equals("closed")) {
                        logPrint("Opening device:" + dev.id);
                        autoOpenLock(dev);
                        dev.lockState = "open";
                        saveLock(dev);
                    }
                } else {
                    if(dev.lockState.equals("unkown") || dev.lockState.equals("open")) {
                        logPrint("Closing device:" + dev.id);
                        dev.lockState = "closed";
                        autoCloseLock(dev);
                        saveLock(dev);
                    }                    
                }
            }
        }
    }

    private void autoOpenLock(GetShopDevice dev) {
        PmsLockServer server = getLockServerForDevice(dev);
        if(server.isGetShopLockBox()) {
            openLock(dev.id, "forceOpenOn");
        }
    }

    private void autoCloseLock(GetShopDevice dev) {
        PmsLockServer server = getLockServerForDevice(dev);
        if(server.isGetShopLockBox()) {
            openLock(dev.id, "forceOpenOff");
        }
        if(server.isGetShopHotelLock()) {
            for(GetShopLockCode code : dev.codes.values()) {
                if(code.inUse()) {
                    code.forceRemove();
                }
            }
        }
    }

    public void checkAndUpdateSubLocks() {
        for(GetShopDevice deviceToUpdate : devices.values()) {
            if(deviceToUpdate.masterLocks != null && deviceToUpdate.masterLocks.isEmpty()) {
                continue;
            }
            
            List<String> codesAdded = new ArrayList();
            for(String masterLockId : deviceToUpdate.masterLocks) {
                GetShopDevice masterLock = getDevice(masterLockId);
                if (masterLock == null || masterLock.codes == null) {
                    continue;
                }
                for(GetShopLockCode code : masterLock.codes.values()) {
                    if(code.inUse()) {
                        String curCode = code.fetchCodeToAddToLock();
                        codesAdded.add(curCode);
                        
                        if(!deviceToUpdate.hasCode(curCode)) {
                            //Add the code to the first available slot
                            //and signal the processor to update the lock.
                            for(int i = 6; i < deviceToUpdate.maxNumberOfCodes; i++) {
                                GetShopLockCode deviceCode = deviceToUpdate.codes.get(i);
                                if(!deviceCode.inUse()) {
                                    deviceCode.code = curCode;
                                    deviceCode.addedToLock = null;
                                    deviceCode.setInUse(true);
                                    saveLock(deviceToUpdate);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
            
            for(int i = 5; i < deviceToUpdate.maxNumberOfCodes; i++) {
                GetShopLockCode deviceCode = deviceToUpdate.codes.get(i);
                if(deviceCode.inUse()) {
                    String code = deviceCode.fetchCodeToAddToLock();
                    if(!codesAdded.contains(code)) {
                        deviceCode.refreshCode();
                        saveLock(deviceToUpdate);
                    }
                }
            }
        }
    }

    public List<GetShopDevice> getAllLocksCached(String serverSource) {
        doUpdateLockList = false;
        return getAllLocks(serverSource);
    }

    private void finalizeLock(GetShopDevice dev) {
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

    private List<String> getServerSoures() {
        HashMap<String, String> servers = new HashMap();
        for(GetShopDevice dev : devices.values()) {
            servers.put(dev.serverSource, "2");
        }
        return new ArrayList(servers.keySet());
    }

    public void claimUsage(String deviceId, GetShopLockCode code, String source) {
        GetShopDevice device = devices.get(deviceId);
        if (device != null) {
            device.markInUse(code.slot, source);
            saveObject(device);    
        }
    }

    public void removeCodeOnLockBySlotId(String deviceId, Integer slot) {
        GetShopDevice device = devices.get(deviceId);
        if (device != null) {
            device.removeCodeBySlotId(slot);
            saveObject(device);
        }
    }

    @Override
    public void triggerFetchingOfCodes(String ip, String deviceId) {
        PmsLockServer server = pmsManager.getConfigurationSecure().getLockServerBasedOnHostname(ip);
        if (server == null) {
            throw new RuntimeException("Could  not start fetching of code, no servers with hostname: " + ip);
        }
        
        GetShopDevice device = getDeviceForServer(server.serverSource, deviceId);
    }

    @Override
    public void addLockLogs(List<GetShopDeviceLog> logs, String code) {
        if (!code.equals("asdfi0u23l4knraslnjdfakjwenrlikqnfklasdfbaewjhbq2hjb4rl1khb34r12lh34")) {
            throw new ErrorException(26);
        }
        
        List<GetShopDeviceLog> logsToSave = logs.stream()
                .filter(o -> !alreadyGotLog(o))
                .collect(Collectors.toList());
        
        for (GetShopDeviceLog log : logsToSave) {
            
            if (log.code == null || log.code.isEmpty()) {
                log.code = getCodeForLog(log);
            }
            
            saveObject(log);
            deviceLogs.put(log.id, log);
            
            if (log.event == 2) {
                logOldAccess(""+log.deviceId, log.serverSource);
            }
            pmsManager.logChanged(log);
        }
    }

    
    private boolean alreadyGotLog(GetShopDeviceLog log) {
        for (GetShopDeviceLog savedLog : deviceLogs.values()) {
            if (savedLog.isSame(log)) {
                return true;
            }
        }
        
        return false;
    }
    
    
    private GetShopDevice getDeviceForServer(String serverSource, String deviceId) {
        List<GetShopDevice> devices = this.devices.values().stream()
                .filter(dev -> dev.serverSource != null && dev.serverSource.equals(serverSource))
                .collect(Collectors.toList());
        
        for (GetShopDevice device : devices) {
            if (device.serverSource != null && device.serverSource.equals(serverSource) && device.zwaveid != null && device.zwaveid.equals(Integer.parseInt(deviceId))) {
                return device;
            }
        }
        
        return null;
    }

    @Override
    public void triggerMassUpdateOfLockLogs() {
        for (String serverSource : pmsManager.getConfiguration().lockServerConfigs.keySet()) {
            PmsLockServer server = pmsManager.getConfiguration().lockServerConfigs.get(serverSource);
            
            List<GetShopDevice> locks = getAllLocks(serverSource);
            for (GetShopDevice dev : locks) {
                if (dev.zwaveid != null && dev.zwaveid.intValue() > 0) {
                    triggerFetchingOfCodes(server.arxHostname, ""+dev.zwaveid.intValue());
                }
            }
        }
    }

    private String getCodeForLog(GetShopDeviceLog log) {
        GetShopDevice device = getDeviceForServer(log.serverSource, ""+log.deviceId);
        if (device == null)
            return "";
        
        if (device.codes.get(log.uId) == null)
            return "";
        
        return ""+device.codes.get(log.uId).code;
    }

    class GetshopLockCodeManagemnt extends Thread {

        private final GetShopDevice device;
        
        private String hostname = "";
        private String username = "";
        private String password = "";
        private List<BookingItem> items;
        private boolean stopUpdatesOnLock;
        private final boolean useNewQueueCheck;
        private final String storeId;

        public GetshopLockCodeManagemnt(GetShopDevice device, 
                String username, 
                String password, 
                String hostname, 
                List<BookingItem> items, 
                boolean stopUpdatesOnLock,
                boolean useNewQueueCheck,
                String storeId) {
            this.device = device;
            this.hostname = hostname;
            this.password = password;
            this.username = username;
            this.items = items;
            this.storeId = storeId;
            this.stopUpdatesOnLock = stopUpdatesOnLock;
            this.useNewQueueCheck = useNewQueueCheck;
        }
        
        public void setCode(Integer offset, String code, boolean remove) {
            String doUpdate = "0";
            if(!remove) {
                doUpdate = "1";
            }
            
            try {
                String addr = "http://"+hostname+":8083/" + URLEncoder.encode("ZWaveAPI/Run/devices["+device.zwaveid+"].UserCode.Set("+offset+","+code+","+doUpdate+")", "UTF-8");
                GetshopLockCom.httpLoginRequest(addr,username,password);
            } catch (Exception ex) {
                GetShopLogHandler.logStack(ex, storeId);
            }
            
        }
        
        @Override
        public void run() {
            if(hasConnectivity()) {
                int codesAdded = 0;
                List<Integer> offsets = new ArrayList(device.codes.keySet());
                offsets = Lists.reverse(offsets);
                
                for(Integer offset : offsets) {
                    if(stopUpdatesOnLock) { continue; }
                    GetShopLockCode code = device.codes.get(offset);
                    if(code.needUpdate()) {
                        if(code.needToBeRemoved()) {
                            code.refreshCode();
                        }
                        boolean added = false;
                        for(int i = 0; i < 10; i++) {
                            if(stopUpdatesOnLock) { continue; }
                            int codesToAdd = 2;
                            if(useNewQueueCheck) {
                                codesToAdd = 10;
                            }
                            
                            if(codesAdded >= codesToAdd && !device.needForceRemove() && !device.isSubLock()) {
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

                            logPrint("\t Need to add code to offsett: " + offset + " (" + device.name + ")" +  " - added to lock : " + code.addedToLock + ",code refreshed: " + code.codeRefreshed + ", in use: " + code.inUse() + ", need to be removed: " + code.needToBeRemoved + ", slot: " + code.slot + "(code: " + code.code + ")");
                            setCode(offset, code.fetchCodeToAddToLock(), true);
                            try {
                                GetShopHotelLockCodeResult result = getSetCodeResult(offset);
                                waitForEmptyQueue();
                                if(result == null || (result.hasCode != null && result.hasCode.value != null && result.hasCode.value.equals(true))) {
                                    logPrint("\t\t Code alread set... should not be on offset: " + offset + " (" + device.name + ")"  + "(code: " + code.code + ")");
                                    if(result == null) {
                                        logPrint("Result is null");
                                    }
                                } else {
                                    if(code.needForceRemove()) {
                                        code.addedToLock = null;
                                        code.unsetForceRemove();
                                        device.needSaving = true;
                                        break;
                                    }
                                    logPrint("\t\t We are ready to set code to " +  offset + " attempt: " + i + " (" + device.name + ")" + ", id: " + device.zwaveid  + "(code: " + code.code + ")");
                                    for(int j = 0; j < 24; j++) {
                                        setCode(offset, code.fetchCodeToAddToLock(), false);
                                        waitForEmptyQueue();
                                        GetShopHotelLockCodeResult res = getSetCodeResult(offset);
                                        if(res != null && res.hasCode != null && res.hasCode.value != null && res.hasCode.value.equals(true)) {
                                            code.setAddedToLock();
                                            device.needSaving = true;
                                            logPrint("\t\t Code was successfully set on offset " + offset + "(" + j + " attempt)"+ " (" + device.name + ")"  + "(code: " + code.code + ")");
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
                        if(!useNewQueueCheck) {
                            try { Thread.sleep(10000); }catch(Exception e) {}
                        }
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
                GetShopLogHandler.logStack(ex, storeId);
            }
            return null;
        }

        private boolean hasConnectivity() {
            if (hostname == null || hostname.isEmpty()) {
                return false;
            }
            
            if (username == null || username.isEmpty()) {
                return false;
            }
            
            if (password == null || password.isEmpty()) {
                return false;
            }
            
             try {
                logPrint("Checking for connectivity for " + device.name + " (" + device.zwaveid + ")");
                String postfix = "ZWave.zway/Run/devices["+device.zwaveid+"].SendNoOperation()";
                postfix = URLEncoder.encode(postfix, "UTF-8");
                String address = "http://"+hostname+":8083/" + postfix;
                try {
                    GetshopLockCom.httpLoginRequest(address,username,password);
                }catch(ConnectException e) {
                    logPrint("Failed to connect to address: " + address + " message: " + e.getMessage());
                    return false;
                }
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
                GetShopLogHandler.logStack(ex, storeId);
            }
            return false;
        }
        
        public boolean isDoneProcessingQueue(String queue) {
            Gson gson = new Gson();
            List<Object> toParse = new ArrayList();
            try {
                toParse = gson.fromJson(queue, toParse.getClass());
                for(Object a : toParse) {
                    String line = a.toString();
                    if(line.contains("0.20000000298023224")) {
                        continue;
                    }
                    String[] lineElements = line.split(",");
                    if(line.length() > 8 && lineElements[5].trim().equals("1.0")) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }catch(Exception e) {
                logPrint("failed to parse queue: " + queue);
            }
            return true;
        }
        
        private void waitForEmptyQueue() {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.SECOND, 360);
            String postfix = "ZWave.zway/InspectQueue";
            while(true) {
                try {
                    String address = "http://"+hostname+":8083/" + postfix;
                    String res = "";
                    try {
                        res = GetshopLockCom.httpLoginRequest(address,username,password);
                    }catch(ConnectException d) {
                        logPrint("Exception, server does not responde for store: " +storeId + " message: " + d.getMessage() + ", address: " + address);
                        return;
                    }
                    if(useNewQueueCheck && isDoneProcessingQueue(res)) {
                        break;
                    }
                    
                    if(res.equals("[]")) {
                        break;
                    }
                    Thread.sleep(2000);
                }catch(Exception e) {
                    logPrintException(e);
                    return;
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
            if(obj instanceof GetShopDeviceLog) {
                GetShopDeviceLog log = (GetShopDeviceLog)obj;
                deviceLogs.put(log.id, log);
            }
            if(obj instanceof GetShopLockMasterCodes) {
                masterCodes = (GetShopLockMasterCodes) obj;
            }
        }
        createScheduler("pmsprocessor", "* * * * *", CheckAllOkGetShopLocks.class);
//        createScheduler("pmsprocessor_lock", "30 23,04 * * *", UpdateLockList.class);
    }
    
    public String getUsername(String serverSource) {
        if (pmsManager != null && pmsManager.getConfigurationSecure() != null && pmsManager.getConfigurationSecure().getLockServer(serverSource) != null) {
            return pmsManager.getConfigurationSecure().getLockServer(serverSource).arxUsername;
        }
        
        return null;
    }
    
    public String getHostname(String serverSource) {
        if (pmsManager != null && pmsManager.getConfigurationSecure() != null && pmsManager.getConfigurationSecure().getLockServer(serverSource) != null) {
            return pmsManager.getConfigurationSecure().getLockServer(serverSource).arxHostname;
        }
        
        return null;
    }
    
    public String getPassword(String serverSource) {
        if (pmsManager != null && pmsManager.getConfigurationSecure() != null && pmsManager.getConfigurationSecure().getLockServer(serverSource) != null) {
            return pmsManager.getConfigurationSecure().getLockServer(serverSource).arxPassword;
        }
        
        return null;
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

    public void openLock(String lockId, String action) {
        GetShopDevice dev = devices.get(lockId);
        PmsLockServer lockServer = getLockServerForDevice(dev);
        if(lockServer != null && lockServer.isGetShopLockBox()) {
            openGetShopLockBox(lockId, action);
        } else {
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
                logPrintException(ex);
            }
        }
    }
    public void openLock(String lockId) {
        openLock(lockId, "");
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
            logPrint("Lock device :" + lockId + " does not exists, room: ( " + item.bookingItemName);
        } else {
            dev.removeCode(code);
            saveObject(dev);
        }
    }

    @Override
    public void checkIfAllIsOk() {
        List<String> sources = getServerSoures();
        for(int h = 0; h < sources.size();h++) {
            String source = sources.get(h);
            if(source == null || source.isEmpty()) {
                source = "default";
            }
            
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
            checkAndUpdateSubLocks();
            checkDoorsWithOpeningHours();
            for(GetShopDevice dev : devices.values()) {
                if(dev.needSaving) {
                    dev.needSaving = false;
                    saveObject(dev);
                }

                if(dev.isLock()) {
                    checkForMasterCodeUpdates(dev);
                }
            }

            List<BookingItem> items = bookingEngine.getBookingItems();
            GetShopDevice toSet = null;
            for(GetShopDevice dev : devices.values()) {
                String deviceServerSource = dev.serverSource;
                if(deviceServerSource == null || deviceServerSource.isEmpty()) {
                    deviceServerSource = "default";
                }
                if(!deviceServerSource.equals(source)) {
                    continue;
                }
                if(connectedToBookingEngineItem(dev, items) == null) {
                    if(!dev.isSubLock()) {
                        continue;
                    }
                }

                if(isUpdatingSource(dev.serverSource)) {
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
                PmsLockServer server = getLockServerForDevice(toSet);
                if(server != null && server.doNotResponde()) {
                    continue;
                }
                toSet.beingUpdated = true;
                toSet.lastTriedUpdate = new Date();
                String user = getUsername(toSet.serverSource);
                String pass = getPassword(toSet.serverSource);
                String host = getHostname(toSet.serverSource);
                boolean useNewQueueCheck = pmsManager.getConfigurationSecure() != null ? pmsManager.getConfigurationSecure().useNewQueueCheck : false;
                
                GetshopLockCodeManagemnt mgr = new GetshopLockCodeManagemnt(toSet, user, pass, host, items, stopUpdatesOnLock, useNewQueueCheck, storeId);
                mgr.setName("Checking if all locks are ok for store: " + storeId);
                mgr.start();
            }
        }
    }
    
    private boolean isUpdatingSource(String serverSource) {
        if(serverSource == null) {
            serverSource = "";
        }
        for(GetShopDevice dev : devices.values()) {
            String toCheck = dev.serverSource;
            if(toCheck == null) {
                toCheck = "";
            }
            if(toCheck.equals(serverSource) && dev.beingUpdated) {
                return true;
            }
        }
        return false;
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
        
        masterCodes.codes.put(slot, code);
        saveObject(masterCodes);
    }

    @Override
    public GetShopLockMasterCodes getMasterCodes() {
        if(masterCodes.checkIfEmtpy()) {
            saveObject(masterCodes);
        }
        
        if(!masterCodes.codes.containsKey(1)) { masterCodes.codes.put(1, "1234"); }
        if(!masterCodes.codes.containsKey(2)) { masterCodes.codes.put(2, "1234"); }
        if(!masterCodes.codes.containsKey(3)) { masterCodes.codes.put(3, "1234"); }
        if(!masterCodes.codes.containsKey(4)) { masterCodes.codes.put(4, "1234"); }
        if(!masterCodes.codes.containsKey(5)) { masterCodes.codes.put(5, "1234"); }
        
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

    public void finalizeLocks() {
        for(GetShopDevice dev : devices.values()) {
            if(dev.isLock()) {
                finalizeLock(dev);
            }
        }
    }
    
    @Override
    public void accessEvent(String id, String code, String domain) {
        GetShopDeviceLog log = new GetShopDeviceLog();
        log.deviceId = new Integer(id);
        log.code = code;
        log.event = 2;
        log.serverSource = getHostname(domain);
        
        ArrayList<GetShopDeviceLog> logs = new ArrayList();
        logs.add(log);
     
        addLockLogs(logs, "asdfi0u23l4knraslnjdfakjwenrlikqnfklasdfbaewjhbq2hjb4rl1khb34r12lh34");
    }

    private void logOldAccess(String id, String domain) throws ErrorException, NumberFormatException {
        for(GetShopDevice dev : devices.values()) {
            Integer zwaveid = new Integer(id);
            if(dev.zwaveid.equals(zwaveid) && dev.isSameSource(domain)) {
                dev.accessLog.add(new Date());
                saveObject(dev);
            }
        }
    }
}
