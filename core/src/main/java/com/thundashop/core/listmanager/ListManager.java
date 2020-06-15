package com.thundashop.core.listmanager;

import com.getshop.scope.GetShopSession;
import com.thundashop.core.common.Administrator;
import com.thundashop.core.common.AppContext;
import com.thundashop.core.common.DataCommon;
import com.thundashop.core.common.ErrorException;
import com.thundashop.core.common.ManagerBase;
import com.thundashop.core.databasemanager.data.DataRetreived;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.listmanager.data.EntryList;
import com.thundashop.core.listmanager.data.JsTreeList;
import com.thundashop.core.listmanager.data.ListType;
import com.thundashop.core.listmanager.data.Menu;
import com.thundashop.core.listmanager.data.TreeNode;
import com.thundashop.core.messagemanager.MessageManager;
import com.thundashop.core.pagemanager.GetShopModules;
import com.thundashop.core.pagemanager.PageManager;
import com.thundashop.core.pagemanager.data.Page;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author boggi
 */
@Component
@GetShopSession
public class ListManager extends ManagerBase implements IListManager {
    private Map<String, Map<String, EntryList>> moduleAllEntries = new HashMap();
    private Map<String, EntryList> allEntries = new HashMap();
    
    private Map<String, Map<String, JsTreeList>> moduleJsTreeList = new HashMap();
    private Map<String, JsTreeList> jsTreeLists = new HashMap();
    
    /**
     * String = ApplicatoinInstanceId
     * List instide is a list of all menues connected to the app.
     */
    private Map<String, Map<String, List<Menu>>> moduleMenues = new HashMap();
    private Map<String, List<Menu>> menues = new HashMap();
    
    private GetShopModules modules = new GetShopModules();
    
    private Integer currentUniqueCounter = -1;
    
    @Autowired
    private PageManager pageManager;
    
    @Autowired
    private MessageManager messageManager;
    
