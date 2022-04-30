/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.getshoplocksystem;

import static com.thundashop.core.arx.WrapClient.wrapClient;

import com.thundashop.core.common.*;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.axis.encoding.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.mongodb.morphia.annotations.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ktonder
 */
public abstract class LockServerBase extends DataCommon {

    private static final Logger logger = LoggerFactory.getLogger(LockServerBase.class);

    private boolean hasConnection = false;
    private Date lastChecked;
    private Date connectionDown;
    private Date lastConnected;
    
    public Date lastPing;
    public String hostname;
    public String username;
    public String givenName;
    
    public String token;
    
    @Administrator
    public String password;
    
    @Transient
    @ExcludeFromJson
    private GetShopLockSystemManager getShopLockSystemManager;
    
    @Transient
    @ExcludeFromJson
    private List<AccessEvent> accessEvents = new ArrayList<>();
    

    public void setManger(GetShopLockSystemManager manager) {
        this.getShopLockSystemManager = manager;
    }

    public GetShopLockSystemManager getManager() {
        return this.getShopLockSystemManager;
    }
    
    protected synchronized  void lostConnection() {
        if (hasConnection) {
            connectionDown = new Date();
        }
        
        lastChecked = new Date();
        hasConnection = false;
    }
    
    protected synchronized void successfullyMadeRequest() {
        lastConnected = new Date();
        if (!hasConnection) {
            logConnectionEstablished();
        }
        hasConnection = true;   
    }

    private void logConnectionEstablished() {
        ServerConnectionLog log = new ServerConnectionLog();
        log.connectionDown = connectionDown;
        log.connectionUp = lastConnected;
        if (getShopLockSystemManager != null) {
            getShopLockSystemManager.saveObject(log);
        }
    }
    
    public synchronized String httpLoginRequestZwaveServer(String address) {
        
        try {
            if(address.contains("data.givenName.value")) {
                address = URLEncoder.encode(address, "UTF-8").replace("+", "%20");
            } else {
                address = URLEncoder.encode(address, "UTF-8");
            }
        } catch (UnsupportedEncodingException ex) {
            logger.error("sid-{}", storeId, ex);
        }
        
        String loginUrl = "http://"+hostname+":8083/"+address;
        
        HttpParams my_httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(my_httpParams, 3000);
        HttpConnectionParams.setSoTimeout(my_httpParams, 6000);
        
        DefaultHttpClient client = new DefaultHttpClient(my_httpParams);
        try {
            client = wrapClient(client);
            HttpResponse httpResponse;

            HttpEntity entity;
            HttpGet request = new HttpGet(loginUrl);
            byte[] bytes = (username + ":" + password).getBytes();
            String encoding = Base64.encode(bytes);

            request.addHeader("Authorization", "Basic " + encoding);
            httpResponse = client.execute(request);

            int statusCode = httpResponse.getStatusLine().getStatusCode();

            if (statusCode != 200) {
                logger.error("sid-{} response status code: {} , username: {} , loginUrl: {}",
                        storeId, statusCode, username, loginUrl);
            }

            if(statusCode == 401) {
                return "401";
            }
            entity = httpResponse.getEntity();

            if (entity != null) {
                InputStream instream = entity.getContent();
                try {
                    int ch;
                    StringBuilder sb = new StringBuilder();
                    while ((ch = instream.read()) != -1) {
                        sb.append((char) ch);
                    }
                    String result = sb.toString();
                    return result.trim();
                } finally {
                    instream.close();
                }
            }
            successfullyMadeRequest();
        } catch (Exception x) {
            lostConnection();
            logger.error("sid-{} exception during zwave call message:", storeId, x);
        } finally {
            if (client != null) {
                client.getConnectionManager().shutdown();
            }
        }
        
        return "";
    }
    
    protected void saveMe() {
        if (getShopLockSystemManager != null) {
            getShopLockSystemManager.saveObject(this);
        }
        
        startUpdatingOfLocks();
        if (getShopLockSystemManager != null) {
            getShopLockSystemManager.clearCache(this);
        }
    }
    
    public void setDetails(String hostname, String userName, String password, String givenName, String token) {
        this.hostname = hostname;
        this.password = password;
        this.username = userName;
        this.givenName = givenName;
        this.token = token;
        saveMe();
    }
    
    void codeRemovedFromLock(String id, UserSlot slot) {
        Lock lock = getLock(id);
        LockCode code = lock.codeRemovedFromLock(slot.slotId);
        LockCodeLog log = new LockCodeLog();
        log.code = code;

        if (getShopLockSystemManager != null) {
            getShopLockSystemManager.saveObject(log);
        }
        
        saveMe();
    }
    
    abstract Lock getLock(String id);
    
    public void codeAddedSuccessfully(String id, int slotId) {
        Lock lock = getLock(id);
        lock.codeAddedSuccessfully(slotId);
        saveMe();
    }
    
    public String getId() {
        return id;
    }
    
   
    public void releaseFromGroup(String lockId, String groupId) {
        Lock lock = getLock(lockId);
        lock.releaseAllSlotsForGroup(groupId);
        saveMe();
    }
    
    
    public void markCodeForDeletion(String lockId, int slotId) {
        Lock lock = getLock(lockId);
        lock.markCodeForDeletion(slotId);
        saveMe();
    }
    
