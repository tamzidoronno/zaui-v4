/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.eventbooking;

import com.getshop.scope.GetShopSchedulerBase;

/**
 *
 * @author ktonder
 */
public class EventBookingScheduler extends GetShopSchedulerBase {

    public EventBookingScheduler(String webAddress, String username, String password, String scheduler) throws Exception {
        super(webAddress, username, password, scheduler);
    }

    @Override
    public void execute() throws Exception {
        System.out.println("OK: " + getApi().getUserManager().getLoggedOnUser());
    }

    
}