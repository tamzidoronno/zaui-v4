package com.thundashop.core.sales;

import com.thundashop.core.common.Editor;
import com.thundashop.core.common.GetShopApi;
import com.thundashop.core.salesmanager.SalesCustomer;
import com.thundashop.core.salesmanager.SalesEvent;
import java.util.List;

/**
 * Manager for handling sales for getshop.
 */
@GetShopApi
public interface ISalesManager {
    @Editor
    public SalesCustomer getCustomer(String orgId);

    @Editor
    public void saveCustomer(SalesCustomer customer);

    @Editor
    public SalesCustomer getLatestCustomer();

    @Editor
    public SalesEvent getLatestEvent();

    @Editor
    public void saveEvent(SalesEvent event);
    
    @Editor
    public List<SalesEvent> getEventsForCustomer(String orgId);
    
    @Editor
    public List<SalesEvent> getEventsForDay(Long day);
    
    @Editor
    public SalesEvent getEvent(String eventId);
    
    @Editor
    public List<SalesCustomer> findCustomer(String key, String type);
    
}
