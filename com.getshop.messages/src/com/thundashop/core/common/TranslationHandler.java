
package com.thundashop.core.common;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class TranslationHandler implements Serializable {
    private HashMap<String, String> translationStrings = new HashMap<>();
    private HashMap<String, Map> translationMaps = new HashMap<>();
    
    public void updateTranslation(String language, String mainlanguage, boolean loading) {
        Field[] fields;
        fields = getClass().getFields();
        for(Field field : fields) {
            if(field.getAnnotation(Translation.class) != null) {
                try {
                    Object result = field.get(this);
                    if(result instanceof String) {
                        String keyindex = language+"_"+field.getName();
                        if(!translationStrings.containsKey(mainlanguage+"_"+field.getName())) {
                            translationStrings.put(mainlanguage+"_"+field.getName(), (String) result);
                        }
                        if(!loading) {
                            translationStrings.put(keyindex, (String) result);
                        } else {
                            if(translationStrings.containsKey(keyindex))
                                field.set(this, translationStrings.get(keyindex));
                        }
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
                
            }
        }
    }
    
}
