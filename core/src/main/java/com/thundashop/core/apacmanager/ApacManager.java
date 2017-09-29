/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.apacmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.arx.Door;
import com.thundashop.core.arx.DoorManager;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.getshop.data.GetShopLockCode;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pmsmanager.PmsLockServer;
import com.thundashop.core.pmsmanager.PmsManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class ApacManager extends GetShopSessionBeanNamed implements IApacManager {
    @Autowired
    public DoorManager doorManager;
    
    @Autowired
    public MessageManager messageManager;
    
    @Autowired
    public PmsManager pmsManager;

    public HashMap<String, ApacAccess> accesslist = new HashMap();

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof ApacAccess) {
                ApacAccess db = (ApacAccess)dataCommon;
                accesslist.put(db.id, db);
            }
        }
    }
    
    @Override
    public ApacAccess grantAccess(ApacAccess apacAccess) throws Exception {
        GetShopLockCode code = doorManager.getNextAvailableCode(apacAccess.deviceId);
        
        if (code == null) {
            throw new ErrorException(104);
        }
        
        apacAccess.code = code.code;
        apacAccess.slot = code.slot;
        saveObject(apacAccess);

        doorManager.claimUsage(apacAccess.deviceId, code, "ApacManager");

        accesslist.put(apacAccess.id, apacAccess);
        
        finalizeAllAccessList();
        
        return apacAccess;
    }

    @Override
    public List<ApacAccess> getAccessList() {
        finalizeAllAccessList();
        List retList = new ArrayList(accesslist.values());
        
        return retList;
    }

    private void finalizeAllAccessList() {
        for (ApacAccess access : accesslist.values()) {
            try {
                Door door = doorManager.getDoorByDeviceId(access.deviceId);
                if (door != null) {
                    access.door = door;
                }
            } catch (Exception ex) {
                Logger.getLogger(ApacManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void removeAccess(String accessId) {
        ApacAccess access = accesslist.remove(accessId);
        
        if (access != null) {
            doorManager.removeCode(access.deviceId, access.slot);
            deleteObject(access);
        }
    }
    
    @Override
    public ApacAccess getApacAccess(String accessId) {
        finalizeAllAccessList();
        return accesslist.get(accessId);
    }

    @Override
    public void sendSms(String accessId, String message) {
        ApacAccess access = getApacAccess(accessId);
        if (access != null) {
            messageManager.sendSms("nexmo", access.phoneNumber, message, access.prefix);
        }
    }

    @Override
    public List<Door> getAllDoors() throws Exception {
        HashMap<String, PmsLockServer> lockServers = pmsManager.getConfiguration().lockServerConfigs;
        List<Door> doors = doorManager.getAllDoors();
        doors.removeIf(door -> !lockServers.containsKey(door.serverSource) && !door.serverSource.isEmpty());
        return doors;
    }
}
