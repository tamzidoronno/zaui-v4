package com.thundashop.core.hotelbookingmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        ArrayList<Domain> dom = new ArrayList();
        for (HotelDomainController controller : domains.values()) {
            dom.add(controller.getDomain());
        }
        
        return dom;
    }

    @Override
    public RoomType createRoomType(String domainId, String name, double price, int size) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) {
            return domainController.createRoomType(name, price, size);
        }
        
        return null;
    }

    
    private HotelDomainController createHotelBookingManager(Domain domain) {
        HotelDomainController controller = new HotelDomainController(domain, this);
        return controller;
    }

    @Override
    public List<RoomType> getRoomTypes(String domainId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) {
            return domainController.getRoomTypes();
        }
        
        return new ArrayList();
    }

    @Override
    public Domain getDomain(String domainId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) 
            return domainController.getDomain();
        
        return null;
    }

    @Override
    public void deleteRoomType(String domainId, String roomTypeId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) 
            domainController.deleteRoomType(roomTypeId);
    }

    @Override
    public RoomType getRoomType(String domainId, String roomTypeId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) 
            return domainController.getRoomType(roomTypeId);
        
        return null;
    }

    @Override
    public void saveRoom(Room room) {
        if (room == null || room.domainId == null)
            return;
        
        HotelDomainController domainController = domains.get(room.domainId);
        if (domainController != null)
            domainController.saveRoom(room);
    }

    @Override
    public List<Room> getRooms(String domainId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null)
            return domainController.getRooms();
        
        return null;
    }

    @Override
    public Room getRoom(String domainId, String roomId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) 
            return domainController.getRoom(roomId);
            
        return null;
    }

    @Override
    public void deleteRoom(String domainId, String roomId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) 
            domainController.deleteRoom(roomId);
    }

    @Override
    public void deleteDomain(String domainId) {
        HotelDomainController domainController = domains.get(domainId);
        if (domainController != null) {
            Domain domain = domainController.getDomain();
            domains.remove(domainId);
            deleteObject(domain);
        }
            
    }
}