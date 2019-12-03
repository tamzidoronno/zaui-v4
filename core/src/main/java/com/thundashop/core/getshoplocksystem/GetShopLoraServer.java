/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class GetShopLoraServer extends LockServerBase implements LockServer {

    
    private final String USER_AGENT = "Mozilla/5.0";
    
    private Map<String, GetShopLock> locks = new HashMap();
    
//    @Autowired
//    private WebManager webManager;
    
    @Override
    public Lock getLock(String id) {
        return locks.get(id);
    }

    @Override
    public List<Lock> getLocks() {
        return new ArrayList(locks.values());
    }

    @Override
    public void fetchLocksFromServer() {
        Gson gson = new Gson();
        
        String hostname = getHostname();
        
        Type listType = new TypeToken<ArrayList<LoraDevice>>(){}.getType();
        
        String res = getHtml("http://"+hostname+":5000/devices?token="+getAccessToken());
        
        if (res == null) {
            return;
        }
        
        List<LoraDevice> loraDevices = new Gson().fromJson(res, listType);
        
        for (LoraDevice device : loraDevices) {
            GetShopLock lock = getLockByDeviceId(device.deviceId);
            
            if ( lock == null) {
                lock = new GetShopLock();
                lock.deviceId = device.deviceId;
                lock.maxnumberOfCodes = 2000;
                lock.typeOfLock = "GetShop Lock";
                lock.zwaveDeviceId = device.deviceId;
                lock.name = "Lock " + device.deviceId;
                lock.initializeUserSlots();
                locks.put(lock.id, lock);
            }
        }
        
        saveMe();
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public void save() {
        saveMe();
    }

    @Override
    public void prioritizeLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void lockSettingsChanged(LockSettings lockSettings) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void deactivatePrioritingOfLock() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finalizeServer() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addTransactionHistory(String tokenId, String lockId, Date accessTime, int userSlot) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void openLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void pulseOpenLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addAccessHistoryEntranceDoor(String lockId, int code, Date date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean hasAccessLogFeature() {
        return false;
    }

    @Override
    public void deleteLock(String lockId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void startUpdatingOfLocks() {
        // TODO - Need to thread 
    }

    @Override
    public void markCodeForResending(String lockId, int slotId) {
        super.markCodeForResending(lockId, slotId); //To change body of generated methods, choose Tools | Templates.
        Lock lock = getLock(lockId);
        transferToLoraGateWay((GetShopLock)lock);
    }
    

    private GetShopLock getLockByDeviceId(int deviceId) {
        return locks.values()
                .stream()
                .filter(o -> o.deviceId == deviceId)
                .findAny()
                .orElse(null);
    }
    
    public void resyncDatabaseWithLoraGateway() {
        List<Lock> locks = getLocks();
        for (Lock lock : locks) {
            GetShopLock getShopLock = (GetShopLock)lock;
            for (UserSlot slot : lock.getUserSlots()) {
                if (slot.inUse && slot.serverLockSlotId != null) {
                    String isSet = slot.code != null && slot.code.addedDate != null ? "true" : "false";
                    getHtml("http://"+hostname+":5000/setCode?token="+getAccessToken()+"&device="+getShopLock.deviceId+"&slot="+slot.serverLockSlotId+"&code="+slot.code.pinCode+"&isSet="+isSet);
                }
            }
        }
        
        getHtml("http://"+hostname+":5000/setDeviceCount?token="+getAccessToken()+"&devicecount=" + locks.size());
    }

    void transferToLoraGateWay(GetShopLock lock) {
        boolean codeUpdated = false;
        
        refreshLockFromServer(lock);
        
        for (UserSlot slot : lock.getUserSlots()) {

            boolean resend = slot.serverLockSlotId != null && slot.code.addedDate == null;
            boolean toBeRemoved = !slot.inUse && slot.serverLockSlotId != null && slot.pinCodeAddedToServer != slot.code.pinCode;
            
            if (resend || toBeRemoved) {
                String res = getHtml("http://"+hostname+":5000/removeCode?token="+getAccessToken()+"&device="+lock.deviceId+"&slot="+slot.serverLockSlotId);
                if (res.equals("MARKED_FOR_REMOVAL") || res.equals("CODE_NOT_ADDED")) {
                    slot.serverLockSlotId = null;
                    codeUpdated = true;
                }
            }
            
            if (slot.inUse && slot.serverLockSlotId == null) {
                Integer nextUnusedSlot = getNextUnusedSlot(lock);
                
                if (nextUnusedSlot == -1) {
                    continue;
                }
                
                slot.serverLockSlotId = nextUnusedSlot;
                String res = getHtml("http://"+hostname+":5000/setCode?token="+getAccessToken()+"&device="+lock.deviceId+"&slot="+nextUnusedSlot+"&code="+slot.code.pinCode+"&isSet=false");
                
                if (res.equals("CODE_RECEIVED")) {
                    codeUpdated = true;
                    slot.setAddedDate(new Date());
                    slot.pinCodeAddedToServer = slot.code.pinCode;
                    System.out.println("Asked to save code " + slot.code.pinCode + " to slot : " + nextUnusedSlot);
                } else {
                    System.out.println("Failed to update code: " + res);
                    slot.serverLockSlotId = null;
                }
            }
        }
        
        if (codeUpdated) {
            saveMe();
        }
    }

    private Integer getNextUnusedSlot(GetShopLock lock) {
        String res = getHtml("http://"+hostname+":5000/nextAvailableSlot?token="+getAccessToken()+"&device="+lock.deviceId);
        
        if (res == null || res.isEmpty()) {
            return -1;
        }
        
        return Integer.parseInt(res);
    }

    private void refreshLockFromServer(GetShopLock lock) {
        String res = getHtml("http://"+hostname+":5000/device?token="+getAccessToken()+"&device="+lock.deviceId);

        if (res == null || res.isEmpty() || res.equals("false")) {
            return;
        }
        
        GetShopLock lockOnServer = new Gson().fromJson(res, GetShopLock.class);
        
        if (lockOnServer == null || lockOnServer.lockUserSlots == null) {
            return;
        }
        
        for (Integer iSlot : lockOnServer.lockUserSlots.keySet()) {
            if (iSlot == null)
                continue;
            
            GetShopUserSlot iGetShopSlot = lockOnServer.lockUserSlots.get(iSlot);
            UserSlot userSlotForLock = lock.getSlotByServerSlotId(iSlot);

            if (userSlotForLock == null) {
                continue;
            }

            if (iGetShopSlot.setOnLock && userSlotForLock.code.addedDate != null) {
                userSlotForLock.codeAddedSuccesfully();
            }
        }
    }

    void doorAccess(int deviceId, int slot, Date date) {
        Lock lock = getLockByDeviceId(deviceId);
        if (lock != null) {
            UserSlot userSlot = lock.getUserSlots().stream()
                    .filter(o -> o.serverLockSlotId != null && o.serverLockSlotId == slot)
                    .findAny()
                    .orElse(null);
            
            if (userSlot != null) {
                addAccessHistory(lock.id, userSlot.slotId, date);
            }
        }
        
    }
    
    public String getHtml(String url) {
        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(5000);
            
            BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));    

            String responseLine;
            StringBuilder responseBuffer = new StringBuilder();

            while((responseLine = responseStream.readLine()) != null) {
                responseBuffer.append(responseLine);
            }

            return responseBuffer.toString();
        } catch (Exception ex) {
            
//            ex.printStackTrace();
            return "";
        }
    }
}
