/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import com.thundashop.core.common.FrameworkConfig;
import com.thundashop.core.mobilemanager.data.MobileApp;
import javapns.Push;
import javapns.notification.PushedNotifications;

/**
 *
 * @author ktonder
 */
public class AppleNotificationThread implements Runnable {
    private final String message;
    private final String tokenId;
    private final int badge;
    private final boolean production;
    private final MobileApp mobileApp;

    public AppleNotificationThread(String message, String tokenId, int badge, FrameworkConfig frameworkConfig, MobileApp mobileApp) {
        this.message = message;
        this.tokenId = tokenId;
        this.production = frameworkConfig.productionMode;
        this.badge = badge;
        this.mobileApp = mobileApp;
    }
    
    @Override
    public void run() {
        try {
            if (production) {
                Push.combined(message, this.badge, "alert", "/opt/getshop/certs/"+mobileApp.id+"_ios_prod.p12", mobileApp.iosPassword, true, tokenId);
            } else {
                Push.combined(message, this.badge, "alert", "/opt/getshop/certs/"+mobileApp.id+"_ios_dev.p12", mobileApp.iosPassword, false, tokenId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }   
}