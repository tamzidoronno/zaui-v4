/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.dbbackupmanager;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.mongodb.morphia.annotations.Transient;

/**
 *
 * @author ktonder
 */
public class GsonExcludeTransientFields implements ExclusionStrategy{

    @Override
    public boolean shouldSkipField(FieldAttributes fa) {
        boolean shouldSkip = fa.getAnnotation(Transient.class) != null;
        return shouldSkip;
    }

    @Override
    public boolean shouldSkipClass(Class<?> type) {
        return false;
    }
    
}
