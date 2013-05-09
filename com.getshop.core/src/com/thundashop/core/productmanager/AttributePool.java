package com.thundashop.core.productmanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.productmanager.data.AttributeGroup;
import java.util.ArrayList;
import java.util.HashMap;

public class AttributePool {

    HashMap<String, AttributeGroup> attributeGroups;

    private DatabaseSaver databaseSaver;
    private Credentials credentials;
    private String storeId;

    public void addFromDatabase(AttributeGroup group) {
        if (attributeGroups == null) {
            attributeGroups = new HashMap();
        }
        System.out.println("Adding from db: " + group.id + " - " + group.groupName);

        attributeGroups.put(group.id, group);
    }

    public void initialize(Credentials credentials, String storeId, DatabaseSaver saver) {
        System.out.println(storeId);
        this.credentials = credentials;
        this.storeId = storeId;
        this.databaseSaver = saver;
    }

    public AttributeGroup getAttributeGroup(String groupname) throws ErrorException {
        if (attributeGroups == null) {
            attributeGroups = new HashMap();
        }
        
        for (AttributeGroup group : attributeGroups.values()) {
            if (group.groupName.equalsIgnoreCase(groupname)) {
                return group;
            }
        }

        AttributeGroup group = new AttributeGroup();
        group.groupName = groupname;
        group.attributes = new ArrayList();
        group.storeId = storeId;
        
        databaseSaver.saveObject(group, credentials);
        attributeGroups.put(group.id, group);
        return group;
    }

    public String getAttribute(String groupname, String attribute) throws ErrorException {
        AttributeGroup internalAttribute = getAttributeGroup(groupname);
        attribute = attribute.trim();
        if (internalAttribute.attributes == null) {
            internalAttribute.attributes = new ArrayList();
        }

        for (String key : internalAttribute.attributes) {
            if (key.equalsIgnoreCase(attribute)) {
                return key;
            }
        }
        if(attribute.trim().length() > 0) {
            internalAttribute.attributes.add(attribute);
            databaseSaver.saveObject(internalAttribute, credentials);
        }
        return attribute;
    }

    AttributeGroup getAttributeGroupById(String groupId) {
        if(attributeGroups == null) {
            attributeGroups = new HashMap();
        }
        return attributeGroups.get(groupId);
    }

}
