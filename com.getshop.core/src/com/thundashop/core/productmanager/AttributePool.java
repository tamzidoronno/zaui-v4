package com.thundashop.core.productmanager;

import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.databasemanager.data.Credentials;
import com.thundashop.core.productmanager.data.AttributeGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AttributePool {

    HashMap<String, AttributeGroup> attributeGroups = new HashMap();

    private DatabaseSaver databaseSaver;
    private Credentials credentials;
    private String storeId;

    public HashMap<String, AttributeGroup> getAll() {
        return attributeGroups;
    }
    
    public void addFromDatabase(AttributeGroup group) {
        if (attributeGroups == null) {
            attributeGroups = new HashMap();
        }
        System.out.println("Adding from db: " + group.id + " - " + group.groupName);

        attributeGroups.put(group.id, group);
    }

    public void initialize(Credentials credentials, String storeId, DatabaseSaver saver) {
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

    void renameGroup(String oldName, String newName) throws ErrorException {
        AttributeGroup group = getAttributeGroup(oldName);
        group.groupName = newName;
        databaseSaver.saveObject(group, credentials);
    }

    void renameAttribute(String groupName, String oldAttributeName, String newAttributeName) throws ErrorException {
        oldAttributeName = getAttribute(groupName, oldAttributeName);
        newAttributeName = newAttributeName.trim();
        AttributeGroup group = getAttributeGroup(groupName);
        int index = 0;
        if(group.attributes != null) {
            for(int i = 0; i < group.attributes.size(); i++) {
                String old = group.attributes.get(i);
                if(old.equals(oldAttributeName)) {
                    index = i;
                    break;
                }
            }
            group.attributes.add(index, newAttributeName);
            group.attributes.remove(oldAttributeName);
        }
    }

    void deleteGroup(String groupName) throws ErrorException {
        AttributeGroup attribute = getAttributeGroup(groupName);
        attributeGroups.remove(attribute.id);
        databaseSaver.deleteObject(attribute, credentials);
    }

    void deleteAttribute(String groupName, String attribute) throws ErrorException {
        AttributeGroup group = getAttributeGroup(groupName);
        if(group.attributes != null) {
            group.attributes.remove(attribute);
        }
        System.out.println(group);
        if(group.attributes == null || group.attributes.isEmpty()) {
            deleteGroup(groupName);
        }
    }

    boolean attributeExists(String groupName, String newAttributeName) throws ErrorException {
        AttributeGroup group = getAttributeGroup(groupName);
        if(group.attributes != null) {
            return group.attributes.contains(newAttributeName);
        }
        return false;
    }

    void compareAndRemove(List<AttributeGroup> groups) throws ErrorException {
        HashMap<String, List<String>> toRemove = new HashMap();
        for(AttributeGroup group : attributeGroups.values()) {
            if(group.attributes != null) {
                for(String attr : group.attributes) {
                    if(!checkIfExists(groups, group.groupName, attr)) {
                        List<String> list = toRemove.get(group.groupName);
                        if(list == null) {
                            list = new ArrayList();
                        }
                        list.add(attr);
                        toRemove.put(group.groupName, list);
                    }
                }
            }
        }
        
        for(String key : toRemove.keySet()) {
            System.out.println("Removing : " + key + " " + toRemove.get(key));
            for(String value : toRemove.get(key)) {
                deleteAttribute(key, value);
            }
        }
    }

    private boolean checkIfExists(List<AttributeGroup> groups, String groupName, String value) {
        for(AttributeGroup tmpgroup : groups) {
            if(tmpgroup.groupName.equalsIgnoreCase(groupName)) {
                for(String val : tmpgroup.attributes) {
                    if(val.equalsIgnoreCase(value)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
