/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.databasemanager.DatabaseSocketHandler;
import com.thundashop.core.databasemanager.data.Credentials;
import java.io.Serializable;

/**
 *
 * @author ktonder
 */
public class ASyncHandler implements Serializable {
    private final DataCommon data;
    private final Credentials credentials;
    
    public ASyncHandler(DataCommon data, Credentials credentials) {
        this.data = data;
        this.credentials = credentials;
    }
    
    public void handleMessage(final DatabaseSocketHandler socketHandler) {
        final ASyncHandler me = this;
        new Thread(new Runnable() {
            @Override
            public void run() {
                socketHandler.handleMessage(data, credentials, me);
            }
        }).start();
    }
    
}
