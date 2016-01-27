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
public class DummySmsFactory implements SMSFactory {

    @Override
    public void send(String from, String to, String message) {
        System.out.println("running on dummy sms factory");
    }

    @Override
    public int messageCount(int year, int month) {
        System.out.println("running on dummy sms factory");
        return 0;
    }

    @Override
    public void setStoreId(String storeId) {
        System.out.println("running on dummy sms factory");
    }

    @Override
    public void setMessageManager(MessageManager manager) {
        System.out.println("running on dummy sms factory");
    }

    @Override
    public void setPrefix(String prefix) {
        System.out.println("running on dummy sms factory");
    }

    @Override
    public String getMessageState(String msgId) {
        System.out.println("running on dummy sms factory");
        return "";
    }
    
}
