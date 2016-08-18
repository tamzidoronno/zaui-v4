/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplock;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
    
        
    @Autowired
    MessageManager messageManager;

    @Autowired
    BookingEngine bookingEngine;
    
    @Autowired
    FrameworkConfig frameworkConfig;
    
    @Override
    public void refreshLock(String lockId) {
        GetShopDevice dev = devices.get(lockId);
        for(GetShopLockCode code : dev.codes.values()) {
            code.resetOnLock();
        }
    }
    

    private void checkForMasterCodeUpdates(GetShopDevice dev) {
        for(int i = 1; i <= 5; i++) {
            GetShopLockCode code = dev.codes.get(i);
            String masterCode = masterCodes.codes.get(i);
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
    public void deleteAllDevices(String password) {
        if(!password.equals("fdsafbvvre4234235t")) {
            return;
        }
        for(GetShopDevice dev : devices.values()) {
            deleteObject(dev);
        }
        devices.clear();
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
//                if(code.inUse()) {
                    codes.add(code.fetchCodeToAddToLock());
//                }
            }
        }
        
        for(String masterCode : masterCodes.codes.values()) {
            codes.add(masterCode);
        }
        return codes;
    }

    class GetshopLockCodeManagemnt extends Thread {

        private final GetShopDevice device;
        
        private String hostname = "";
        private String username = "";
        private String password = "";
        private List<BookingItem> items;

        public GetshopLockCodeManagemnt(GetShopDevice device, String username, String password, String hostname, List<BookingItem> items) {
            this.device = device;
            this.hostname = hostname;
            this.password = password;
            this.username = username;
            this.items = items;
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
            if(hasConnectivity()) {
                for(Integer offset : device.codes.keySet()) {
                    GetShopLockCode code = device.codes.get(offset);
                    if(code.needUpdate()) {
                        if(code.needToBeRemoved()) {
                            code.refreshCode();
                        }
                        for(int i = 0; i < 10; i++) {
                            logPrint("\t Need to add code to offsett: " + offset + " (" + device.name + ")");
                            setCode(offset, code.fetchCodeToAddToLock(), true);
                            try {
                                GetShopHotelLockCodeResult result = getSetCodeResult(offset);
                                Thread.sleep(3000);
                                if(result != null && result.hasCode != null && result.hasCode.value != null && result.hasCode.value.equals(true)) {
                                    logPrint("\t\t Code alread set... should not be on offset: " + offset + " (" + device.name + ")");
                                } else {
                                    logPrint("\t\t We are ready to set code to " +  offset + " attempt: " + i + " (" + device.name + ")");
                                    for(int j = 0; j < 24; j++) {
                                        setCode(offset, code.fetchCodeToAddToLock(), false);
                                        Thread.sleep(5000);
                                        GetShopHotelLockCodeResult res = getSetCodeResult(offset);
                                        if(res != null && res.hasCode != null && res.hasCode.value != null && res.hasCode.value.equals(true)) {
                                            code.setAddedToLock();
                                            device.needSaving = true;
                                            device.lastTriedUpdate = null;
                                            logPrint("\t\t Code was successfully set on offset " + offset + "(" + j + " attempt)"+ " (" + device.name + ")");
                                            break;
                                        } else {
                                            logPrint("\t\t Failed to set code to offset " + offset + " on attempt: " + j+ " (" + device.name + ")");
                                        }
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
               
                try { Thread.sleep(90000); }catch(Exception e) {}
                 
                postfix = "ZWave.zway/Run/devices["+device.zwaveid+"]";
                postfix = URLEncoder.encode(postfix, "UTF-8");
                address = "http://"+hostname+":8083/" + postfix;
                
                Gson gson = new Gson();
                String res = GetshopLockCom.httpLoginRequest(address,username,password);
                ZWaveDevice result = gson.fromJson(res, ZWaveDevice.class);
                boolean isFailed = new Boolean(result.data.isFailed.value + "");
                if(isFailed) { 
                    logPrint("No connectivity found for :" + device.name + " (" + device.zwaveid + ")");
                    return false; }
                return true;
            } catch (Exception ex) {
                Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return false;
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
    
    public String getUsername() {
        return pmsManager.getConfigurationSecure().arxUsername;
    }
    
    public String getHostname() {
        return pmsManager.getConfigurationSecure().arxHostname;
    }
    
    public String getPassword() {
        return pmsManager.getConfigurationSecure().arxPassword;
    }
    
    private String httpLoginRequest(String address) throws Exception {
        String username = getUsername();
        String password = getPassword();
        return GetshopLockCom.httpLoginRequest(address, username, password);
    }

    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception {
        SimpleDateFormat s1 = new SimpleDateFormat("dd.MM.YYYY HH:mm");
        String startString = start.getTime()/1000 + "";
        String endString = end.getTime()/1000 + "";
        
        id = URLEncoder.encode(id, "UTF-8");
        
        String address = "http://"+getHostname()+":8080/storecode/"+door+"/"+id+"/"+startString+"/"+endString+"/" + code;
        logPrint("Executing: " + address);
        return this.httpLoginRequest(address);
    }

    public String removeCode(String pmsBookingRoomId) throws Exception {
        String id = URLEncoder.encode(pmsBookingRoomId, "UTF-8");
        String address = "http://"+getHostname()+":8080/deletekey/"+id;
        logPrint("Executing: " + address);
        return this.httpLoginRequest(address);
    }

    @Override
    public String getCodeForLock(String lockId) {
        GetShopDevice dev = devices.get(lockId);
        if(dev == null) {
            logPrint("Lock devie where not found");
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
    public List<GetShopDevice> getAllLocks() {
        String hostname = getHostname();
        if(hostname == null || hostname.isEmpty()) { return new ArrayList(); }
        try {
            String address = "http://" + hostname + ":8083/ZWave.zway/Run/devices";
            String res = httpLoginRequest(address);
            
            HashMap<Integer, ZWaveDevice> result = new HashMap();
            Type type = new TypeToken<HashMap<Integer, ZWaveDevice>>(){}.getType();
            Gson gson = new Gson();
            result = gson.fromJson(res, type);
            List<GetShopDevice> currentDevices = new ArrayList();
            for(Integer offset : result.keySet()) {
                ZWaveDevice device = result.get(offset);
                GetShopDevice gsdevice = new GetShopDevice();
                gsdevice.setDevice(device);
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
                devices.remove(torev.id);
            }
            
        } catch (Exception ex) {
            Logger.getLogger(GetShopLockManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        finalizeLocks();
        
        ArrayList<GetShopDevice> res = new ArrayList(devices.values());
        Collections.sort(res, new Comparator<GetShopDevice>(){
             public int compare(GetShopDevice o1, GetShopDevice o2){
             if(o1.name == null || o2.name == null) {
             return 0;
         }
         return o1.name.compareTo(o2.name);
     }
});
        
        return res;
    }

    @Override
    public void openLock(String lockId) {
        String hostname = getHostname();
        if(hostname == null || hostname.isEmpty()) { return; }
        GetShopDevice dev = devices.get(lockId);
        String postfix = "ZWave.zway/Run/devices["+dev.zwaveid+"].instances[0].commandClasses[98].Set(1)";
        try {
            postfix = URLEncoder.encode(postfix, "UTF-8");
        }catch(Exception e) {}
        String address = "http://"+getHostname()+":8083/" + postfix;
        try {
            httpLoginRequest(address);
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
    public void removeCodeOnLock(String lockId, String code) {
        GetShopDevice dev = devices.get(lockId);
        if(dev == null) {
            System.out.println("Lock device :" + lockId + " does not exists");
        } else {
            dev.removeCode(code);
            saveObject(dev);
        }
    }

    @Override
    public void checkIfAllIsOk() {
        if(!frameworkConfig.productionMode) {
            return;
        }
        if(!pmsManager.getConfigurationSecure().isGetShopHotelLock()) {
            return;
        }
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
        for(GetShopDevice dev : devices.values()) {
            if(connectedToBookingEngineItem(dev, bookingEngine.getBookingItems()) == null) {
                continue;
            }
            if(dev.isLock() && !dev.beingUpdated && dev.needUpdate()) {
                dev.beingUpdated = true;
                dev.lastTriedUpdate = new Date();
                String user = getUsername();
                String pass = getPassword();
                String host = getHostname();
                
                GetshopLockCodeManagemnt mgr = new GetshopLockCodeManagemnt(dev, user, pass, host, items);
                mgr.start();
                return;
            }
        }
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
        for(GetShopDevice dev : devices.values()) {
            if(dev.zwaveid.equals(gsdevice.zwaveid)) {
                dev.type = gsdevice.type;
                dev.name = gsdevice.name;
                dev.isAwake = gsdevice.isAwake;
                dev.isFailed = gsdevice.isFailed;
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
            devices.put(gsdevice.id, gsdevice);
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
    
}
