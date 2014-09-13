package com.thundashop.core.hotelbookingmanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.messagemanager.NewsLetterManager;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ArxAccessCommunicator extends ManagerBase {
    public List<HotelBookingManager> managers = new CopyOnWriteArrayList();

    @Autowired
    public ArxAccessCommunicator(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
    }
    
     @Scheduled(fixedRate=3000)
    public synchronized void scheduledUsersToSend() throws ErrorException, UnsupportedEncodingException {
        for(HotelBookingManager manager : managers) {
            try {
                manager.checkForArxUpdate();
            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void addManager(HotelBookingManager manager) {
        managers.add(manager);
    }
    
}
