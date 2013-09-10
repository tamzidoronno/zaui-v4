package com.thundashop.core.productmanager.data;

import java.util.HashMap;


/**
 *
 * @author boggi
 */
public class AttributeSummary {
    //Attributevalueid, / Count
    public HashMap<String, AttributeSummaryEntry> attributeCount = new HashMap();
    private final AttributeData pool;

    public AttributeSummary(AttributeData pool) {
        this.pool = pool;
    }

    public void addToSummary(Product value) {
        if(value.attributes != null) {
            for(String id : value.attributes) {
                AttributeValue thevalue = pool.getAttributeByValueId(id);
                if(thevalue == null) {
                    continue;
                }
                AttributeSummaryEntry count = attributeCount.get(thevalue.groupName);
                if(count == null) {
                    count = new AttributeSummaryEntry();
                    count.value = thevalue;
                }
                count.increaseCount();
                attributeCount.put(id, count);
            }
        }
    }
}
