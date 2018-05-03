/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.pmsmanager;

import com.thundashop.core.common.SmartDataMap;
import com.thundashop.core.databasemanager.Database;

/**
 *
 * @author ktonder
 */
public class PmsBookingMap extends SmartDataMap<String, PmsBooking, PmsBookingLight> {

    public PmsBookingMap(Database database, String storeId, Class manager) {
        super(database, storeId, manager, PmsBooking.class);
    }

    @Override
    public int getVersionNumber() {
        return 5;
    }
    
}
