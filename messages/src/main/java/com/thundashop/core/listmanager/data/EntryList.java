/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thundashop.core.listmanager.data;

import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.GetShopRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author boggi
 */
@GetShopRemoteObject
public class EntryList extends DataCommon {
    public List<Entry> entries = new ArrayList();
    public String appId;
    public String name = "";
    public ListType type = null;
    public List<String> extendedLists; 
    
    public List<Entry> getAllEntriesFlatList() {
        Set<Entry> retEntries = new HashSet();
        for (Entry entry : entries) {
            retEntries.add(entry);
            retEntries.addAll(entry.getAllEntries());
        }
        
        return new ArrayList(retEntries);
    }
}
