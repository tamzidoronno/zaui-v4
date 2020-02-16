
package com.thundashop.core.common;


import com.google.gson.Gson;
import com.mycompany.apigenerator.GetShopLogHandler;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    
    public void setTranslationStrings(HashMap<String, String> translations) {
        translationStrings = translations;
    }

    public String getTranslationsByKey(String key, String lang) {
        HashMap<String, String> translations = getTranslations();
        String text = "";
        if(translations.containsKey(lang + "_" +key)) {
            String translation = translations.get(lang + "_" + key);
            if(translation != null && !translation.isEmpty()) {
                Gson gson = new Gson();
                try {
                    text = gson.fromJson(translation, String.class);
                }catch(Exception e) {
                    GetShopLogHandler.logStack(e, "translationhandler");
                }
            }
        }
        return text;
    }
    
    public HashMap<String, String> getTranslations() {
        return translationStrings;
    }
    
    public void validateTranslationMatrix() {
        try {
            Set<TranslationHandler> handlers = getAllTranslationHandlers(this);
            for(TranslationHandler handler : handlers) {
                handler.checkTranslationMatrix();
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }  
    
    private Set<TranslationHandler> getAllTranslationHandlers(Object inObject) throws IllegalArgumentException, IllegalAccessException {
        Set<TranslationHandler> handlers = new HashSet();
        if(avoidCheck(inObject)) {
            return handlers;
        }
        
        if (inObject instanceof List) {
            List list = (List)inObject;
            for (Object check : list) {
                handlers.addAll(getAllTranslationHandlers(check));
            }
            return handlers;
        }
        if(inObject instanceof Map) {
            Map map = (Map)inObject;
            for(Object dobject : map.values()) {
                handlers.addAll(getAllTranslationHandlers(dobject));
            }
            return handlers;
        }
        
        Field[] fields = null;
        
        fields = inObject.getClass().getFields();
        
        if (inObject instanceof TranslationHandler) {
            handlers.add((TranslationHandler)inObject);
        }
        
        for (Field field : fields) {
            if(!field.isAccessible()) {
//                continue;
            }
            if(field.getType().isPrimitive() || field.getType().isEnum()) {
                continue;
            }
            
            if(field.getType().isAssignableFrom(BigDecimal.class)) {
                continue;
            }
            
            if(field.isAnnotationPresent(Translation.class) && !(inObject instanceof TranslationHandler)) {
                GetShopLogHandler.logPrintStatic("WARNING:::: translation annotiation added without extending translation handler, object: " + inObject.getClass().getCanonicalName(), null);
            }
            try {
                Object dataObject = field.get(inObject);

                if (dataObject instanceof TranslationHandler) {
                    handlers.add((TranslationHandler)dataObject);
                }
                if (dataObject instanceof List) {
                    List list = (List)dataObject;
                    for (Object check : list) {
                        handlers.addAll(getAllTranslationHandlers(check));
                    }
                } else if(dataObject instanceof Map) {
                    Map map = (Map)dataObject;
                    for(Object dobject : map.values()) {
                        handlers.addAll(getAllTranslationHandlers(dobject));
                    }
                } else {
                    handlers.addAll(getAllTranslationHandlers(dataObject));
                }
            }catch(Exception e) {
                return handlers;
            }

        }
        
        return handlers;
    }
    
    private boolean saveTranslationInternal(String language, boolean set) throws IllegalArgumentException, IllegalAccessException {
        if(language == null) {
            return false;
        }
        boolean saved = false;
        Set<TranslationHandler> handlers = getAllTranslationHandlers(this);
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
                    currentKeyLang = language;
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
                    translationStrings.put("current_key_lang_"+field.getName(), language);
                }

                if (oldValueInCurrentLanguage == null) {
                    translationStrings.put(keyindex, newValueInCurrentLanguage); 
                    field.set(this, gson.fromJson(newValueInCurrentLanguage, field.getGenericType()));
                    changed = true;
                    translationStrings.put("current_key_lang_"+field.getName(), language);
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

    private boolean avoidCheck(Object inObject) {
        if(inObject == null) { return true; }
        if(inObject instanceof String) { return true; }
        if(inObject instanceof Boolean) { return true; }
        if(inObject instanceof Character) { return true; }
        if(inObject instanceof Byte) { return true; }
        if(inObject instanceof Short) { return true; }
        if(inObject instanceof Integer) { return true; }
        if(inObject instanceof Long) { return true; }
        if(inObject instanceof Float) { return true; }
        if(inObject instanceof Double) { return true; }
        if(inObject instanceof Void) { return true; }
        return false;
    }

    public void updateTranslationOnAll(String language, Object result) {
        try {
            Set<TranslationHandler> allHandlers = getAllTranslationHandlers(result);
            for(TranslationHandler handle : allHandlers) {
                handle.update(language);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void checkTranslationMatrix() {
        List<String> remove = new ArrayList();
        for(String key : translationStrings.keySet()) {
            if(key.contains(" ") ||
                    key.contains("*") || 
                    key.contains("&") || 
                    key.contains(",") || 
                    key.contains(";") || 
                    key.contains("'") || 
                    key.contains("/")) {
                remove.add(key);
            }
        }
        
        for(String k : remove) {
            translationStrings.remove(k);
        }    
    }
   
}
