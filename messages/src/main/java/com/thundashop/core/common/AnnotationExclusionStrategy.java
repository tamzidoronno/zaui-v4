/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.thundashop.core.usermanager.data.User;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ktonder
 */
public class AnnotationExclusionStrategy implements ExclusionStrategy {

    private final User user;

    public AnnotationExclusionStrategy(User user) {
        this.user = user;
    }
    
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        if (f != null && f.getAnnotation(Internal.class) != null) {
            return true;
        }
        
        if (f != null && f.getAnnotation(Administrator.class) != null) {
            if (user == null || user.type < 100) {
                return true;
            }
        }
        
        if (f != null && f.getAnnotation(Editor.class) != null) {
            if (user == null || user.type < 50) {
                return true;
            }
        }
        
        if (f != null && f.getAnnotation(Customer.class) != null) {
            if (user == null || user.type < 10) {
                return true;
            }
        }
        
        if(user != null && !user.annotionsAdded.isEmpty()) {
            for(String annotation : user.annotionsAdded) {
                try {
                    Class cls = Class.forName("com.thundashop.core.annotations." + annotation);
                    if(annotation.startsWith("Exclude")) {
                        if (f.getAnnotation(cls) != null) {
                            return true;
                        }
                    } else {
                        if (f.getAnnotation(cls) != null) {
                            return false;
                        }
                    }
                }catch(Exception e) {
                    return false;
                }
            }
        }
        return f.getAnnotation(ExcludeFromJson.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}