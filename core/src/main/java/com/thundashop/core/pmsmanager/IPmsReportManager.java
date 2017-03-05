
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import java.util.Date;
import java.util.List;

/**
 * Pms report manager.
 */
@GetShopApi
@GetShopMultiLayerSession
public interface IPmsReportManager {
    
    @Administrator
    public List<PmsMobileReport> getReport(Date start, Date end, String compareTo);
    
    @Administrator
    public List<PmsMobileRoomCoverage> getRoomCoverage(Date start, Date end);
    
    @Administrator
    public List<PmsMobileUsage> getUsage(Date start, Date end);
    
    @Administrator
    public PmsConferenceStatistics getConferenceStatistics(Date start, Date end);
}
