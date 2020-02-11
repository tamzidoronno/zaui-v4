/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thundashop.core.common.AppContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author ktonder
 */
public class SerosLockSystem extends LockServerBase implements LockServer {

    private HashMap<String, SerosLock> locks = new HashMap();

    /**
     * Key = group Id Value = List of lockIds connected to the group.
     */
    private HashMap<String, List<String>> locksInGroup = new HashMap();

    private HashMap<String, SerosApiKey> serosKeys = new HashMap();

    private final String USER_AGENT = "Mozilla/5.0";

    @Override
    public Lock getLock(String id) {
        return locks.get(id);
    }

    @Override
    public void syncGroup(LockGroup group) {
        HashMap<Integer, MasterUserSlot> masterUserSlots = group.getGroupLockCodes();

        for (Integer slot : masterUserSlots.keySet()) {
            MasterUserSlot masterUserSlot = masterUserSlots.get(slot);
            if (masterUserSlot == null) {
                continue;
            }
            internalSyncGroup(group, masterUserSlot, slot);
        }
    }

    private void internalSyncGroup(LockGroup group, MasterUserSlot masterUserSlot, Integer slot) {
        if (group.isVirtual || masterUserSlot.takenInUseDate != null) {
            createKeyOrUpdate(group, slot, masterUserSlot);
        } else {
            removeKey(group, slot, masterUserSlot);
        }
    }

    @Override
    public void syncGroupSlot(LockGroup group, int slot) {
        MasterUserSlot masterUserSlot = group.getGroupLockCodes().get(slot);
        internalSyncGroup(group, masterUserSlot, slot);
    }

    
    @Override
    public List<Lock> getLocks() {
        return new ArrayList(locks.values());
    }

    @Override
    public void fetchLocksFromServer() {
        String res = getHtml("listlocks", "");

        if (res == null) {
            return;
        }

        Type listType = new TypeToken<ArrayList<SerosLock>>() {
        }.getType();

        List<SerosLock> locksFromServer = new Gson().fromJson(res, listType);

        locksFromServer.stream()
                .forEach(o -> {
                    o.id = o.lockId;
                    o.maxnumberOfCodes = 800;

                    SerosLock inMem = locks.get(o.id);
                    if (inMem == null) {
                        locks.put(o.id, o);
                    } else {
                        inMem.name = o.name;
                        inMem.maxnumberOfCodes = 800;
                    }
                });

        saveMe();
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public void save() {
        this.saveMe();
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

    public String getHtml(String action, String extras) {
        String systemToken = "0688a225-25a2-4f92-b291-a135ef005baf";
        
        String url = "https://" + hostname + "/scripts/api.php?action=" + action + "&systemtoken=" + systemToken + "&customertoken=" + token;
        if (!extras.isEmpty()) {
            url += extras;
        }
        
        

        try {
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setConnectTimeout(5000);

            BufferedReader responseStream = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String responseLine;
            StringBuilder responseBuffer = new StringBuilder();

            while ((responseLine = responseStream.readLine()) != null) {
                responseBuffer.append(responseLine);
            }

            return responseBuffer.toString();
        } catch (Exception ex) {
            ex.printStackTrace();
//            ex.printStackTrace();
            return "";
        }
    }

    @Override
    public boolean useSlotConcept() {
        return false;
    }

    @Override
    public void setLockstoGroup(String groupId, List<String> lockIds) {
        locksInGroup.put(groupId, lockIds);
        save();
    }

    @Override
    public List<String> getLocksForGroup(String groupId) {
        return locksInGroup.get(groupId);
    }

    private void createKeyOrUpdate(LockGroup group, Integer slot, MasterUserSlot masterUserSlot) {
        SerosApiKey apiKey = getSerosLockKey(group.id, slot);

        if (apiKey == null) {
            createKey(group, slot);
        } 
        
        apiKey = getSerosLockKey(group.id, slot);
        if (apiKey == null || apiKey.lastReceivedSerosKey == null) {
            return;
        }
        
        changeCode(masterUserSlot, apiKey, group, slot);
        addLocksMissingInKey(group, apiKey, slot);
        removeLocksThatHasBeenRemovedFromGroup(apiKey, group, slot);
    }

    private void removeLocksThatHasBeenRemovedFromGroup(SerosApiKey apiKey, LockGroup group, Integer slot) {
        for (String lockId : apiKey.lastReceivedSerosKey.lockIds) {
            if (!locksInGroup.get(group.id).contains(lockId)) {
                doRequest(group.id, slot, "removeLockFromKey", "&keyId="+apiKey.lastReceivedSerosKey.id+"&lockId="+lockId);
            }
        }
    }

    private void addLocksMissingInKey(LockGroup group, SerosApiKey apiKey, Integer slot) {
        for (String lockIdInGroup : locksInGroup.get(group.id)) {
            if (!apiKey.lastReceivedSerosKey.lockIds.contains(lockIdInGroup)) {
                doRequest(group.id, slot, "addLockToKey", "&keyId="+apiKey.lastReceivedSerosKey.id+"&lockId="+lockIdInGroup);
            }
        }
    }

    private void changeCode(MasterUserSlot masterUserSlot, SerosApiKey apiKey, LockGroup group, Integer slot) {
        int pinCodeInGetShop = masterUserSlot.code != null ? masterUserSlot.code.pinCode : 0;
        int codeOnKeyFromSeros = apiKey.lastReceivedSerosKey.code;

        if (pinCodeInGetShop != codeOnKeyFromSeros) {
            doRequest(group.id, slot, "changeCodeForKey", "&keyId="+apiKey.lastReceivedSerosKey.id+"&pinCode="+pinCodeInGetShop);
        }
    }

    private void createKey(LockGroup group, Integer slot) {
        try {
            String name = URLEncoder.encode(group.name + " ( " + slot + " )", "utf-8");
            doRequest(group.id, slot, "createKey", "&name=" + name);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void removeKey(LockGroup group, Integer slot, MasterUserSlot masterUserSlot) {
        SerosApiKey apiKey = getSerosLockKey(group.id, slot);

        if (apiKey == null) {
            return;
        }
        
        doRequest(group.id, slot, "deleteKey", "&keyId="+apiKey.serosKeyId);
        serosKeys.values().removeIf(o -> o.equals(apiKey));
    }

    private SerosApiKey getSerosLockKey(String lockGroupId, Integer slot) {
        return serosKeys.values()
                .stream()
                .filter(o -> o.getShopLockGroupId.equals(lockGroupId) && o.getShopSlotNumber.equals(slot))
                .findAny()
                .orElse(null);
    }

    private void doRequest(String lockGroupId, Integer slot, String action, String extra) {
        
        if (AppContext.devMode) {
            System.out.println("Request towards seros lock system has been disabled in dev mode");
            return;
        }
        
        String res = getHtml(action, extra);
        
        Gson gson = new Gson();
        SerosApiKeyResult result = gson.fromJson(res, SerosApiKeyResult.class);
        
        SerosApiKey apiKey = getSerosLockKey(lockGroupId, slot);
        if (apiKey == null) {
            apiKey = new SerosApiKey();
            apiKey.getShopLockGroupId = lockGroupId;
            apiKey.getShopSlotNumber = slot;
        }
        
        if (result != null) {
            apiKey.serosKeyId = result.id;
            apiKey.lastReceivedSerosKey = result;
            serosKeys.put(apiKey.lastReceivedSerosKey.id, apiKey);
            save();    
        }        
    }
}