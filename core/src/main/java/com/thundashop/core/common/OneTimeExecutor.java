/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

/**
 *
 * @author ktonder
 */
public abstract class OneTimeExecutor {

    private final ManagerSubBase manager;
    
    public OneTimeExecutor(ManagerSubBase base) {
        this.manager = base;
    }
    
    public abstract void run();
    
    public ManagerSubBase getManager() {
        return this.manager;
    }
    
    /**
     * Handy to use while developing, set this to true in your class,
     * When debug completed, set it to false to ensure
     * that the script only run once on the server.
     * 
     * @return 
     */
    public boolean forceExecute() {
        return false;
    }
}
