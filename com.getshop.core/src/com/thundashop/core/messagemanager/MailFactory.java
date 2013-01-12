/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

/**
 *
 * @author ktonder
 */
public interface MailFactory {
    public void send(String from, String to, String title, String content);
    public void setStoreId(String storeId);
}
