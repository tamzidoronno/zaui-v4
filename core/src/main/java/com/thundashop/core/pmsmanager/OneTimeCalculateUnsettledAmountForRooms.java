/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.ManagerSubBase;
import com.thundashop.core.common.OneTimeExecutor;

/**
 *
 * @author ktonder
 */
public class OneTimeCalculateUnsettledAmountForRooms extends OneTimeExecutor {

    public OneTimeCalculateUnsettledAmountForRooms(ManagerSubBase base) {
        super(base);
    }

    @Override
    public void run() {
        PmsManager manager = (PmsManager)getManager();
        manager.getAllBookingsFlat()
            .stream()
            .forEach(booking -> {
                manager.calculateUnsettledAmountForRooms(booking);
                manager.saveObjectDirect(booking);
            });
    }    
}