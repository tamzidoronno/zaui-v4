package com.thundashop.core.listmanager;

import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.DatabaseSaver;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.Events;
import com.thundashop.core.common.Logger;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.listmanager.data.EntryList;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@Scope("prototype")
public class ListManager extends ManagerBase implements IListManager {
    public Map<String, EntryList> allEntries = new HashMap();
    
    @Autowired
    public ListManager(Logger log, DatabaseSaver databaseSaver) {
        super(log, databaseSaver);
        allEntries = new HashMap();
    }

    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon entry : data.data) {
            if (entry instanceof EntryList) {
                EntryList listObject = (EntryList) entry;
                
                if (allEntries.containsKey(listObject.appId)) {
                    if (allEntries.get(listObject.appId).rowCreatedDate.after(listObject.rowCreatedDate)) {
                        allEntries.put(listObject.appId, listObject);
                    } else {
                        try {
                            databaseSaver.deleteObject(listObject, credentials);
                        } catch (ErrorException ex) {
                            java.util.logging.Logger.getLogger(ListManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    allEntries.put(listObject.appId, listObject);
                }
            }
        }
    }
    
    public List<String> findListIdByEntryId(String id) {
        List<String> entries = new ArrayList();
        
        for(String listId : allEntries.keySet()) {
            Entry entry = recursiveEntrySearch(id, allEntries.get(listId).entries);
            if(entry != null) {
                entries.add(listId);
            }
        }
        
        return entries;
    }

    private void saveToDatabase(EntryList list) throws ErrorException {
        list.storeId = storeId;
        databaseSaver.saveObject(list, credentials);
    }

    private void validateEntry(Entry entry) throws ErrorException {
        if (entry.name == null || entry.name.trim().length() == 0) {
            throw new ErrorException(1000001);
        }
    }

    private void pushToMemory(Entry entry, String listId, String parentPageId) throws ErrorException {
        if (allEntries.get(listId) == null) {
            allEntries.put(listId, new EntryList());
            allEntries.get(listId).appId = listId;
            allEntries.get(listId).entries = new ArrayList();
        }
        if(allEntries.get(listId).entries == null) {
            allEntries.get(listId).entries = new ArrayList();
        }
        
        if (entry.parentId == null || entry.parentId.trim().length() == 0) {
            allEntries.get(listId).entries.add(entry);
        } else {
            addSubEntry(entry);
        }

        if (entry.id == null || entry.id.trim().length() == 0) {
            entry.id = UUID.randomUUID().toString();
        }

        PageManager myPageManager = getManager(PageManager.class);
        if (entry.pageId == null && AppContext.storePool != null) {
            Page page = myPageManager.createPage(entry.pageType, parentPageId);
            entry.pageId = page.id;
        }

        saveList(listId);
    }

    private List<Entry> buildEntries(String listId) {
        if (allEntries == null) {
            allEntries = new HashMap();
        }

        EntryList entries = allEntries.get(listId);
        if (entries == null) {
            return new ArrayList<Entry>();
        }
        if(entries.entries == null) {
            return new ArrayList();
        }
        
        unsetNull(entries.entries);
        return combineLists(entries);
    }

    private Entry getEntry(String id) throws ErrorException {
        for (String key : allEntries.keySet()) {
            Entry entry = recursiveEntrySearch(id, allEntries.get(key).entries);
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }
    
    private Entry getEntryByPageId(String pageId) throws ErrorException {
        for (String key : allEntries.keySet()) {
            Entry entry = recursiveEntrySearchByPageId(pageId, allEntries.get(key).entries);
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }

    private void addSubEntry(Entry entry) throws ErrorException {
        Entry parent = getEntry(entry.parentId);
        if (parent.subentries == null) {
            parent.subentries = new ArrayList();
        }
        parent.subentries.add(entry);
    }

    private void saveList(String listId) throws ErrorException {
        EntryList entry = allEntries.get(listId);
        entry.storeId = storeId;
        saveToDatabase(entry);
    }

    private EntryList getEntryList(String appId) {
        if (allEntries.get(appId) == null) {
            allEntries.put(appId, new EntryList());
            allEntries.get(appId).appId = appId;
            allEntries.get(appId).entries = new ArrayList();
        }

        return allEntries.get(appId);
    }

    private void sortList(String id, String after) throws ErrorException {
        Entry toSortEntry = getEntry(id);
        Entry toSortParent = null;
        if (toSortEntry.parentId != null && toSortEntry.parentId.trim().length() > 0) {
            toSortParent = getEntry(toSortEntry.parentId);
        }

        //Now sort.
        EntryList list = getEntryList(getListIdFromEntry(toSortEntry));

        List<Entry> toSortList;
        if (toSortParent != null) {
            toSortList = toSortParent.subentries;
        } else {
            toSortList = list.entries;
        }

        ArrayList<Entry> newList = new ArrayList();

        //Add it to the top if nothing has been set.
        if (after == null || after.trim().length() == 0) {
            newList.add(toSortEntry);
        }

        //Append the rest of the entries to the list.
        for (Entry entry : toSortList) {
            if (entry.id.equals(toSortEntry.id)) {
                continue;
            }

            newList.add(entry);
            if (entry.id.equals(after)) {
                //Put it after this entry.
                newList.add(toSortEntry);
            }
        }

        //Replace the old list with the new one.
        if (toSortParent != null) {
            toSortParent.subentries = newList;
        } else {
            EntryList elist = allEntries.get(getListIdFromEntry(toSortEntry));
            elist.entries = newList;
        }
    }

    private void moveEntry(String id, String parentId) throws ErrorException {
        Entry toSortEntry = getEntry(id);
        String listId = getListIdFromEntry(toSortEntry);
        if (toSortEntry == null) {
            throw new ErrorException(1000005);
        }

        if (parentId == null || parentId.trim().length() == 0) {
            parentId = "";
        }

        if (toSortEntry.parentId == null || toSortEntry.parentId.trim().length() == 0) {
            toSortEntry.parentId = "";
        }

        if (!toSortEntry.parentId.equals(parentId)) {
            //First remove it.
            removeEntry(toSortEntry.id);

            //Place it correctly.
            if (parentId.equals("")) {
                toSortEntry.parentId = "";
                getEntryList(listId).entries.add(toSortEntry);
            } else {
                Entry parent = getEntry(parentId);
                if (parent.subentries == null) {
                    parent.subentries = new ArrayList();
                }
                toSortEntry.parentId = parentId;
                parent.subentries.add(toSortEntry);
            }
        }

    }

    private void removeEntry(String id) throws ErrorException {
        List<Entry> toRemove;

        Entry entry = getEntry(id);
        String listId = getListIdFromEntry(entry);
        if (entry == null) {
            throw new ErrorException(1000004);
        }
        Entry parent = getEntry(entry.parentId);
        if (parent == null) {
            toRemove = getEntryList(listId).entries;
        } else {
            toRemove = parent.subentries;
        }
        
        if(toRemove == null) {
            allEntries.get(listId).entries.remove(entry);
        } else {
            toRemove.remove(entry);
        }

        saveList(listId);
    }

    @Override
    public List<Entry> getList(String listId) {
        return buildEntries(listId);
    }

    @Override
    public Entry addEntry(String listId, Entry entry, String parentPageId) throws ErrorException {
        if (listId == null) {
            throw new ErrorException(1002);
        }
        validateEntry(entry);
        pushToMemory(entry, listId, parentPageId);
        return entry;
    }

    @Override
    public void deleteEntry(String id, String listId) throws ErrorException {
        removeEntry(id);
    }

    @Override
    public String createListId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Entry orderEntry(String id, String after, String parentId) throws ErrorException {
        Entry entry = getEntry(id);
        if (entry == null) {
            throw new ErrorException(1000005);
        }

        validateEntry(getEntry(id));

        //First check if the entry actually exists.
        moveEntry(id, parentId);

        //Then sort it
        sortList(id, after);

        //Save updates
        saveList(getListIdFromEntry(getEntry(id)));

        return getEntry(id);
    }

    @Override
    public void updateEntry(Entry entry) throws ErrorException {
        Entry oldEntry = getEntry(entry.id);
        validateEntry(entry);

        oldEntry.name = entry.name;
        oldEntry.imageId = entry.imageId;
        oldEntry.hardLink = entry.hardLink;
        oldEntry.userLevel = entry.userLevel;
        oldEntry.navigateByPages = entry.navigateByPages;
        oldEntry.pageId = entry.pageId;
        
        saveList(getListIdFromEntry(entry));
    }

    private Entry recursiveEntrySearch(String id, List<Entry> entries) {
        if(entries == null) {
            return null;
        }
        
        for (Entry entry : entries) {
            if (entry.id.equals(id)) {
                return entry;
            }
            if (entry.subentries != null && entry.subentries.size() > 0) {
                Entry result = recursiveEntrySearch(id, entry.subentries);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
        
    
    private Entry recursiveEntrySearchByPageId(String pageId, List<Entry> entries) {
        if (entries == null)
            return null;
        
        for (Entry entry : entries) {
            if (entry.pageId != null && entry.pageId.equals(pageId)) {
                return entry;
            }
            if (entry.subentries != null && entry.subentries.size() > 0) {
                Entry result = recursiveEntrySearchByPageId(pageId, entry.subentries);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private String getListIdFromEntry(Entry entry) {
        for (String key : allEntries.keySet()) {
            if(entry != null && entry.id != null && allEntries != null && allEntries.get(key) != null) {
                Entry result = recursiveEntrySearch(entry.id, allEntries.get(key).entries);
                if (result != null) {
                    return key;
                }
            }
        }
        return null;
    }

    @Override
    public Entry getListEntry(String id) throws ErrorException {
        return getEntry(id);
    }

    @Override
    public List<String> getLists() {
        return new ArrayList<>(this.allEntries.keySet());
    }

    private void unsetNull(List<Entry> entries) {
        for(Entry entry : entries) {
            if(entry.hardLink == null) { entry.hardLink = ""; }
            if(entry.id == null) { entry.id = ""; }
            if(entry.imageId == null) { entry.imageId = ""; }
            if(entry.name == null) { entry.name = ""; }
            if(entry.pageId == null) { entry.pageId = ""; }
            if(entry.parentId == null) { entry.parentId = ""; }
            if(entry.subentries == null) { entry.subentries = new ArrayList(); }
        }
    }

    @Override
    public HashMap<String, String> translateEntries(List<String> entryIds) throws ErrorException {
        HashMap<String, String> addresses = new HashMap();
        
        for (String address : entryIds) {
            Entry entry = getEntryByPageId(address);
            if(entry != null) {
                addresses.put(address, entry.name);
            }
        }
        
        return addresses;
    }

    @Override
    public void combineList(String toListId, String newListId) throws ErrorException {
        EntryList list = getEntryList(toListId);
        
        if(list.extendedLists == null) {
            list.extendedLists = new ArrayList();
        }
        
        list.storeId = storeId;
        list.extendedLists.add(newListId);
        databaseSaver.saveObject(list, credentials);
    }

    private List<Entry> combineLists(EntryList entries) {
        if(entries.extendedLists == null) {
            return entries.entries;
        }
        
        List<Entry> newList = new ArrayList();
        
        for(Entry entry : entries.entries) {
            newList.add(entry);
        }
        
        for(String listId : entries.extendedLists) {
            if(entries.appId.equals(listId)) {
                continue;
            }
            newList.addAll(getList(listId));
        }
        
        return newList;
    }

    @Override
    public void unCombineList(String fromListId, String toRemoveId) throws ErrorException {
        EntryList list = getEntryList(fromListId);
        if(list.extendedLists == null) {
            throw new ErrorException(1000011);
        }
        
        int number = 0;
        boolean found = false;
        for(String key : list.extendedLists) {
            if(key.equals(toRemoveId)) {
                found = true;
                break;
            }
            number++;
        }
        
        if(!found) {
            throw new ErrorException(1000011);
        }
        
        list.extendedLists.remove(number);
        
        databaseSaver.saveObject(list, credentials);
    }

    @Override
    public List<String> getCombinedLists(String listId) throws ErrorException {
        EntryList entries = getEntryList(listId);
        if(entries == null) {
            throw new ErrorException(1000014);
        }
        
        if(entries.extendedLists == null) {
            return new ArrayList();
        }
        
        return entries.extendedLists;
    }

    @Override
    public void clearList(String listId) throws ErrorException {
        EntryList entries = getEntryList(listId);
        if (entries == null || entries.entries == null) {
            return;
        }
        
        List<String> ids = new ArrayList();
        for(Entry entry : entries.entries) {
            ids.add(entry.id);
        }
        for (String id : ids) {
            removeEntry(id);
        }
    }
    
    
    @Override
    public void onEvent(String eventName, String eventReferance) throws ErrorException {
        if (Events.PRODUCT_DELETED.equals(eventName))
            removeProductFromListsIfExists(eventReferance);
    }

    
     public void removeProductFromListsIfExists(String productId) throws ErrorException {
        List<String> lists = getLists();
        for(String listId : lists) {
            List<String> toDelete = new ArrayList();
            List<Entry> list = getList(listId);
            for(Entry entry : list) {
                if(entry.productId != null && entry.productId.equals(productId)) {
                    toDelete.add(entry.id);
                }
            }
            
            for(String entryId : toDelete) {
                deleteEntry(entryId, listId);
            }
        }
    }

    @Override
    public void setEntries(String listId, ArrayList<Entry> entries) throws ErrorException {
        EntryList entryList = allEntries.get(listId);
        if (entryList != null) {
            entryList.entries.clear();
        }
        
        for(Entry entry : entries) {
           addEntry(listId, entry, "");
        }
    }
}
