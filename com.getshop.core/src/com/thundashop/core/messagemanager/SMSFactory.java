/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

/**
 *
 * @author boggi
 */
public interface SMSFactory {
    public void send(String from, String to, String message);
    public int messageCount(int year, int month);

    public void setStoreId(String storeId);
    public void setMessageManager(MessageManager manager);
}
