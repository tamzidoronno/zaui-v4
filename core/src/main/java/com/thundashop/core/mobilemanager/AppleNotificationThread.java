/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.mobilemanager;

import com.thundashop.core.common.FrameworkConfig;
import javapns.Push;

/**
 *
 * @author ktonder
 */
public class AppleNotificationThread implements Runnable {
    private final String message;
    private final String tokenId;
    private final int badge;
    private final String certificate;
    private final String password;
    private final boolean production;

    public AppleNotificationThread(String message, String tokenId, int badge, String certificate, String password, FrameworkConfig frameworkConfig) {
        this.message = message;
        this.tokenId = tokenId;
        this.production = frameworkConfig.productionMode;
        this.badge = badge;
        this.certificate = certificate;
        this.password = password;
    }
    
    
    @Override
    public void run() {
        try {
            if (production) {
                Push.combined(message, this.badge, "alert", "certs/"+certificate+"_prod.p12", password, true, tokenId);
            } else {
                Push.combined(message, this.badge, "alert", "certs/"+certificate+"_dev.p12", password, false, tokenId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
