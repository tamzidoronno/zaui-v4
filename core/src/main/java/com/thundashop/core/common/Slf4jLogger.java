/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Slf4jLogger implements com.thundashop.core.common.Logger {
    
    @Override
    public void debug(Object from, String message) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(from.getClass());
        logger.debug(message);
    }
    
    @Override
    public void info(Object from, String message) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(from.getClass());
        logger.info(message);
    }
    
    @Override
    public void warning(Object from, String message) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(from.getClass());
        logger.warn(message);
    }
    
    @Override
    public void error(Object from, String message) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(from.getClass());
        logger.error(message);
    }

    @Override
    public void error(Object from, String message, Throwable ex) {
        org.slf4j.Logger logger = LoggerFactory.getLogger(from.getClass());
        logger.error(message, ex);
    }

}
