
package com.thundashop.core.common;

import com.google.gson.Gson;
import com.google.gson.stream.MalformedJsonException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TranslationHandler implements Serializable {
    private HashMap<String, String> translationStrings = new HashMap<>();
    private String translationId = UUID.randomUUID().toString();
    
    public boolean updateTranslation(String language) {                
        boolean saved = false;
        try {
            saved = saveTranslationInternal(language, true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        return saved;
    }

    
    private Set<TranslationHandler> getAllTranslationHandlers(Object inObject) throws IllegalArgumentException, IllegalAccessException {
        Set<TranslationHandler> handlers = new HashSet();
        
        Field[] fields = null;
        
        if (inObject == null) {
            inObject = this;
            fields = this.getClass().getFields();
        } else {
            fields = inObject.getClass().getFields();
        }
        
        if (inObject instanceof TranslationHandler) {
            handlers.add((TranslationHandler)inObject);
        }
        
        for (Field field : fields) {
            Object dataObject = field.get(inObject);
            
            if (dataObject instanceof TranslationHandler) {
                handlers.add((TranslationHandler)dataObject);
                continue;
            }
            
            if (dataObject instanceof List) {
                List list = (List)dataObject;
                for (Object check : list) {
                    handlers.addAll(getAllTranslationHandlers(check));
                }
            }
        }
        
        return handlers;
    }
    
    private boolean saveTranslationInternal(String language, boolean set) throws IllegalArgumentException, IllegalAccessException {
        boolean saved = false;
        Set<TranslationHandler> handlers = getAllTranslationHandlers(null);
        for (TranslationHandler handler : handlers) {
            boolean handlerSaved = handler.update(language);
            if (handlerSaved) {
                saved = true;
            }
        }
        
        return saved;
    }

    private boolean update(String language) throws IllegalArgumentException, IllegalAccessException {
        Gson gson = new Gson();
        boolean changed = false;
        
        Field[] fields = getClass().getFields();
        for (Field field : fields) {
            if (field.getAnnotation(Translation.class) != null) {
                String keyindex = language+"_"+field.getName();
                
                String oldValue = "";
                if (field.getName().equals("name")) {
                    oldValue = (String) field.get(this);
                }
                
                
                String currentKeyLang = translationStrings.get("current_key_lang_"+field.getName());
                if (currentKeyLang == null) {
                    currentKeyLang = "";
                }
                
                boolean languageHasChanged = !currentKeyLang.equals(language);
                String oldValueInCurrentLanguage = translationStrings.get(keyindex);
                
                String newValueInCurrentLanguage = null;
                if (field.get(this) != null) {
                    newValueInCurrentLanguage = gson.toJson(field.get(this));
                } 
                
                if (languageHasChanged && oldValueInCurrentLanguage != null) {
                    try {
                        field.set(this, gson.fromJson(oldValueInCurrentLanguage, field.getGenericType()));
                    } catch (Exception ex2) {
                        // Not as Json ? We can not do anything with that.
                    }
                    translationStrings.put("current_key_lang_"+field.getName(), language);
                } 
                
                
                if (!languageHasChanged && oldValueInCurrentLanguage != null && !oldValueInCurrentLanguage.equals(newValueInCurrentLanguage)) {
                    translationStrings.put(keyindex, newValueInCurrentLanguage); 
                    changed = true;
                }

                if (oldValueInCurrentLanguage == null) {
                    translationStrings.put(keyindex, newValueInCurrentLanguage); 
                    field.set(this, gson.fromJson(newValueInCurrentLanguage, field.getGenericType()));
                    changed = true;
                }
            }
        }
        
        return changed;
    }
    
    public boolean hasTranslations() {
        return !translationStrings.isEmpty();
    }
    
    public void resetLanguage() {
        translationStrings = new HashMap();
    }
   
}
