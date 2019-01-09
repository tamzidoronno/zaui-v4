/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.gsd;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.common.UserQueueMessage;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * A GetShop Device Manager.
 * 
 * This unit handles the communication and security for the GetShop Devices,
 * At the implementation of this the different GetShop Devices is currently PGA, GetShop Locksystem and Access Points
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class GdsManager extends ManagerBase implements IGdsManager {
    public HashMap<String, GetShopDevice> devices = new HashMap();
    public ConcurrentHashMap<String, DeviceMessageQueue> messages = new ConcurrentHashMap();
    public ConcurrentHashMap<String, UserMessageQueue> userMessageQueue = new ConcurrentHashMap();
    
    @Override
    public void saveDevice(GetShopDevice device) {
        saveObject(device);
        devices.put(device.id, device);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon common : data.data) {
            if (common instanceof GetShopDevice) {
                devices.put(common.id, (GetShopDevice)common);
            }
            if (common instanceof DeviceMessageQueue) {
                messages.put(common.id, (DeviceMessageQueue)common);
            }
            if (common instanceof UserMessageQueue) {
                userMessageQueue.put(common.id, (UserMessageQueue)common);
            }
        }
    }

    
    @Override
    public void deleteDevice(String deviceId) {
        GetShopDevice device = devices.remove(deviceId);
        if (device != null) {
            deleteObject(device);
        }
        
        List<DeviceMessageQueue> deviceQueues = messages.values().stream()
                .filter(o -> o.deviceId.equals(deviceId))
                .collect(Collectors.toList());
        
        deviceQueues.forEach(q -> messages.remove(q.id));
        deviceQueues.forEach(q -> deleteObject(q));
    }

    @Override
    public List<GetShopDeviceMessage> getMessages(String tokenId) {
        GetShopDevice device = getDeviceByToken(tokenId);
        if (device == null)  {
            ArrayList list = new ArrayList();
            list.add(new GdsAccessDenied());
            return list;
        }
        
        device.lastPulledRequest = new Date();
        DeviceMessageQueue queue = getQueue(device.id);
        queue.markMessagesPulled();
        
        if (queue.isEmpty()) {
            for (int i=0; i<180; i++) {
                try { Thread.sleep(100); } catch (Exception ex) {};
                if (!queue.messages.isEmpty()) {
                    break;
                }
            }
        }
        
        if (queue.isEmpty()) {
            return new ArrayList();
        }
        
        List<GetShopDeviceMessage> retMessages= new ArrayList(queue.messages);
        queue.clear();
        
        saveObject(queue);
        
        return retMessages;
    }

    @Override
    public void sendMessageToDevice(String deviceId, GetShopDeviceMessage message) {
        DeviceMessageQueue queue = getQueue(deviceId);
        queue.messages.add(message);
        saveObject(queue);
    }
    
    private DeviceMessageQueue getQueue(String deviceId) {
        deleteNullQueues();
        
        DeviceMessageQueue queue = messages.values()
                .stream()
                .filter(o -> o.deviceId.equals(deviceId))
                .findFirst()
                .orElse(null);
        
        if (queue == null) {
            queue = createNewMessageQueue(deviceId);
            saveObject(queue);
            messages.put(queue.id, queue);
        }
        
        return queue;
    }

    private void deleteNullQueues() throws ErrorException {
        List<DeviceMessageQueue> nullQueus = messages.values()
                .stream()
                .filter(o -> o.deviceId == null)
                .collect(Collectors.toList());
        
        for (DeviceMessageQueue queue : nullQueus) {
            messages.remove(queue.id);
            deleteObject(queue);
        }
    }

    private DeviceMessageQueue createNewMessageQueue(String deviceId) {
        DeviceMessageQueue queue;
        queue = new DeviceMessageQueue();
        queue.deviceId = deviceId;
        return queue;
    }

    private GetShopDevice getDeviceByToken(String tokenId) {
        GetShopDevice device = devices.values().stream()
                .filter(d -> d.token.equals(tokenId))
                .findFirst()
                .orElse(null);
        
        return device;
    }

    @Override
    public List<DeviceMessageQueue> getQueues() {
        return new ArrayList(messages.values());
    }

    @Override
    public List<GetShopDevice> getDevices() {
        return new ArrayList(devices.values());
    }
    
    public void addUserMessageToQueue(String userId, Serializable dataCommon) {
        removeExpiredUserMessages();
        
        UserMessageQueue queue = getQueueForUser(userId);
        UserQueueMessage msg = new UserQueueMessage();
        msg.dataCommon = dataCommon;
        
        queue.messages.removeIf(o -> o.equals(dataCommon));
        queue.messages.add(msg);
    }

    @Override
    public List<Serializable> getMessageForUser() {
        removeExpiredUserMessages();
        
        String userId = getSession().currentUser.id;
        
        UserMessageQueue queue = getQueueForUser(userId);
        
        queue.markMessagesPulled();
        
        if (queue.isEmpty()) {
            for (int i=0; i<180; i++) {
                try { Thread.sleep(100); } catch (Exception ex) {};
                if (!queue.messages.isEmpty()) {
                    break;
                }
            }
        }
        
        if (queue.isEmpty()) {
            return new ArrayList();
        }
        
        List<Serializable> retMessages= new ArrayList(
                queue.messages
                        .stream()
                        .map(m -> m.dataCommon)
                        .collect(Collectors.toList())
        );
        
        queue.clear();
        
        saveObject(queue);
        
        return retMessages;
    }

    private UserMessageQueue getQueueForUser(String userId) {
        UserMessageQueue queue = userMessageQueue.values()
                .stream()
                .filter(q -> q.userId.equals(userId))
                .findFirst()
                .orElse(createQueueForUser(userId));
        return queue;
    }

    private UserMessageQueue createQueueForUser(String userId) {
        UserMessageQueue queue = new UserMessageQueue();
        queue.userId = userId;
        saveObject(queue);
        userMessageQueue.put(queue.id, queue);
        return queue;
    }

    private void removeExpiredUserMessages() {
        userMessageQueue.values()
                .forEach(queue -> {
                    queue.messages.removeIf(o -> o.hasExpired());
                });
    }
}
