package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ArxAccessCommunicator extends ManagerBase {
    public List<HotelBookingManager> managers = new CopyOnWriteArrayList();
    
     @Scheduled(fixedRate=3000)
    public synchronized void scheduledUsersToSend() throws ErrorException, UnsupportedEncodingException {
        for(HotelBookingManager manager : managers) {
            manager.checkForArxUpdate();
        }
    }
    
    public void addManager(HotelBookingManager manager) {
        managers.add(manager);
    }
    
}