    @Override
    public void dataFromDatabase(DataRetreived data) {
        for (DataCommon entry : data.data) {
            if (entry instanceof EntryList) {
                EntryList listObject = (EntryList) entry;
                
                if (getAllEntriesForCurrentModule().containsKey(listObject.appId)) {
                    if (getAllEntriesForCurrentModule().get(listObject.appId).rowCreatedDate.after(listObject.rowCreatedDate)) {
                        getAllEntriesForCurrentModule().put(listObject.appId, listObject);
                    } else {
                        try {
                            deleteObject(listObject);
                        } catch (ErrorException ex) {
                            java.util.logging.Logger.getLogger(ListManager.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    getAllEntriesForCurrentModule().put(listObject.appId, listObject);
                }
            }
            
            if (entry instanceof JsTreeList) {
               JsTreeList l = (JsTreeList) entry;
                getJsTreeListCurrentModule().put(l.treeName, l);
            }
            
            if (entry instanceof Menu) {
                Menu menu = (Menu)entry;
                addMenu(menu);
            }
        }
        try {
            loadRemoteData();
        }catch(Exception e) {
            
        }
            
    }
    
    public List<String> findListIdByEntryId(String id) {
        List<String> entries = new ArrayList();
        
        for(String listId : getAllEntriesForCurrentModule().keySet()) {
            Entry entry = recursiveEntrySearch(id, getAllEntriesForCurrentModule().get(listId).entries);
            if(entry != null) {
                entries.add(listId);
            }
        }
        
        return entries;
    }

    private void saveToDatabase(EntryList list) throws ErrorException {
        list.storeId = storeId;
        saveObject(list);
    }

    private void validateEntry(Entry entry) throws ErrorException {
        if (entry.name == null || entry.name.trim().length() == 0) {
            throw new ErrorException(1000001);
        }
    }
    
    private void makeSureListExists(String listId) {
        if (getAllEntriesForCurrentModule().get(listId) == null) {
            getAllEntriesForCurrentModule().put(listId, new EntryList());
            getAllEntriesForCurrentModule().get(listId).appId = listId;
            getAllEntriesForCurrentModule().get(listId).entries = new ArrayList();
        }
        
        if(getAllEntriesForCurrentModule().get(listId).entries == null) {
            getAllEntriesForCurrentModule().get(listId).entries = new ArrayList();
        }
		
        saveList(listId);
    }
	
    private void pushToMemory(Entry entry, String listId) throws ErrorException {
        makeSureListExists(listId);

        if (entry == null) {
            return;
        }
		
        String parentListId = getListIdFromEntry(getEntry(entry.parentId));
        if (parentListId == null) {
            entry.parentId = "";
        }
        
        if ((entry.parentId == null || entry.parentId.trim().length() == 0) || getEntry(entry.parentId) == null) {
            entry.parentId = "";
            getAllEntriesForCurrentModule().get(listId).entries.add(entry);
        } else {
            addSubEntry(entry);
        }

        if (entry.id == null || entry.id.trim().length() == 0) {
            entry.id = UUID.randomUUID().toString();
        }

        if (entry.pageId == null && AppContext.storePool != null) {
            Page page = pageManager.createPage();
            
            entry.pageId = page.id;
        }

        saveList(listId);
    }

    private List<Entry> buildEntries(String listId) {
        genereateUniqueIds();

        EntryList entries = getAllEntriesForCurrentModule().get(listId);
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
        for (String key : getAllEntriesForCurrentModule().keySet()) {
            Entry entry = recursiveEntrySearch(id, getAllEntriesForCurrentModule().get(key).entries);
            if (entry != null) {
                return entry;
            }
        }
        return null;
    }
    
    @Administrator
    public Entry getEntryByPageId(String pageId) throws ErrorException {
        for (String key : getAllEntriesForCurrentModule().keySet()) {
            Entry entry = recursiveEntrySearchByPageId(pageId, getAllEntriesForCurrentModule().get(key).entries);
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
        if(!parent.subentries.contains(entry)) {
            parent.subentries.add(entry);
        }
    }

    private void saveList(String listId) throws ErrorException {
        EntryList entry = getAllEntriesForCurrentModule().get(listId);
        entry.storeId = storeId;
        getAllEntriesForCurrentModule().put(listId, entry);
        saveToDatabase(entry);
    }

    private EntryList getEntryListInternal(String appId) {
        if (getAllEntriesForCurrentModule().get(appId) == null) {
            getAllEntriesForCurrentModule().put(appId, new EntryList());
            getAllEntriesForCurrentModule().get(appId).appId = appId;
            getAllEntriesForCurrentModule().get(appId).entries = new ArrayList();
        }

        return getAllEntriesForCurrentModule().get(appId);
    }

    private void sortList(String id, String after) throws ErrorException {
        Entry toSortEntry = getEntry(id);
        Entry toSortParent = null;
        if (toSortEntry.parentId != null && toSortEntry.parentId.trim().length() > 0) {
            toSortParent = getEntry(toSortEntry.parentId);
        }

        //Now sort.
        EntryList list = getEntryListInternal(getListIdFromEntry(toSortEntry));

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
            EntryList elist = getAllEntriesForCurrentModule().get(getListIdFromEntry(toSortEntry));
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
                getEntryListInternal(listId).entries.add(toSortEntry);
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
            toRemove = getEntryListInternal(listId).entries;
        } else {
            toRemove = parent.subentries;
        }
        
        if(toRemove == null) {
            getAllEntriesForCurrentModule().get(listId).entries.remove(entry);
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
        pushToMemory(entry, listId);
        
        if(entry.subentries != null) {
            List<Entry> subentries = new ArrayList();
            for(Entry tentry : entry.subentries) {
                subentries.add(tentry);
            }
            for(Entry subentry : subentries) {
                subentry.parentId = entry.id;
                //This does not make any sense.
                addEntry(listId, subentry, entry.pageId);
            }
        }
        
        return entry;
    }
    
    @Override
    public Entry addUnsecureEntry(String listId, Entry entry) throws ErrorException {
        return addEntry("unsecure_" + listId, entry, "");
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
            if (entry == null || entry.id == null)
                continue;
            
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
        for (String key : getAllEntriesForCurrentModule().keySet()) {
            if(entry != null && entry.id != null && getAllEntriesForCurrentModule() != null && getAllEntriesForCurrentModule().get(key) != null) {
                Entry result = recursiveEntrySearch(entry.id, getAllEntriesForCurrentModule().get(key).entries);
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
        return new ArrayList<>(this.getAllEntriesForCurrentModule().keySet());
    }
    
    @Override
    public List<EntryList> getAllListsByType(String inType) {
        ListType type = ListType.valueOf(inType);
        ArrayList<EntryList> allLists = new ArrayList();
        for (EntryList list : getAllEntriesForCurrentModule().values()) {
            if (list.type != null && list.type.equals(type)) {
                allLists.add(list);
            }
        }
        return allLists;
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
        EntryList list = getEntryListInternal(toListId);
        
        if(list.extendedLists == null) {
            list.extendedLists = new ArrayList();
        }
        
        list.storeId = storeId;
        list.extendedLists.add(newListId);
        saveObject(list);
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
        EntryList list = getEntryListInternal(fromListId);
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
        
        saveObject(list);
    }

    @Override
    public List<String> getCombinedLists(String listId) throws ErrorException {
        EntryList entries = getEntryListInternal(listId);
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
        EntryList entries = getEntryListInternal(listId);
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
        EntryList entryList = getAllEntriesForCurrentModule().get(listId);
        if (entryList != null) {
            entryList.entries.clear();
        }
        
        for(Entry entry : entries) {
           addEntry(listId, entry, "");
        }
    }

    private String getPageIdByName(String name, List<Entry> entries) {
        for (Entry entry : entries) {
                
            String found = "";
            if (entry.subentries != null && !entry.subentries.isEmpty()) {
                found = getPageIdByName(name, entry.subentries);
            }
            
            if (!found.equals("")) {
                return found;
            }
            
            String entryName = makeSeoUrl(entry.name, getPrefix(entry));
            
            if (entryName != null && name != null && entryName.equals(name.toLowerCase())) {
                return entry.pageId;
            }
        }
        
        return "";
    }
    
    public String getPrefix(Entry entry) {
        String listId = getListIdFromEntry(entry);
        
        if (listId == null) {
            return "";
        }
        
        for (String appId : getMenusCurrentModule().keySet()) {
            List<Menu> all = getMenusCurrentModule().get(appId);
            for (Menu menu : all) {
                if (menu.entryListId.equals(listId)) {
                    if (all.size() < 2) {
                        return "";
                    }
                    
                    return menu.name.toLowerCase()+"_";
                }
            }
        }
        return "";
    }
    
    @Override
    public String getPageIdByName(String name) {
        String found = "";
        for (EntryList entryList : getAllEntriesForCurrentModule().values()) {
            
            if (getSession() != null && getSession().language != null) {
                entryList.updateTranslation(getSession().language);
            }
            
            if (found.equals("")) {
                found = getPageIdByName(name, entryList.entries);
            }
        }
        
        return found;
    }

    private void genereateUniqueIds() {
        List<Entry> tmpAllEntries = new ArrayList();
        HashMap<String, Boolean> count = new HashMap();
        
        for(EntryList list : getAllEntriesForCurrentModule().values()) {
            addToList(list.entries, tmpAllEntries);
        }
        
        for(Entry entry : tmpAllEntries) {
            if(count.containsKey(entry.name)) {
                count.put(entry.name, true);
            } else {
                count.put(entry.name, false);
            }
        }
        
        for(Entry entry : tmpAllEntries) {
            if(count.get(entry.name)) {
                entry.uniqueId = entry.name + "_" + entry.pageId;
            } else {
                entry.uniqueId = entry.name;
            }
        }
    }

    private void addToList(List<Entry> entries, List<Entry> tmpAllEntries) {
        for(Entry entry : entries) {
            tmpAllEntries.add(entry);
            if(entry.subentries != null && !entry.subentries.isEmpty()) {
                addToList(entry.subentries, tmpAllEntries);
            }
        }
    }

	@Override
	public void createMenuList(String menuApplicationId) throws ErrorException {
		pushToMemory(null, menuApplicationId);
		EntryList list = getAllEntriesForCurrentModule().get(menuApplicationId);
		list.type = ListType.MENU;
		list.name = "Menu "+(getAllListsByType(ListType.MENU.toString()).size() + 1);
		saveList(menuApplicationId);
	}   

    public Entry findEntryByPageId(String id) {
        return getEntryByPageId(id);
    }

    public int getHighestAccessLevel(String pageId) {
        int accessLevel = 0;
        
        for (EntryList list : getAllEntriesForCurrentModule().values()) {
            for (Entry entry : list.getAllEntriesFlatList()) {
                if (entry.pageId != null && entry.pageId.equals(pageId)) {
                    if (accessLevel < entry.userLevel) {
                        accessLevel = entry.userLevel;
                    }
                }
            }
        }
        
        return accessLevel;
    }

    @Override
    public List<Menu> getMenues(String applicationInstanceId ){
        List<Menu> retMenues = getMenusCurrentModule().get(applicationInstanceId);
        
        if (retMenues == null) {
            retMenues = new ArrayList();
            getMenusCurrentModule().put(applicationInstanceId, retMenues);
        }
        
        for (Menu menu : retMenues) {
            finalizeMenu(menu);
        } 
        
        return retMenues;
    }

    private void finalizeMenu(Menu menu) {
        menu.entryList = getEntryListInternal(menu.entryListId);
        menu.entryList.name = menu.name;
        
        if (menu != null && menu.entryList != null && menu.entryList.entries != null) {
            for (Entry entry : menu.entryList.entries) {
                askForBadge(entry);
            }    
        }
    }

    private void addMenu(Menu menu) {
        List<Menu> retMenues = getMenusCurrentModule().get(menu.appId);
        if (retMenues == null) {
            retMenues = new ArrayList();
        }
        
        retMenues.add(menu);
        getMenusCurrentModule().put(menu.appId, retMenues);
    }

    @Override
    public void saveMenu(String appId, String listId, ArrayList<Entry> entries, String name) {
        List<Menu> retMenues = getMenusCurrentModule().get(appId);
        
        if (retMenues == null) {
            retMenues = new ArrayList();
            getMenusCurrentModule().put(appId, retMenues);
        }
        
        Menu menu = retMenues.stream().filter(o -> o.entryListId.equals(listId)).findFirst().orElse(null);
        
        if (menu == null) {
            menu = new Menu();
            menu.appId = appId;
            menu.entryListId = listId;
            retMenues.add(menu);
        }
        
        menu.name = name;
        saveObject(menu);
        
        setEntries(listId, entries);
    }

    @Override
    public void deleteMenu(String appId, String listId) {
        List<Menu> retMenues = getMenusCurrentModule().get(appId);
        
        if (retMenues != null) {
            for (Menu menu : retMenues) {
                if (menu.entryListId.equals(listId)) {
                    retMenues.remove(menu);
                    deleteObject(menu);
                    return;
                }
            }
        }
        
    }

    @Override
    public void saveJsTree(String name, JsTreeList list) {
        list.treeName = name;
        saveObject(list);
        getJsTreeListCurrentModule().put(name, list);
    }

    @Override
    public JsTreeList getJsTree(String name) {
        JsTreeList res = jsTreeLists.get(name);
        if(res == null) {
            res = new JsTreeList();
        }
        return res;
    }

    private void askForBadge(Entry entry) {
        if (entry == null)
            return;
        
        if (entry.subentries != null) {
            for (Entry iEntry : entry.subentries) {
                askForBadge(iEntry);
            }
        }
        
        int badges = 0;
        
        entry.badges = badges;
    }

    public void deleteList(String listId) {
        EntryList listToDelete = getAllEntriesForCurrentModule().values()
                .stream()
                .filter(list -> list.id.equals(listId))
                .findFirst()
                .orElse(null);
        
        if (listToDelete != null) {
            deleteObject(listToDelete);
        }
    }

    @Override
    public TreeNode getJSTreeNode(String nodeId) {
        for (JsTreeList treeList : getJsTreeListCurrentModule().values()) {
            TreeNode node = treeList.getNode(nodeId);
            if (node != null)
                return node;
        }
        
        return null;
    }

    @Override
    public void askConfirmationOnEntry(String entryId, String text) {
        Entry entry = getEntry(entryId);
        messageManager.sendSms("sveve", entry.phone, text, entry.prefix);
    }

    @Override
    public void confirmEntry(String entryId) {
        Entry entry = getEntry(entryId);
        entry.confirmed = true;
        updateEntry(entry);
        
        String content = "";
        
        for(String key : entry.metaData.keySet()) {
            content += key + " : " + entry.metaData.get(key) + "<br>";
        }
        
        messageManager.sendMail(entry.emailToSendConfirmationTo, entry.emailToSendConfirmationTo, "Message entry confirmed", content, "", "");
    }
    
    private Map<String, EntryList> getAllEntriesForCurrentModule() {
        if (isCmsModule()) {
            return allEntries;
        }
        
        if (moduleAllEntries.get(getCurrentGetShopModule()) == null) {
            moduleAllEntries.put(getCurrentGetShopModule(), new HashMap());
        }
        
        return moduleAllEntries.get(getCurrentGetShopModule());
    }
    
    private void loadRemoteData() {
        modules.getModules().stream().forEach(m -> {
            databaseRemote.getAll("ListManager", "all", m.id).forEach(o -> {
                if (o instanceof EntryList) {
                    EntryList instance = (EntryList)o;
                    if (moduleAllEntries.get(instance.getshopModule) == null) {
                        moduleAllEntries.put(instance.getshopModule, new HashMap());
                    }
                    moduleAllEntries.get(instance.getshopModule).put(instance.appId, instance);
                }

                if (o instanceof JsTreeList) {
                    JsTreeList instance = (JsTreeList)o;
                    if (moduleJsTreeList.get(instance.getshopModule) == null) {
                        moduleJsTreeList.put(instance.getshopModule, new HashMap());
                    }
                    moduleJsTreeList.get(instance.getshopModule).put(instance.treeName, instance);
                }

                if (o instanceof Menu) {
                    Menu instance = (Menu)o;
                    if (moduleMenues.get(instance.getshopModule) == null) {
                        moduleMenues.put(instance.getshopModule, new HashMap());
                    }
                    if (moduleMenues.get(instance.getshopModule).get(instance.appId) == null) {
                        moduleMenues.get(instance.getshopModule).put(instance.appId, new ArrayList());
                    }
                    moduleMenues.get(instance.getshopModule).get(instance.appId).add(instance);
                }
            });
        });
    }
    
    private Map<String, JsTreeList> getJsTreeListCurrentModule() {
        if (isCmsModule()) {
            return jsTreeLists;
        }
        
        if (moduleJsTreeList.get(getCurrentGetShopModule()) == null) {
            moduleJsTreeList.put(getCurrentGetShopModule(), new HashMap());
        }
        
        return moduleJsTreeList.get(getCurrentGetShopModule());
    }
    
    private Map<String, List<Menu>> getMenusCurrentModule() {
        if (isCmsModule()) {
            return menues;
        }
        
        if (moduleMenues.get(getCurrentGetShopModule()) == null) {
            moduleMenues.put(getCurrentGetShopModule(), new HashMap());
        }
        
        return moduleMenues.get(getCurrentGetShopModule());
    }

}


