/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thundashop.core.databasemanager;

import com.thundashop.core.common.DataCommon;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class DatabaseRemoteCache {
    private HashMap<String, List<DataCommon>> cached = new HashMap();
    
    List<DataCommon> get(String key) {
        return cached.get(key);
    }

    void put(String key, List<DataCommon> collect) {
        cached.put(key, collect);
    }   
}