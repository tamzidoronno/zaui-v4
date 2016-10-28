/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 *
 * @author ktonder
 */
public class DoubleKeyMap<K1, K2, V> {

    private final Map<K1, HashMap<K2, V>> map1 = new HashMap<K1, HashMap<K2, V>>();

    public V get(K1 key1, K2 key2) {
        HashMap<K2, V> innerMap = map1.get(key1);
        if (innerMap == null) {
            return null;
        }

        return innerMap.get(key2);
    }

    public void put(K1 key1, K2 key2, V value) {
        HashMap<K2, V> innerMap = map1.get(key1);
        if (innerMap == null) {
            innerMap = new HashMap<K2, V>();
            map1.put(key1, innerMap);
        }

        innerMap.put(key2, value);
    }

    public Set<K1> keySet() {
        return map1.keySet();
    }
    
    public Set<K2> innerKeySet(K1 key) {
        HashMap<K2, V> innerMap = map1.get(key);
        if (innerMap == null) {
            return new TreeSet();
        }
        
        return innerMap.keySet();
    }

    public boolean keyExists(K1 k1, K2 k2) {
        if (map1.get(k1) != null) {
            return map1.get(k1).get(k2) != null;
        }
        
        return false;
    }
}
