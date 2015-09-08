/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

public interface Logger {
    public void debug(Object from, String message);
    public void warning(Object from, String message);
    public void error(Object from, String message);
    public void info(Object from, String message);
    public void error(Object from, String message, Throwable ex);
}
