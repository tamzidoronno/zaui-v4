/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.getshop.scope;

import com.getshop.bookingengine.BookingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 *
 * @author ktonder
 */
@Configuration
public class BeanConfiguration {
    
//    @Autowired
//    private ApplicationContext appContext;
//    
//    @Bean
//    @Scope("getshop")
//    public GetShopSessionBeanNamed getBookingBean(String name) {
        
//        BookingEngine bookingEngine = applicationContext.getBean(BookingEngine.class);
//        bookingEngine.setName(name);
//        System.out.println("created with name: " + name);
//        return new GetShopSessionBeanNamed(name);
//    }
}
