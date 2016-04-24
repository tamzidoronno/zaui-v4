/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.thundashop.core.usermanager.data.User;

/**
 *
 * @author ktonder
 */
public class SerializationExcludeStragety implements ExclusionStrategy {
    private final User user;

    public SerializationExcludeStragety(User user) {
        this.user = user;
    }

    
    @Override
    public boolean shouldSkipField(FieldAttributes fa) {
        if (fa.getAnnotation(Administrator.class) != null) {
            if (user == null || user.type < 100) {
                return true;
            }
        }
        
        if (fa.getAnnotation(Editor.class) != null) {
            if (user == null || user.type < 50) {
                return true;
            }
        }
        
        if (fa.getAnnotation(Customer.class) != null) {
            if (user == null || user.type < 10) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        
        return false;
    }
    
}
