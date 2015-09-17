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
    public String send(String from, String to, String message);
    public int messageCount(int year, int month);
}
