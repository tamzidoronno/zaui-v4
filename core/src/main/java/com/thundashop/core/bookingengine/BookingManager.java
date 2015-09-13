/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.bookingengine;

import com.getshop.scope.GetShopSession;
import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.usermanager.data.User;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BookingManager extends GetShopSessionBeanNamed implements IBookingManager {

    @Override
    public void dataFromDatabase(DataRetreived data) {
        System.out.println("Found data: " + getName() + " Size: " + data.data.size());
    }

    
    @Override
    public void doTest(String testMe) {
        System.out.println("Test succedded: " + testMe + " storeid: " + storeId + " session: " + getSession() + " name: " + getName());
    }

    @Override
    public void saveData(String fullName) {
        User user = new User();
        user.fullName = fullName;
        saveObject(user);
    }
    
}
