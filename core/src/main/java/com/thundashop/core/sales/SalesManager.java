package com.thundashop.core.sales;

import com.getshop.scope.GetShopSession;
import com.ibm.icu.util.Calendar;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.reportingmanager.data.Report;
import com.thundashop.core.salesmanager.SalesCustomer;
import com.thundashop.core.salesmanager.SalesEvent;
import com.thundashop.core.usermanager.UserManager;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@GetShopSession
public class SalesManager extends ManagerBase implements ISalesManager {

    @Autowired
    UserManager userManager;
    
    public HashMap<String, SalesCustomer> customers = new HashMap();
    public HashMap<String, SalesEvent> events = new HashMap();
    
        @Override
    public void dataFromDatabase(DataRetreived data) {
        for(DataCommon obj : data.data) {
            if (obj instanceof SalesCustomer) {
                SalesCustomer cust = (SalesCustomer)obj;
                customers.put(cust.orgid, cust);
            }
            if (obj instanceof SalesEvent) {
                SalesEvent evt = (SalesEvent)obj;
                events.put(evt.id, evt);
            }
            
        }
    }

    
    
    @Override
    public SalesCustomer getCustomer(String orgId) {
        SalesCustomer theCustomer = customers.get(orgId);
        if(theCustomer != null) {
            return theCustomer;
        }
        
        return new SalesCustomer();
    }

    @Override
    public void saveCustomer(SalesCustomer customer) {
        SalesCustomer oldCustomer = getCustomer(customer.orgid);
        if(oldCustomer != null && oldCustomer.orgid != null) {
            oldCustomer.update(customer);
            customer = oldCustomer;
        }
        saveObject(customer);
        customers.put(customer.orgid, customer);
    }
    
    public SalesCustomer getLatestCustomer() {
        SalesCustomer latestCustomer = null;
        for(SalesCustomer cust : customers.values()) {
            if(latestCustomer == null || cust.rowCreatedDate.after(latestCustomer.rowCreatedDate)) {
                latestCustomer = cust;
            }
        }
        
        return latestCustomer;
    }

    @Override
    public SalesEvent getLatestEvent() {
         SalesEvent latestEvent = null;
        for(SalesEvent event : events.values()) {
            if(latestEvent == null || event.rowCreatedDate.after(latestEvent.rowCreatedDate)) {
                latestEvent = event;
            }
        }
        
        return latestEvent;
    }

    @Override
    public void saveEvent(SalesEvent event) {
        event.registeredBy = userManager.getLoggedOnUser().id;
        saveObject(event);
        events.put(event.id, event);
    }

    @Override
    public List<SalesEvent> getEventsForCustomer(String orgId) {
        LinkedList list = new LinkedList();
        for(SalesEvent evt : events.values()) {
            if(evt.orgId.equals(orgId)) {
                finalize(evt);
                list.add(evt);
            }
        }
        return list;
    }

    @Override
    public SalesEvent getEvent(String eventId) {
        return events.get(eventId);
    }

    private void finalize(SalesEvent evt) {
        evt.createdByName = userManager.getUserById(evt.registeredBy).fullName;
        evt.orgName = customers.get(evt.orgId).name;
    }

    @Override
    public List<SalesEvent> getEventsForDay(Long day) {
        List<SalesEvent> result = new LinkedList();
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(day*1000);
        for(SalesEvent evt : events.values()) {
            if(evt.date != null && evt.date > 0) {
                finalize(evt);
                Calendar evtcal = Calendar.getInstance();
                evtcal.setTimeInMillis(evt.date * 1000);
                if(evtcal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) && evtcal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                    result.add(evt);
                }
           }
        }
        
        return result;
    }

    @Override
    public List<SalesCustomer> findCustomer(String key, String type) {
        List<SalesCustomer> result = new LinkedList();
        for(SalesCustomer cust : customers.values()) {
            if(!type.equals("-1") && !cust.type.equals(type)) {
                continue;
            }
            if(cust.name != null && cust.name.toLowerCase().contains(key) || (cust.orgid != null && cust.orgid.toLowerCase().contains(key))) {
                result.add(cust);
            }
        }
        
        return result;
    }
    
}
