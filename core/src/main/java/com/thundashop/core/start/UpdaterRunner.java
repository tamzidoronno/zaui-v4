/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.start;

import com.thundashop.core.databasemanager.DatabaseUpdater;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 * @author ktonder
 */
public class UpdaterRunner {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        DatabaseUpdater updater = context.getBean(DatabaseUpdater.class);
        updater.check(context);
    }
}
