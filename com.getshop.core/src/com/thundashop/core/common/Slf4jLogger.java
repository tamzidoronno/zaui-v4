/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class Slf4jLogger implements com.thundashop.core.common.Logger {
    
    @Override
    public void debug(Object from, String message) {
        Logger logger = Logger.getLogger(from.getClass());
        logger.debug(message);
    }
    
    @Override
    public void info(Object from, String message) {
        Logger logger = Logger.getLogger(from.getClass());
        logger.info(message);
    }
    
    @Override
    public void warning(Object from, String message) {
        Logger logger = Logger.getLogger(from.getClass());
        logger.warn(message);
    }
    
    @Override
    public void error(Object from, String message) {
        Logger logger = Logger.getLogger(from.getClass());
        logger.error(message);
    }

    @Override
    public void error(Object from, String message, Throwable ex) {
        Logger logger = Logger.getLogger(from.getClass());
        logger.error(message, ex);
    }
    

    
}
