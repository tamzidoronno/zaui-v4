/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.thundashop.core.socket.CacheManager;
import org.springframework.context.ApplicationContext;

/**
 *
 * @author ktonder
 */
public class AppContext {
    public static ApplicationContext appContext;
    public static StorePool storePool;
    public static CacheManager cacheManager;
}
