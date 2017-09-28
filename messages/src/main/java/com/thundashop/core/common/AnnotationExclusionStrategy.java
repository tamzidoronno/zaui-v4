/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

/**
 *
 * @author ktonder
 */
public class AnnotationExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(ExcludeFromJson.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}