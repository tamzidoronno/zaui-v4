/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.databasemanager;

import com.thundashop.core.common.AppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class Hookup {
    @Autowired
    public DatabaseSocketHandler databaseSocketHandler;
    private String remoteAddress;
    private String localAddress;
    
    public void start(String args[]) {
        init(args);
        databaseSocketHandler.hookup(localAddress, remoteAddress);
    }
    
    private void init(String args[]) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
        }
        
        if (args.length == 1) {
            System.out.println("Local address saved");
            databaseSocketHandler.startListener(args[1]);
            System.exit(0);
        }
        
        if (args.length == 2) {
            localAddress = args[0];
            remoteAddress = args[1];
        }
    }
    
    private void printUsage() {
        System.out.println("===================================================================================================");
        System.out.println("Usage:");
        System.out.println("java -Xmx2048m "+this.getClass().getCanonicalName()+" localAddress serverToHookUpWith");
        System.out.println("===================================================================================================");
    }
    
    public static void main(String args[]) {
        args = new String[2];
        args[0] = "10.0.0.130";
        args[1] = "10.0.0.123";
        
        ApplicationContext context = new ClassPathXmlApplicationContext("All.xml");
        AppContext.appContext = context;
        context.getBean(DatabaseSocketHandler.class).startListener(args[0]);
        Hookup handler = context.getBean(Hookup.class);
        handler.start(args);
    }
}
