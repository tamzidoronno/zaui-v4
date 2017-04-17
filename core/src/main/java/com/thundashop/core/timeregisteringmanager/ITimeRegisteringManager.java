
package com.thundashop.core.timeregisteringmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;
import java.util.List;

/**
 * Time registering management system.<br>
 */

@GetShopApi
public interface ITimeRegisteringManager {
    public void registerTime(Date start, Date end, String comment);
    public void deleteTimeUnsecure(String id);
    public List<TimeRegistered> getMyHours();
    
    @Administrator
    public List<TimeRegistered> getAllTimesRegistered();
    
    @Administrator
    public List<TimeRegistered> getRegisteredHoursForUser(String userId, Date start, Date end);
}
