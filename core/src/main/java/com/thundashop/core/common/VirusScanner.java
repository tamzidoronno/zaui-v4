/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

/**
 *
 * @author ktonder
 */
public class VirusScanner implements JsonDeserializer<String> {

    private final AntiSamy antiySamy;
    private Policy policy;

    public VirusScanner() {
        this.antiySamy = new AntiSamy();
         
        try {
             policy = Policy.getInstance(getClass().getResource("/antisamy-myspace-1.4.4.xml"));
        } catch (Exception ex) {
            GetShopLogHandler.logPrintStatic("Could not find the antisamy policy file, can not continue unsecurly", null);
            ex.printStackTrace();
            System.exit(1);
        }
    }
    
    
    @Override
    public String deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {
        CleanResults result2;
        try {
            result2 = antiySamy.scan(je.getAsString(), policy);
            return result2.getCleanHTML();
        } catch (ScanException ex) {
            ex.printStackTrace();
        } catch (PolicyException ex) {
            ex.printStackTrace();
        }
        
        return "";
    }
    
}