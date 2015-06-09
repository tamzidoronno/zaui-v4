package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class HotelBookingManager extends ManagerBase implements IHotelBookingManager {

    public List<Domain> domains = new ArrayList();
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon dataCommon : data.data) {
            if (dataCommon instanceof Domain) {
                domains.add((Domain)dataCommon);
            }
        }
    }   

    @Override
    public void createDomain(String name, String description) {
        Domain domain = new Domain();
        domain.name = name;
        domain.description = description;
        saveObject(domain);
        domains.add(domain);
    }

    @Override
    public ArrayList<Domain> getDomains() {
        return new ArrayList(domains);
    }

    @Override
    public RoomType createRoomType(String domainId, String name, double price, int size) {
        Domain domain = getDomain(domainId);
        if (domain != null) {
//            return domain.createRoomType();
        }
        
        return null;
    }

    private Domain getDomain(String domainId) {
        return domains.stream().filter(o -> o.id.equals(domainId)).findFirst().orElse(null);
    }
}