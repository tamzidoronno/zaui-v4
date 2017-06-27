/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.ordermanager;

import com.getshop.scope.GetShopSchedulerBase;
import java.util.Random;

/**
 *
 * @author boggi
 */

public class CheckOrderCollector extends GetShopSchedulerBase {

    public CheckOrderCollector(String webAddress, String username, String password, String scheduler, String multiLevelName) throws Exception {
        super(webAddress, username, password, scheduler, multiLevelName);
    }

    public CheckOrderCollector() {
    }
    
    @Override
    public void execute() throws Exception {
        getApi().getOrderManager().checkForOrdersToCapture("asfasdfuj2843ljsdflansfkjn432k5lqjnwlfkjnsdfklajhsdf2");
    }
}
