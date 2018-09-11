/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.backupmanager;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionScope;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.pmsmanager.PmsBooking;
import com.thundashop.core.pmsmanager.PmsManager;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BackupManager extends ManagerBase implements IBackupManager {

    @Autowired
    private UserManager userManager;
    
    @Autowired
    private GetShopSessionScope sessionScope;
    
    @Override
    public void createBackup() {
        createBookings();
    }

    private void createBookings() {
        List<PmsManager> pmsManagers = sessionScope.getSessionNamedObjects()
                .stream()
                .filter(o -> o.getClass().isAssignableFrom(PmsManager.class))
                .map(o -> (PmsManager)o)
                .collect(Collectors.toList());
        
        
        for (PmsManager manager : pmsManagers) {
            BookingBackupExcel excel = new BookingBackupExcel("booking_"+manager.getName());
            
            for (PmsBooking booking : manager.getAllBookingsFlat()) {
                User user = userManager.getUserById(booking.userId);
            
                if (user == null)
                    continue;
                
                excel.addRow(user, booking);
            }
            
            excel.writeFile("/tmp/bookings_"+manager.getSessionBasedName()+".xlsx");
        }
        
    }
    
}
