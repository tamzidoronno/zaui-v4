
package com.thundashop.core.common;

import java.io.Serializable;
import java.util.HashMap;

public class TranslationHandler implements Serializable {
    private HashMap<String, HashMap<String, Object>> translations = new HashMap();
    
    public void addTranslation(String language, String field, Object value) {
        System.out.println("Translationhandler is working");
        if(!translations.containsKey(language)) {
            translations.put(language, new HashMap());
        }
        translations.get(language).put(field, value);
    }
}
