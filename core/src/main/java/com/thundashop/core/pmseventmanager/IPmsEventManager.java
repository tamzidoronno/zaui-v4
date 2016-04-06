package com.thundashop.core.pmseventmanager;

import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.common.GetShopMultiLayerSession;
import com.thundashop.core.pmseventmanager.PmsBookingEventEntry;
import com.thundashop.core.pmseventmanager.PmsEventFilter;
import java.util.List;

/**
 * Event calendar management.<br>
 */

@GetShopApi
@GetShopMultiLayerSession
public interface IPmsEventManager {
    public List<PmsBookingEventEntry> getEventEntries(PmsEventFilter filter);
    public void saveEntry(PmsBookingEventEntry entry, String day);
    @Administrator
    public void deleteEntry(String entryId, String day);
    @Administrator
    public PmsBookingEventEntry createEvent(String id);
            
    public PmsBookingEventEntry getEntry(String entryId, String day);
    public PmsBookingEventEntry getEntryShort(String shortId, String day);
}
