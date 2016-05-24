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
import com.thundashop.core.storemanager.data.Store;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
 *
 * @author ktonder
 */
@Component
public class DeleteC3ExcessMenuEntries extends UpdateScriptBase implements UpdateScript {

    @Override
    public Date getAddedDate() {
        return getDate("23/05-2016");
    }
    
    @Override
    public String getId() {
        return "0c0f4102-c4a2-4174-90b2-7d8912970991";
    }
    
    public static void main(String[] args) {
        new DeleteC3ExcessMenuEntries().runSingle();
    }
    
    @Override
    public void run() {
        Store store = new Store();
        store.id = "f2d0c13c-a0f7-41a7-8584-3c6fa7eb68d1";
        
        ListManager listManager = getManager(ListManager.class, store, "");
        listManager.deleteList("823e37ac-0f75-46c5-872f-3945be4d4b2e");
    }
}
