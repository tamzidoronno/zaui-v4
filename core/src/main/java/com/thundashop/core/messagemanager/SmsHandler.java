/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

/**
 *
 * @author ktonder
 */
public interface SmsHandler {
    public String getName();
    public void sendMessage();
    public String getMessageId();
}
