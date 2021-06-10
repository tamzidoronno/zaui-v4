
package com.thundashop.core.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GetShopLogHandler {
    public static boolean isDeveloper = false;
    
    public static List<String> started = new ArrayList<>();
    public static List<String> authenticationError = new ArrayList<>();

    public static final Logger log = LoggerFactory.getLogger(GetShopLogHandler.class);

    public static void logPrintStatic(Object key, String storeId) {
        log.info("storeId `{}` {}", storeId, key);
    }

    public static void logPrintStaticSingle(Object key, String storeId) {
        logPrintStatic(key, storeId);
    }

    public static void logStack(Exception ex, String storeId) {
        log.error("Error for storeId `{}`", storeId, ex);
    }

 

}
