package com.thundashop.core.common;

import com.thundashop.core.productmanager.data.Product;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TranslationHelper {
    public String curLang = "";
    private String mainLang;
    private List<Object> checked = new ArrayList();
    
    public TranslationHelper(String curLang, String main_lang) {
        if(curLang == null && main_lang == null) {
            return;
        }
        if(main_lang != null && curLang == null) {
            curLang = main_lang;
        }
        this.curLang = curLang;
        this.mainLang = main_lang;
    }

    public void checkFields(Object data, boolean loading) {
        if(checked.contains(data)) {
            return;
        }
        if(!(data instanceof List)) {
            checked.add(data);
        }
        
        if(data instanceof List) {
            List tmpList = (List)data;
            for(Object tmpObj : tmpList) {
                checkFields(tmpObj, loading);
            }
        }
        if(data instanceof Map) {
            Map tmpMap = (Map)data;
            for(Object tmpObj : tmpMap.values()) {
                checkFields(tmpObj, loading);
            }
        }
        
        if(data == null || !data.getClass().getCanonicalName().startsWith("com.thundashop")) {
            return;
        }
        
        try {
            Method method = data.getClass().getMethod("updateTranslation", String.class, String.class, boolean.class);
            method.invoke(data, curLang, mainLang, loading);
        }catch(Exception e) {
            if(!(e instanceof NoSuchMethodException)) {
                e.printStackTrace();
            }
        }
        
        Field[] fields = data.getClass().getFields();
        for(Field field : fields) {
            try {
                Object val = field.get(data);
                checkFields(val, loading);
            }catch(Exception d) {}
        }

    }
}
