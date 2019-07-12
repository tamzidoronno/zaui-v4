/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import java.lang.reflect.Type;

/**
 *
 * @author ktonder
 */
public class StringInstanceCreator implements JsonDeserializer<String> {


    @Override
    public String deserialize(JsonElement json, Type type, JsonDeserializationContext jdc) throws JsonParseException {
//        
        JsonPrimitive jobject = (JsonPrimitive) json;

        String retString = jobject.getAsString();
        
        return retString;
    }
    
}
