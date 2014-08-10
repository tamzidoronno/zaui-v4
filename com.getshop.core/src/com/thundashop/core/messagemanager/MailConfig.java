/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.messagemanager;

/**
 *
 * @author ktonder
 */
public interface MailConfig {
    MailSettings getSettings();
    void setup(String storeId);
}
