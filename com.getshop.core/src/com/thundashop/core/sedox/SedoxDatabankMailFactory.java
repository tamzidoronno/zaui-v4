/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.sedox;

import com.thundashop.core.messagemanager.MailConfig;
import com.thundashop.core.messagemanager.MailSettings;


/**
 *
 * @author ktonder
 */
class SedoxDatabankMailConfig implements MailConfig {

    @Override
    public MailSettings getSettings() {
        MailSettings settings = new MailSettings();
        settings.username = "databank@tuningfiles.com";
        settings.password = "GT92Support";
        return settings;
    }

    @Override
    public void setup(String storeId) {
        
    }


    
}
