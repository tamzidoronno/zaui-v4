package com.thundashop.core.getshoplock;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.getshop.data.GetShopDevice;
import com.thundashop.core.getshop.data.GetShopLockMasterCodes;
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
    
    @Administrator
    public String pushCode(String id, String door, String code, Date start, Date end) throws Exception;
    
    @Administrator
    public String getCodeForLock(String lockId);
    
    @Administrator
    public List<GetShopDevice> getAllLocks();
    
    @Administrator
    public void deleteAllDevices(String password);
    
    @Administrator
    public void openLock(String lockId);
    
    @Administrator
    public boolean pingLock(String lockId);
    
    @Administrator
    public void removeCodeOnLock(String lockId, String code);
    
    @Administrator
    public void checkIfAllIsOk();
    
    @Administrator
    public void deleteLock(String code, String lockId);
    
    @Administrator
    public void setMasterCode(Integer slot, String code);
    
    @Administrator
    public void refreshLock(String lockId);
    
    @Administrator
    public GetShopLockMasterCodes getMasterCodes();
    
    @Administrator
    public void saveMastercodes(GetShopLockMasterCodes codes);
    
}
