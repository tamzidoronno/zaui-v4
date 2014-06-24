
package com.thundashop.core.common;

import com.google.gson.Gson;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TranslationHandler implements Serializable {
    private HashMap<String, String> translationStrings = new HashMap<>();
    private HashMap<String, Map> translationMaps = new HashMap<>();
    private boolean debug = false;
    
    public void updateTranslation(String language, String mainlanguage, boolean loading) {
        Field[] fields;
        fields = getClass().getFields();
        for(Field field : fields) {
            if(field.getAnnotation(Translation.class) != null) {
                try {
                    Object result = field.get(this);
                    String toSave = null;
                    if(result instanceof String) {
                        toSave = (String) result;
                    } else {
                        toSave = new Gson().toJson(result);
                    }
                    
                    String keyindex = language+"_"+field.getName();
                    if(!translationStrings.containsKey(mainlanguage+"_"+field.getName())) {
                        if(debug)
                            System.out.println("Saving default translation field (" + this.getClass().getSimpleName() + "): " + keyindex + " to : " + toSave.replaceAll("\\s+",""));
                        translationStrings.put(mainlanguage+"_"+field.getName(), toSave);
                    }
                    if(!loading) {
                        if(debug)
                            System.out.println("Saving translation field (" + this.getClass().getSimpleName() + "): " + keyindex + " to : " + toSave.replaceAll("\\s+",""));
                        translationStrings.put(keyindex, toSave);
                    } else {
                        if(translationStrings.containsKey(keyindex)) {
                            String toSet = translationStrings.get(keyindex);
                            if(debug) {
                                System.out.println("Updating translation field (" + this.getClass().getSimpleName() + "): " + keyindex + " to : " + toSet.replaceAll("\\s+",""));
                            }
                            Object resultObject = null;
                            if(result instanceof String) {
                                resultObject = toSet;
                            } else {
                                resultObject = new Gson().fromJson(toSet, field.getType());
                            }
                            field.set(this, resultObject);
                        }
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
                
            }
        }
    }
    
}
