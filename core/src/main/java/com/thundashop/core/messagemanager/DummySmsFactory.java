/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

import com.thundashop.core.common.GetShopLogHandler;
import com.thundashop.core.common.ManagerSubBase;

/**
 *
 * @author ktonder
 */
public class DummySmsFactory implements SMSFactory {

    @Override
    public void send(String from, String to, String message) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
    }

    @Override
    public int messageCount(int year, int month) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
        return 0;
    }

    @Override
    public void setStoreId(String storeId) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
    }

    @Override
    public void setMessageManager(MessageManager manager) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
    }

    @Override
    public void setPrefix(String prefix) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
    }

    @Override
    public String getMessageState(String msgId) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
        return "";
    }

    @Override
    public void setFrom(String from) {
        GetShopLogHandler.logPrintStatic("running on dummy sms factory", null);
    }
    
}
