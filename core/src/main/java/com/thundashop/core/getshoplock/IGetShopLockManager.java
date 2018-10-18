package com.thundashop.core.getshoplock;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.ForceAsync;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
import com.thundashop.core.pmsmanager.PmsBookingRooms;
import java.util.Date;
import java.util.List;

/**
 *
 * Communicating with the getshop lock.
 * @author boggi
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IGetShopLockManager {
    
    public void accessEvent(String id, String code, String domain) throws Exception;
    
    @Administrator
    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception;
    
    @Administrator
    public void finalizeLocks();
    
    @Administrator
    public void removeAllUnusedLocks(String source) throws Exception;
    
    @Administrator
    public String getCodeForLock(String lockId);
    
    @Administrator
    public void changeZWaveId(String lockId, Integer newId);
   
    @Administrator
    public List<GetShopDevice> getAllLocks(String serverSource);
    
    @Administrator
    public GetShopDevice getDevice(String deviceId);
    
    @Administrator
    public void saveLock(GetShopDevice lock);
    
    @Administrator
    public void deleteAllDevices(String password, String source);
    
    @Administrator
    public void openLock(String lockId);
    
    @Administrator
    public boolean pingLock(String lockId);
    
    @Administrator
    public void removeCodeOnLock(String lockId, PmsBookingRooms room);
    
    @Administrator
    public void checkIfAllIsOk();
    
    @Administrator
    public void deleteLock(String code, String lockId);
    
    @Administrator
    public void setMasterCode(Integer slot, String code);
    
    @Administrator
    public void refreshLock(String lockId);
    
    @Administrator
    public void refreshAllLocks(String source);
    
    @Administrator
    public GetShopLockMasterCodes getMasterCodes();
    
    @Administrator
    public void saveMastercodes(GetShopLockMasterCodes codes);
    
    @Administrator
    public List<String> getCodesInUse();
    
    @Administrator
    public void stopUpdatesOnLock();
    
    @Administrator
    public boolean getUpdatesOnLock();
    
    public void triggerFetchingOfCodes(String ip, String deviceId);
    
    public void addLockLogs(List<GetShopDeviceLog> logs, String code);
    
    @Administrator
    public void triggerMassUpdateOfLockLogs();
}
