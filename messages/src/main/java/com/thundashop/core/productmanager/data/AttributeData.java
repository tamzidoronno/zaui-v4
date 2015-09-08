package com.thundashop.core.productmanager.data;

import com.thundashop.core.common.ErrorException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttributeData {
    public HashMap<String, AttributeValue> values = new HashMap();
    
    public void addAttributeValue(AttributeValue value) {
        values.put(value.id, value);
    }
    
    public AttributeValue getAttributeValue(String valueId) {
        return values.get(valueId);
    }

    public List<AttributeValue> getAll() {
        return new ArrayList(values.values());
    }

    public void remove(String id) {
        values.remove(id);
    }

    public AttributeValue findAttributeValue(String group, String value) throws ErrorException {
        for(AttributeValue attr : values.values()) {
            if(attr.groupName.equalsIgnoreCase(group) && attr.value.equalsIgnoreCase(value)) {
                return attr;
            }
        }
        
        throw new ErrorException(1017);
    }

    public AttributeValue getAttributeByValueId(String attrid) {
        return values.get(attrid);
    }
    
}
