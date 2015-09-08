/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.common;

import com.getshop.scope.GetShopSession;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Figure out how this should work.
 * 
 * @author ktonder
 */
@Component
@GetShopSession
public class ManagerHandler {
    
    
    /**
     * @param object
     * @param loading If you are loading the object, or if you are saving it.
     * @throws ErrorException 
     */
    public void updateTranslation(Object object, boolean loading) throws ErrorException {
//        HashMap<String, Setting> settings = getSettings("Settings");
//        if(settings != null && settings.containsKey("languages")) {
//            Gson sgon = new Gson();
//            Setting langsetting = settings.get("languages");
//            List<String> langcodes = sgon.fromJson(langsetting.value, ArrayList.class);
//            
//            if (langcodes == null) {
//                return;
//            }
//            
//            if(langcodes.size() > 0) {
//                TranslationHelper helper = new TranslationHelper(getSession().language, getMainLanguage());
//                helper.checkFields(object, loading);
//            }
//        }
    }
    
    
    public String getMainLanguage() throws ErrorException {
        return null;
//        String standardlang = "en_en";
//        HashMap<String, Setting> settings = getSettings("Settings");
//        if (settings.containsKey("language")) {
//            standardlang = settings.get("language").value;
//        }
//
//        return standardlang;
    }
}
