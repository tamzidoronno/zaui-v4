/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import javapns.Push;

/**
 *
 * @author ktonder
 */
public class AppleNotificationThread implements Runnable {
    private String message;
    private String tokenId;
    private int badge;

    public AppleNotificationThread(String message, String tokenId, int badge) {
        this.message = message;
        this.tokenId = tokenId;
        this.badge = badge;
    }
    
    
    @Override
    public void run() {
        try {
            Push.combined(message, this.badge, "alert", "GetShop.p12", "auto1000", false, tokenId);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
