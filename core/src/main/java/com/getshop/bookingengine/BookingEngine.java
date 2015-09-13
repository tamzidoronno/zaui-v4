/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.getshop.scope.GetShopSessionBeanNamed;
import com.thundashop.core.databasemanager.data.DataRetreived;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@Scope("getshop")
public class BookingEngine extends GetShopSessionBeanNamed {


    public BookingEngine(String name) {
        super(name);
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        
    }

    @Override
    public void initialize() {
        super.initialize(); //To change body of generated methods, choose Tools | Templates.
    }
    
}