    public void markCodeForResending(String lockId, int slotId) {
        Lock lock = getLock(lockId);
        lock.markCodeForResending(slotId, getManager().getCodeSize());
        saveMe();
    }

    public void claimLockGroupUseOfSlot(String lockId, int slotId, String groupId) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            lock.claimSlotForGroup(slotId, groupId);
            saveMe();
        }
    }

    public void syncGroupSlot(LockGroup group, int slotId) {
        MasterUserSlot groupCode = group.getGroupLockCodes().get(slotId);
        groupCode.subSlots.forEach(slot -> {
            Lock lock = getLock(slot.connectedToLockId);

            if (lock != null && groupCode.code != null) {
                boolean changed = lock.setCodeObject(slot.slotId, groupCode.code, getManager().getCodeSize());
                
                if (changed) {
                    lock.markCodeForResending(slot.slotId, getManager().getCodeSize());
                }
            }
        });
        
        saveMe(); 
    }
    
    public void syncGroup(LockGroup group) {
        group.getGroupLockCodes().values().forEach(groupCode -> syncGroupSlot(group, groupCode.slotId));
        saveMe();
    }
    
    public void startUpdatingOfLocks() {
        
    }
    
    public void markCodeAsUpdatedOnLock(String lockId, int slotId) {
        getLock(lockId).markCodeAsUpdatedOnLock(slotId);
    }

    public void addAccessHistory(String lockId, int slotId, Date time) {
        AccessHistory history = new AccessHistory();
        history.lockId = lockId; 
        history.userSlot = slotId;
        history.code = getCodeForSlot(time, slotId, lockId);
        history.accessTime = time;
        history.serverId = getId();
        getShopLockSystemManager.saveObject(history);
    }
    
    public void addAccessHistorySeros(String lockId, int slotId, Date time, String code) {
        AccessHistory history = new AccessHistory();
        history.lockId = lockId;
        history.userSlot = slotId;
        history.code = code;
        history.accessTime = time;
        history.serverId = getId();
        getShopLockSystemManager.saveObject(history);
    }

    private String getCodeForSlot(Date time, int slotId, String lockId) {
        Lock lock = getLock(lockId);
        if (lock == null)
            return "";
        
        UserSlot slot = lock.getUserSlot(slotId);
        if (slot == null) {
            return "";
        }
        
        if (slot.code == null) {
            return "";
        }
        
        if ((slot.code.addedDate != null && slot.code.addedDate.after(time)) && (slot.code.removedDate == null || slot.code.removedDate.before(time))) {
            return ""+slot.code.pinCode;
        }
        
        return "";
    }

    public boolean checkToken(String tokenId) {
        if (tokenId == null || tokenId.isEmpty()) {
            return true;
        }
        if (!tokenId.equals(this.token)) {
            return true;
        }
        return false;
    }
    
    public void renameLock(String lockId, String name) {
        Lock lock = getLock(lockId);
        if (lock != null) {
            lock.name = name;
            saveMe();
        }
    }
    
    public void addEvent(AccessEvent event) {
        accessEvents.add(event);
    }

    public List<AccessEvent> getAccessEvents() {
        List<AccessEvent> events = new ArrayList<>(accessEvents);
        accessEvents.clear();
        return events;
    }
    
    public String getGivenName() {
        return givenName;
    }
    
    public void setLastPing(Date date) {
        lastPing = date;
    }

    public Date getLastPing() {
        return lastPing;
    }
    
    public String getAccessToken() {
        return token;
    }
    
    /**
     * Override this if you want to only use the access groups.
     * 
     * @return 
     */
    public boolean useSlotConcept() {
        return true;
    }
    
    /**
     * This function will be called only if the useSlotConcept is returning false.
     * 
     * Override this method in order to keep track of what locks are connected to what lockgroups
     * 
     * @param groupId
     * @param lockIds 
     */
    public void setLockstoGroup(String groupId, List<String> lockIds) {
    }
    
    
    /**
     * This function will be called only if the useSlotConcept is returning true.
     * 
     * Override this method in order return the locks connected to a group.
     * 
     * @param groupId
     */
    
    public List<String> getLocksForGroup(String groupId) {
        return new ArrayList<>();
    }

    @Override
    public String toString() {
        return "LockServerBase{" +
                "hasConnection=" + hasConnection +
                ", lastChecked=" + lastChecked +
                ", connectionDown=" + connectionDown +
                ", lastConnected=" + lastConnected +
                ", lastPing=" + lastPing +
                ", hostname='" + hostname + '\'' +
                ", username='" + username + '\'' +
                ", givenName='" + givenName + '\'' +
                ", token='" + token + '\'' +
                ", password='" + password + '\'' +
                ", getShopLockSystemManager=" + getShopLockSystemManager +
                ", accessEvents=" + accessEvents +
                ", id='" + id + '\'' +
                ", storeId='" + storeId + '\'' +
                ", deleted=" + deleted +
                ", className='" + className + '\'' +
                ", rowCreatedDate=" + rowCreatedDate +
                ", lastModified=" + lastModified +
                ", gs_manager='" + gs_manager + '\'' +
                ", colection='" + colection + '\'' +
                ", gsDeletedBy='" + gsDeletedBy + '\'' +
                ", lastModifiedByUserId='" + lastModifiedByUserId + '\'' +
                ", getshopModule='" + getshopModule + '\'' +
                ", deepFreeze=" + deepFreeze +
                '}';
    }
}
