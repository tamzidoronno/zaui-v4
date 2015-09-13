/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.bookingengine;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.ManagerBase;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
@GetShopSession
public class BookingEngineHolder extends ManagerBase {
    Map<String, BookingEngine> engines = new HashMap();
    
    @Autowired
    private ApplicationContext applicationContext;
    
    
    public BookingEngine getBookingEngine(String name) {
        if (engines.get(name) != null) {
            return engines.get(name);
        }
        
        BookingEngine engine = (BookingEngine) applicationContext.getBean("bookingEngine", name);
//        proxyBean.setTargetBeanName("bookingEngine");
//        BookingEngine engine = (BookingEngine)proxyBean.getObject();
        System.out.println("Got one: " + engine.getName());
        engine.initialize();
        
        engines.put(name, engine);
        return engine;
        
//        return null;
    }
    
}
