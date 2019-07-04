/*
 * When multilevel menues was introduced this was added as a new Menu object was made.
 * Adding Menu to EntryLists.
 *
 * @ktonder
 */
package com.thundashop.core.databasemanager.updatescripts;

import com.thundashop.core.databasemanager.UpdateScript;
import com.thundashop.core.databasemanager.UpdateScriptBase;
import com.thundashop.core.listmanager.ListManager;
import com.thundashop.core.listmanager.data.Entry;
import com.thundashop.core.storemanager.data.Store;
import com.thundashop.core.usermanager.UserManager;
import com.thundashop.core.usermanager.data.User;
import java.util.Date;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class ChangeUserLevelAccessMenuEntriesWilhelmsenHouse extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("01/09-2016");
    }
    
    @Override
    public String getId() {
        return "0d0a5d89-9f37-4575-a7c1-7e1f30d03458";
    }
    
    public static void main(String[] args) {
        new ChangeUserLevelAccessMenuEntriesWilhelmsenHouse().runSingle();
    }
    
    @Override
    public void run() {
        Store store = new Store();
        store.id = "123865ea-3232-4b3b-9136-7df23cf896c6";

        ListManager listManager = getManager(ListManager.class, store, "");
        UserManager userManager = getManager(UserManager.class, store, "");
        User loggedOn = userManager.logOn("kai@getshop.com", "g4kkg4kk");
        userManager.getSession().currentUser = loggedOn;

        List<String> lists = listManager.getLists();
        for (String listId : lists) {
            List<Entry> entries = listManager.getList(listId);
            printEntries(entries, listManager);
        }

    }

    private void printEntries(List<Entry> entries, ListManager listManager) {
        if (entries == null)
            return;
        
        for (Entry entry : entries) {
            entry.userLevel = 0;
            listManager.updateEntry(entry);
            
            printEntries(entry.subentries, listManager);
        }
    }
}
