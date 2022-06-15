package com.thundashop.core.utils;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @param <K>
 * @param <V>
 */
public class NullSafeConcurrentHashMap<K, V> extends ConcurrentHashMap<K, V> {

    /*
    * @Link ConcurrentHashMap throws NPE for null key or null value. Here it omits any action if Key or value is NULL. For value null, to remove existed value we are removing that key!
    * */
    @Override
    public V put(K var1, V var2) {
        if(var1 == null) return null;
        if(var2 == null) {
            /*To invalidate the existed key, will remove that key so that if any operation to get with that key will return NULL*/
            super.remove(var1);
            return null;
        }
        return super.put(var1, var2);
    }

    /*
    * Avoiding NPE
    */
    @Override
    public V get(Object var1) {
        if(var1 == null) return null;
        return super.get(var1);
    }

    /*
     * Avoiding NPE
     */
    @Override
    public V remove(Object var1) {
        if(var1 == null) return null;
        return super.remove(var1);
    }
}
