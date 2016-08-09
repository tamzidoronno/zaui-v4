
package com.thundashop.core.common;

import java.util.Date;

public class GetShopLogHandler {
    
    
    public static void logPrintStatic(Object key, String storeId) {
        if(storeId == null) {
            System.out.println(new Date() + " : " + key);
        } else {
            System.out.println(storeId + " : " + new Date() + " : " + key);
        }
    }

    public static void logPrintStaticSingle(Object key, String storeId) {
        System.out.print(key);
    }
}
