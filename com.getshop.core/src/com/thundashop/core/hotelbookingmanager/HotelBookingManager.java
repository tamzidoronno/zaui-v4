package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    public Map<String, HotelDomainController> domains = new HashMap();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof Domain) {
                Domain domain = (Domain)dataCommon;
                domains.put(domain.id, createHotelBookingManager(domain));
            }
        }
        
        for (HotelDomainController domainController : domains.values()) {
            domainController.dataFromDatabase(data.data);
        }
    }   

    @Override
    public void createDomain(String name, String description) {
        Domain domain = new Domain();
        domain.name = name;
        domain.description = description;
        saveObject(domain);
        domains.put(domain.id, createHotelBookingManager(domain));
    }

    @Override
    public ArrayList<Domain> getDomains() {
        return new ArrayList(domains.keySet());
    }

    @Override
    public RoomType createRoomType(String domainId, String name, double price, int size) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) {
            return domainController.createRoom(name, price, size);
        }
        
        return null;
    }


    private HotelDomainController createHotelBookingManager(Domain domain) {
        HotelDomainController controller = new HotelDomainController(domain, this);
        return controller;
    }
}