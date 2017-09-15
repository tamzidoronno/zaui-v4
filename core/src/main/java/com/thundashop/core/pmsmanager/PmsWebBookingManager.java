package com.thundashop.core.pmsmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.wubook.webbookingmanager.PmsWebRoom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@GetShopSession
public class PmsWebBookingManager extends GetShopSessionBeanNamed implements IPmsWebBookingManager {

    @Autowired
    PmsManager pmsManager;
    
    @Override
    public List<PmsWebRoom> getAllRooms(Date start, Date end) {
        List<PmsWebRoom> rooms = new ArrayList();
        
        String[] defaultStart = pmsManager.getConfigurationSecure().defaultStart.split(":");
        String[] defaultEnd = pmsManager.getConfigurationSecure().defaultEnd.split(":");
        
        Calendar calStart = Calendar.getInstance();
        calStart.setTime(start);
        calStart.set(Calendar.HOUR_OF_DAY, new Integer(defaultStart[0]));
        calStart.set(Calendar.MINUTE, new Integer(defaultStart[1]));
        start = calStart.getTime();
        
        Calendar calEnd = Calendar.getInstance();
        calEnd.setTime(end);
        calEnd.set(Calendar.HOUR_OF_DAY, new Integer(defaultEnd[0]));
        calEnd.set(Calendar.MINUTE, new Integer(defaultEnd[1]));
        end = calEnd.getTime();
        
        List<PmsBookingRooms> pmsrooms = pmsManager.getAllRoomTypes(start, end);
        for(PmsBookingRooms r : pmsrooms) {
            PmsWebRoom toAdd = new PmsWebRoom();
            toAdd.price = r.price;
            toAdd.availableRooms = pmsManager.getNumberOfAvailable(r.type.id, start, end);
            toAdd.roomId = r.type.id;
            toAdd.name = r.type.name;
            rooms.add(toAdd);
        }
        
        return rooms;
    }
    
}
