/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.google.gson.Gson;
import com.thundashop.core.common.DataCommon;

/**
 *
 * @author ktonder
 */
public class MailStatus extends DataCommon {
    String status;
    String exeptionMessage;
    String text;
    String messageType;
    
    public MailStatus clone() {
        Gson gson = new Gson();
        String json = gson.toJson(this);
        return gson.fromJson(json, MailStatus.class);
    }
}
