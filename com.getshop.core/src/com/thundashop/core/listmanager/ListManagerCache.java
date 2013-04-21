package com.thundashop.core.listmanager;

import com.thundashop.core.common.CachingKey;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.listmanager.data.Entry;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ListManagerCache implements IListManager {

    ListManager manager;
    private final String addr;

    public ListManagerCache(ListManager manager, String addr) {
        this.manager = manager;
        this.addr = addr;
    }
    
    
    @Override
    public List<Entry> getList(String listId) throws ErrorException {
        List<Entry> result = manager.getList(listId);
        
        CachingKey key = new CachingKey();
        
        LinkedHashMap<String,Object> keys = new LinkedHashMap();
        keys.put("listId", listId);
        key.args = keys;
        key.interfaceName = this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
        key.sessionId = "";
        key.method = "getList";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        return null;
    }

    @Override
    public List<String> getLists() {
        return null;
    }

    @Override
    public Entry addEntry(String listId, Entry entry, String parentPageId) throws ErrorException {
        getList(listId);
        updateTranslateEntries(entry.pageId);
        return null;
    }

    @Override
    public void deleteEntry(String entryId, String listId) throws ErrorException {
        getList(listId);
        Entry entry = manager.getListEntry(entryId);
        updateTranslateEntries(entry.pageId);
    }

    @Override
    public String createListId() throws ErrorException {
        return null;
    }

    @Override
    public Entry orderEntry(String id, String after, String parentId) throws ErrorException {
        List<String> lists = manager.findListIdByEntryId(id);
        for(String listId : lists) {
            getList(listId);
        }
        return null;
    }

    @Override
    public void updateEntry(Entry entry) throws ErrorException {
        List<String> lists = manager.findListIdByEntryId(entry.id);
        for(String listId : lists) {
            getList(listId);
        }
        updateTranslateEntries(entry.pageId);
    }

    @Override
    public Entry getListEntry(String id) throws ErrorException {
        return null;
    }

    @Override
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException {
        HashMap<String, String> result = manager.translateEntries(entryIds);
        
        CachingKey key = new CachingKey();
        
        LinkedHashMap<String,Object> keys = new LinkedHashMap();
        keys.put("entryIds", entryIds);
        key.args = keys;
        key.interfaceName = getInterfaceName();
        key.sessionId = "";
        key.method = "translateEntries";
        
        manager.getCacheManager().addToCache(key, result, manager.storeId, addr);
        
        return null;
    }
    
    
    public void updateTranslateEntries(String pageId) throws ErrorException {
        HashMap<CachingKey, Object> objects = manager.getCacheManager().getAllCachedObjects(manager.storeId);
        CachingKey toRemove = null;
        for(CachingKey key : objects.keySet()) {
            if(key.interfaceName.equals(getInterfaceName()) && key.method.equals("translateEntries")) {
                LinkedHashMap<String,Object> map = (LinkedHashMap<String,Object>) key.args;
                List<String> ids = (List<String>) map.get("entryIds");
                for(String tmpEntryId : ids) {
                    if(tmpEntryId.equals(pageId)) {
                        toRemove = key;
                        break;
                    }
                }
            }
        }
        
        if(toRemove != null) {
            manager.getCacheManager().removeFromCache(toRemove, manager.storeId, addr);
        }
        
    }

    private String getInterfaceName() {
        return this.getClass().getInterfaces()[0].getCanonicalName().replace("com.thundashop.", "");
    }

    @Override
    public void combineList(String toListId, String newListId) throws ErrorException {
        getList(toListId);
    }

    @Override
    public void unCombineList(String fromListId, String toRemoveId) throws ErrorException {
        getList(fromListId);
    }

    @Override
    public List<String> getCombinedLists(String listId) throws ErrorException {
        return null;
    }

    @Override
    public void clearList(String listId) throws ErrorException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
